package org.dice_research.squirrel.predictor.impl;

import de.jungblut.math.DoubleVector;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.BinomialPredictor;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class BinomialPredictorTest {
    private CrawleableUri curi;
    private BinomialPredictor predictor;

    @Test
    public void binomialTrain(){
        boolean flag = true;
        Double prediction;
        predictor = new BinomialPredictor.BinomialPredictorBuilder().withFile("https://hobbitdata.informatik.uni-leipzig.de/squirrel/lodstats-seeds.csv").build();
        try {
            CrawleableUri curiPos = new CrawleableUri(new URI("https://mcloud.de/export/datasets/037388ba-52a7-4d7e-8fbd-101a4202be7f"));
            CrawleableUri curiNeg = new CrawleableUri(new URI("https://ckan.govdata.de"));
            predictor.featureHashing(curiPos);
            predictor.featureHashing(curiNeg);
            prediction = predictor.predict(curiPos);
            if(prediction <= 0.5){
                flag = false;
            }
            prediction = predictor.predict(curiNeg);
            if(prediction > 0.5){
                flag = false;
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Assert.assertTrue(flag);

    }

    @Test
    public void featureHashing(){
        int flag1 = 0;
        int flag2 = 0;
        predictor = new BinomialPredictor.BinomialPredictorBuilder().withFile("https://hobbitdata.informatik.uni-leipzig.de/squirrel/lodstats-seeds.csv").build();
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
    public void weightUpdate() throws URISyntaxException {
        boolean flag = false;
        curi = new CrawleableUri(new URI("https://mcloud.de/export/datasets/037388ba-52a7-4d7e-8fbd-101a4202be7f"));
        predictor = new BinomialPredictor.BinomialPredictorBuilder().withFile("https://hobbitdata.informatik.uni-leipzig.de/squirrel/lodstats-seeds.csv").build();
        DoubleVector modelWeights = predictor.getModel().getWeights();
        predictor.featureHashing(curi);
        curi.addData(Constants.URI_TRUE_CLASS, 0);
        predictor.weightUpdate(curi);
        DoubleVector modelNewWeights = predictor.getModel().getWeights();
        if(!modelNewWeights.equals(modelWeights)){
            flag = true;
        }
        Assert.assertTrue(flag);
    }

}
