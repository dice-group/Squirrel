package org.dice_research.squirrel.predictor.impl;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MultinomialPredictorEvaluation {
    /**
     * {@link Predictor} used to initialize the object for Predictor to access the functions in PredictorImpl class
     */
    protected Predictor predictor =  new MultinomialPredictor.MultinomialPredictorBuilder().withFile("multiNomialTrainData.txt").build();
    /**
     * Indicates the path to the file containing train data.
     */
    String trainFilePath;
    /**
     * Indicates the name of the type which should be used as positive class while training.
     */
    String positiveClass;
    /**
     * Indicates the path to the file containing the evaluation data.
     */
    String testFilePath;
    /**
     * List cf four classes of the URIs
     */
    private static final ArrayList<String> classList = new ArrayList<>();
    static{
        classList.add("SPARQL");
        classList.add("DUMP");
        classList.add("CKAN");
    }

    Integer[][] confusionMatrix = new Integer[3][3];

    /**
     * Constructor.
     *
     * @param trainFilePath
     *            Indicates the path to the file containing train data.
     * @param testFilePath
     *             Indicates the path to the file containing the test data.
     */
    public MultinomialPredictorEvaluation(String trainFilePath, String testFilePath){
        this.trainFilePath = trainFilePath;
        this.testFilePath = testFilePath;
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                confusionMatrix[i][j] = 0;
            }
        }
    }

    /**
     * Function to evaluate the performance of the URI predictor on a test set
     */
    public double evaluation() {

        Integer uriCount = 0;
        Integer correctCount = 0;
        double accuracy;
        BufferedReader br = null;
        try (FileReader in = new FileReader(testFilePath)){
            br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                uriCount ++;
                String[] split = line.split("," );
                URI furi = null;
                try {
                    furi = new URI(split[0].replace("\"", ""));
                } catch (URISyntaxException e) {
                    try {
                        furi = new URI("http://scoreboard.lod2.eu/data/scoreboardDataCube.rdf");
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                }
                CrawleableUri uri = new CrawleableUri(furi);
                predictor.featureHashing(uri);
                String pred = predictor.predict(uri);
                //System.out.println("predicted values: "+ pred);
                split[1] = split[1].replace("\"", "");
                //System.out.println("the classList index: "+classList.indexOf(split[1]));
                if(classList.indexOf(split[1]) != -1)
                    confusionMatrix[classList.indexOf(split[1])][classList.indexOf(pred)]++;
                if(pred.equals(split[1])){
                    correctCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        accuracy = correctCount.floatValue() / uriCount.floatValue();
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                System.out.print(" " +confusionMatrix[i][j]);
            }
            System.out.println();
        }
        System.out.println(" The total number of URIs is: " + uriCount);
        System.out.println(" The total number of correct predictions  is: " + correctCount);
        System.out.println(" The accuracy of the predictor is: " + accuracy);
        return accuracy;
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
            br = new BufferedReader(new InputStreamReader(new FileInputStream(trainFilePath)));
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
            for(int i=0; i<folds ; i++){
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
                //predictor.train("trainFile.txt");
//                System.out.println("calling multinomial train function");
//                predictor.multiNomialTrain("trainFile.txt");
                evaluation();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeConfusionMatrix(){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("confusionMatrix.txt"));
            bw.write("Sparql\tDump\tCkan");
            bw.newLine();
            for (int i = 0; i < confusionMatrix.length; i++) {
                for (int j = 0; j < confusionMatrix[i].length; j++) {
                    bw.write(confusionMatrix[i][j] + "\t");
                }
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {}
    }

    public static void main(String[] args) {
        MultinomialPredictorEvaluation evaluate = new MultinomialPredictorEvaluation("multiNomialTrainData", "testFile.txt");
        evaluate.crossValidation();
        evaluate.writeConfusionMatrix();
        }
}
