package org.dice_research.squirrel.predictor.impl;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.BinomialPredictor;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class BinomialPredictorEvaluation {

    /**
     * Used to initialize the object for the binomial predictor
     */
    protected BinomialPredictor predictor;

    /**
     * Indicates the path to the file containing train data.
     */
    protected String trainFilePath;

    /**
     * Indicates the name of the type which should be used as positive class while training.
     */
    protected String positiveClass;

    /**
     * Indicates the path to the file containing the test data.
     */
    String testFilePath;

    /**
     * Constructor.
     *
     * @param trainFilePath Indicates the path to the file containing train data.
     * @param positiveClass Indicates the name of the type which should be used as positive class while training.
     * @param testFilePath  Indicates the path to the file containing the test data.
     */
    public BinomialPredictorEvaluation(String trainFilePath, String positiveClass, String testFilePath) {
        this.trainFilePath = trainFilePath;
        this.positiveClass = positiveClass;
        this.testFilePath = testFilePath;

    }

    /**
     * Function to evaluate the performance of the URI predictor on a test set
     */
    public void evaluation() {
        Integer uriCount = 0;
        Integer correctCount = 0;
        double accuracy;
        Integer truePos = 0;
        Integer falsePos = 0;
        Integer falseNeg = 0;
        Integer trueNeg = 0;
        BufferedReader br = null;
        try{
            FileReader in = new FileReader(testFilePath);
            br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null){
                uriCount++;
                String[] split = line.split("," );
                URI furi = null;
                try{
                    furi = new URI(split[0].replace("\"", ""));
                }catch (URISyntaxException e) {
                    try {
                        furi = new URI("http://scoreboard.lod2.eu/data/scoreboardDataCube.rdf");
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
                CrawleableUri uri = new CrawleableUri(furi);
                this.predictor.featureHashing(uri);
                String pred = this.predictor.predict(uri);
                split[1] = split[1].replace("\"", "");
                if(split[1].equals(positiveClass)){
                    //System.out.println("the class is: " + split[1]);
                    if(pred.equals("dereferencing")){
                        correctCount ++;
                        truePos ++;
                    }
                    else{
                        falseNeg ++;
                    }
                }
                else{
                    if(!pred.equals("dereferencing")){
                        correctCount ++;
                        trueNeg ++;
                    }
                    else{
                        falsePos ++;
                    }
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        accuracy = correctCount.floatValue() / uriCount.floatValue();

        System.out.println(" The total number of URIs is: " + uriCount);
        System.out.println(" The total number of correct predictions  is: " + correctCount);
        System.out.println(" The accuracy of the predictor is: " + accuracy);
        System.out.println("True Positive is: " + truePos);
        System.out.println("False Positive is: " + falsePos);
        System.out.println("False Negative is: " + falseNeg);
        System.out.println("True Negative is: " + trueNeg);

    }

    /**
     * Function to perform K-fold cross validation.
     */
    public void crossValidation(){
        URL url = null;
        BufferedReader br = null;
        ArrayList<String> lineList = new ArrayList<String>();
        int[][] train;
        int[][] test;
        int[] index;
        String line;
        int folds = 10;
        int chunk;
        try {
            url = new URL(trainFilePath);

            br = new BufferedReader((new InputStreamReader(url.openStream())));
            line = br.readLine();
            while( ( line = br.readLine()) != null){
                lineList.add(line);
            }
            Collections.shuffle(lineList, new Random(113));
            chunk = lineList.size()/folds;
            train = new int[folds][];
            test = new int[folds][];
            index = new int[lineList.size()];
            for (int i = 0; i < lineList.size(); i++) {
                index[i] = i;
            }
            for(int i=0; i<folds; i++){
                int start = chunk * i;
                int end = chunk * (i+1);
                if(i == folds-1 )
                    end = lineList.size();
                train[i] = new int[lineList.size() - end + start];
                test[i] = new int[end - start];
                for(int j=0, p=0, q=0; j<lineList.size(); j++){
                    if(j>=start && j<end){
                        test[i][p++] = index[j];
                    }
                    else{
                        train[i][q++] = index[j];
                    }
                }
            }
            for(int i=0; i<folds; i++){
                PrintWriter writerTrain = new PrintWriter("trainFile.txt", "UTF-8");
                PrintWriter writerTest = new PrintWriter("testFile.txt", "UTF-8");
                for(int p=0; p<chunk; p++){
                    if(p == test[i].length)
                        break;
                    writerTest.println(lineList.get(test[i][p]));
                }
                for(int q=0; q<(lineList.size() - chunk); q++){
                    if(q == train[i].length)
                        break;
                    writerTrain.println(lineList.get(train[i][q]));
                }
                writerTrain.close();
                writerTest.close();
                this.predictor = new BinomialPredictor.BinomialPredictorBuilder().withFile("trainFile.txt").build();
                evaluation();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BinomialPredictorEvaluation evaluate = new BinomialPredictorEvaluation("https://hobbitdata.informatik.uni-leipzig.de/squirrel/lodstats-seeds.csv", "dereferenceable","testFile.txt");
        evaluate.crossValidation();
    }
}
