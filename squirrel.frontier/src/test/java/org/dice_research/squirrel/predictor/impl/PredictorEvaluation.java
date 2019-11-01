package org.dice_research.squirrel.predictor.impl;

import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class PredictorEvaluation {
    /**
     * {@link Predictor} used to initialize the object for Predictor to access the functions in PredictorImpl class
     */
    protected Predictor predictor;
    /**
     * Indicates the path to the file containing train data.
     */
    String trainfilePath;
    /**
     * Indicates the name of the type which should be used as positive class while training.
     */
    String positiveClass;
    /**
     * Indicates the path to the file containing the evaluation data.
     */
    String evalfilePath;

    /**
     * Constructor.
     *
     * @param trainfilePath
     *            Indicates the path to the file containing train data.
     * @param positiveClass
     *             Indicates the name of the type which should be used as positive class while training.
     * @param evalfilePath
     *             Indicates the path to the file containing the evaluation data.
     */
    public PredictorEvaluation(String trainfilePath, String evalfilePath, String positiveClass){
        this.trainfilePath = trainfilePath;
        this.positiveClass = positiveClass;
        this.evalfilePath = evalfilePath;
    }

    /**
     * Function to evaluate the performance of the URI predictor
     */
    public  void evaluation() {
        predictor = new PredictorImpl();

        Integer uriCount = 0;
        Integer correctCount = 0;
        double accuracy;

        predictor.train(trainfilePath);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(evalfilePath)))){
            String line;
            while ((line = br.readLine()) != null) {
                uriCount ++;
                String[] split = line.split(" " );
                URI furi = null;
                try {
                    furi = new URI(split[0]);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                CrawleableUri uri = new CrawleableUri(furi);
                predictor.featureHashing(uri);
                double pred = predictor.predict(uri);
                if(split[1].equals(positiveClass)){
                    if(pred >= 0.5){
                        correctCount ++;
                    }
                }
                else{
                    if(pred < 0.5){
                        correctCount ++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        accuracy = correctCount.floatValue() / uriCount.floatValue();
        System.out.println(" The total number of URIs is: " + uriCount);
        System.out.println(" The total number of correct predictions  is: " + correctCount);
        System.out.println(" The accuracy of the predictor is: " + accuracy);
    }

    public static void main(String[] args) {
        PredictorEvaluation evaluate = new PredictorEvaluation("https://hobbitdata.informatik.uni-leipzig.de/squirrel/lodstats-seeds.csv", "predictor/evalDataSet.txt","dereferenceable");
        evaluate.evaluation();
        }
}
