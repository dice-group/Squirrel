package org.dice_research.squirrel.predictor.impl;

import de.jungblut.math.DoubleVector;
import de.jungblut.online.regression.RegressionModel;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.MultinomialPredictor;
import org.dice_research.squirrel.predictor.Predictor;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class MultinomialPredictorTest {
    private CrawleableUri curi;
    private Predictor predictor;

    @Test
    public void multiNomialTrain(){
        Integer predictedClass;
        boolean flag = true;
        predictor = new MultinomialPredictor.MultinomialPredictorBuilder().withFile("multiNomialTrainData.txt").build();
        try {
            CrawleableUri testUri1 = new CrawleableUri(new URI("http://ckan.gobex.es"));
            CrawleableUri testUri2 = new CrawleableUri(new URI("https://data.medicare.gov/api/views/rs6n-9qwg/rows.rdf?accessType=DOWNLOAD"));
            CrawleableUri testUri3 = new CrawleableUri(new URI("http://lod.euscreen.eu/sparql"));
            predictor.featureHashing(testUri1);
            predictor.featureHashing(testUri2);
            predictor.featureHashing(testUri3);
            predictedClass = predictor.predict(testUri1);
            System.out.println(predictedClass);
            if(predictedClass != 2)
                flag = false;
            predictedClass = predictor.predict(testUri2);
            System.out.println(predictedClass);
            if(predictedClass != 1)
                flag = false;
            predictedClass = predictor.predict(testUri3);
            System.out.println(predictedClass);
            if(predictedClass != 0)
                flag = false;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(flag);
    }

    @Test
    public void featureHashing(){
        int flag1 = 0;
        int flag2 = 0;
        predictor = new MultinomialPredictor.MultinomialPredictorBuilder().withFile("multiNomialTrainData.txt").build();
        try {
            CrawleableUri uri1 = new CrawleableUri(new URI("https://dbpedia.org/resource/New_York"));
            CrawleableUri uri2 = new CrawleableUri(new URI("https://wikipedia.org/resource/New_York"));
            CrawleableUri uri3 = new CrawleableUri(new URI("abc:///xyz/zyx/lmn.uvw"));
            predictor.featureHashing(uri1);
            predictor.featureHashing(uri2);
            predictor.featureHashing(uri3);
            Object feature1 = uri1.getData(Constants.FEATURE_VECTOR);
            Object feature2 = uri2.getData(Constants.FEATURE_VECTOR);
            Object feature3 = uri3.getData(Constants.FEATURE_VECTOR);
            double[] featureArray1 = ((double[]) feature1);
            double[] featureArray2 = ((double[]) feature2);
            double[] featureArray3 = ((double[]) feature3);
            for(int i=0; i<featureArray2.length; i++){
                if(featureArray2[i] == featureArray1[i] && featureArray1[i] != 0.0) {
                    flag1 = 1;
                }
                if(featureArray1[i] == featureArray3[i] && featureArray1[i]!= 0.0 ){
                    flag2 = 1;
                }
            }
            assertEquals(1, flag1);
            assertEquals(0, flag2);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void multiWeightUpdate() throws URISyntaxException {
        boolean flag = false;
        curi = new CrawleableUri(new URI("https://mcloud.de/export/datasets/037388ba-52a7-4d7e-8fbd-101a4202be7f"));
        MultinomialPredictor predictor = new MultinomialPredictor.MultinomialPredictorBuilder().build();
        int i = 0;
        DoubleVector[] modelWeights = new DoubleVector[3];
        Integer numModels = predictor.getMultinomialModel().getModels().length;
        double[][] oldWeights = new double[numModels][];
        for(RegressionModel model : predictor.getMultinomialModel().getModels()){
            modelWeights[i] = model.getWeights();
            oldWeights[i] = Arrays.copyOf(model.getWeights().toArray(), model.getWeights().getLength());
            i++;
        }
        predictor.featureHashing(curi);
        curi.addData(Constants.URI_TRUE_CLASS, 0);
        predictor.weightUpdate(curi);
        int j = 0;
        DoubleVector[] modelNewWeights = new DoubleVector[3];
        double[][] newWeights = new double[numModels][];
        for(RegressionModel model : predictor.getMultinomialModel().getModels()){
            modelNewWeights[j] = model.getWeights();
            newWeights[j] = Arrays.copyOf(model.getWeights().toArray(), model.getWeights().getLength());
            j++;
        }
        if(!oldWeights.equals(newWeights))
           flag = true;

        Assert.assertTrue(flag);
    }

}




