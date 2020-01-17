package org.dice_research.squirrel.predictor.impl;

import de.jungblut.math.DoubleVector;
import de.jungblut.online.regression.RegressionModel;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.FeatureVectorGenerator;
import org.dice_research.squirrel.predictor.MultinomialPredictor;
import org.dice_research.squirrel.predictor.Predictor;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;



public class MultinomialPredictorTest {
    private CrawleableUri curi;
    private Predictor predictor;
    private FeatureVectorGenerator featureGenerator = new FeatureVectorGenerator();

    @Test
    public void multiNomialTrain(){
        String predictedClass;
        boolean flag = true;
        predictor = new MultinomialPredictor.MultinomialPredictorBuilder().withFile("multiNomialTrainData.txt").build();
        try {
            CrawleableUri testUri1 = new CrawleableUri(new URI("http://ckan.gobex.es"));
            CrawleableUri testUri2 = new CrawleableUri(new URI("https://data.medicare.gov/api/views/rs6n-9qwg/rows.rdf?accessType=DOWNLOAD"));
            CrawleableUri testUri3 = new CrawleableUri(new URI("http://lod.euscreen.eu/sparql"));
            predictedClass = predictor.predict(testUri1);
            if(!predictedClass.equals("CKAN"))
                flag = false;
            predictedClass = predictor.predict(testUri2);
            if(!predictedClass.equals("DUMP"))
                flag = false;
            predictedClass = predictor.predict(testUri3);
            if(!predictedClass.equals("SPARQL"))
                flag = false;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(flag);
    }

    @Test
    public void multiWeightUpdate() throws URISyntaxException {
        boolean flag = false;
        curi = new CrawleableUri(new URI("https://mcloud.de/export/datasets/037388ba-52a7-4d7e-8fbd-101a4202be7f"));
        MultinomialPredictor predictor = new MultinomialPredictor.MultinomialPredictorBuilder().withFile("multiNomialTrainData.txt").build();
        int i = 0;
        DoubleVector[] modelWeights = new DoubleVector[3];
        Integer numModels = predictor.getMultinomialModel().getModels().length;
        double[][] oldWeights = new double[numModels][];
        for(RegressionModel model : predictor.getMultinomialModel().getModels()){
            modelWeights[i] = model.getWeights();
            oldWeights[i] = Arrays.copyOf(model.getWeights().toArray(), model.getWeights().getLength());
            i++;
        }
        featureGenerator.featureHashing(curi);
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




