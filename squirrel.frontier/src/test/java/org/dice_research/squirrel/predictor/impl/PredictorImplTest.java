package org.dice_research.squirrel.predictor.impl;

import de.jungblut.math.activation.SigmoidActivationFunction;
import de.jungblut.math.dense.DenseDoubleVector;
import de.jungblut.online.regression.RegressionClassifier;
import de.jungblut.online.regression.RegressionModel;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.PredictorImpl;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class PredictorImplTest {
    private CrawleableUri curi;
    private PredictorImpl predictor;

    @Test
    public void featureHashing() throws Exception {
        int flag1 = 0;
        int flag2 = 0;
        CrawleableUri uri1 = new CrawleableUri(new URI("https://dbpedia.org/resource/New_York"));
        CrawleableUri uri2 = new CrawleableUri(new URI("https://wikipedia.org/resource/New_York"));
        CrawleableUri uri3 = new CrawleableUri(new URI("abc:///xyz/zyx/lmn.uvw"));
        PredictorImpl predictor = new PredictorImpl();
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
            if(featureArray2[i] == featureArray1[i]) {
                if(featureArray1[i] != 0.0) {
                    flag1 = 1;
                }
            }
            if(featureArray1[i] == featureArray3[i] ){
                if(featureArray1[i]!= 0.0){
                    flag2 = 1;
                }
            }
        }
        assertEquals(1, flag1);
        assertEquals(0, flag2);
    }

    @Test
    public void predict() throws Exception {

        //Initialization
        curi = new CrawleableUri(new URI("https://dbpedia.org/resource/New_York"));
        predictor = new PredictorImpl();

        // TODO weight to be intilized by the training weight
        DenseDoubleVector weights = new DenseDoubleVector(new double[] {
            -9.77964344, 1.178953672, 3.018310781554,-15.77967, 0.1795672, 2.018095854, 59.7796434436, 1.178922695672 });

        // Model and Classifier set up
        predictor.model = new RegressionModel(weights,  new SigmoidActivationFunction());
        RegressionClassifier classifier = new RegressionClassifier(predictor.model);

        // Generate feature vector
        predictor.featureHashing(curi);

        // Prediction
        double pred = predictor.predict(curi);
        assertEquals(1d, pred, 1e-4);

    }

    @Test
    public void updateWeight() throws Exception {

    }


}
