package org.dice_research.squirrel.predictor.impl;


import de.jungblut.math.DoubleVector;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.BinomialPredictor;
import org.dice_research.squirrel.predictor.FeatureVectorGenerator;
import org.dice_research.squirrel.predictor.Predictor;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class BinomialPredictorTest {
    private Predictor predictor;
    private FeatureVectorGenerator featureGenerator = new FeatureVectorGenerator();

    @Test
    public void binomialTrain(){
        boolean flag = true;
        String prediction;
        predictor = new BinomialPredictor.BinomialPredictorBuilder().withFile("binomialTrainData.txt").withThreshold(0.8).withPositiveClass("dereferenceable").build();
        try {
            CrawleableUri curiPos = new CrawleableUri(new URI("https://data.cityofnewyork.us/api/views/qqsi-vm9f/rows.rdf?accessType=DOWNLOAD"));
            CrawleableUri curiNeg = new CrawleableUri(new URI("1234567!!!!!!!*****"));
            prediction = predictor.predict(curiPos);
            if(!prediction.equals("dereferenceable")){
                flag = false;
            }
            prediction = predictor.predict(curiNeg);
            if(prediction.equals("dereferenceable")){
                flag = false;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(flag);

    }

    @Test
    public void weightUpdate() throws URISyntaxException {
        boolean flag = false;
        CrawleableUri curi = new CrawleableUri(new URI("https://mcloud.de/export/datasets/037388ba-52a7-4d7e-8fbd-101a4202be7f"));
        BinomialPredictor predictor = new BinomialPredictor.BinomialPredictorBuilder().withFile("binomialTrainData.txt").withPositiveClass("dereferencing").build();
        DoubleVector modelWeights = predictor.getModel().getWeights();
        double[] oldWeights = Arrays.copyOf(modelWeights.toArray(), modelWeights.getLength());
        featureGenerator.featureHashing(curi);
        curi.addData(Constants.URI_TRUE_LABEL, 0);
        predictor.weightUpdate(curi);
        DoubleVector modelNewWeights = predictor.getModel().getWeights();
        double[] newWeights = Arrays.copyOf(modelNewWeights.toArray(), modelNewWeights.getLength());
        if(!oldWeights.equals(newWeights)){
            flag = true;
        }
        Assert.assertTrue(flag);
    }
}
