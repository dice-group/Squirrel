package org.dice_research.squirrel.predictor.impl;

import de.jungblut.math.activation.SigmoidActivationFunction;
import de.jungblut.math.dense.DenseDoubleVector;
import de.jungblut.online.regression.RegressionClassifier;
import de.jungblut.online.regression.RegressionModel;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.PredictorImpl;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class PredictorImplTest {
    private CrawleableUri curi;
    private PredictorImpl predictor;

    @Test
    public void featureHashing() throws Exception {

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
        Assert.assertEquals(1d, pred, 1e-4);

    }

    @Test
    public void updateWeight() throws Exception {

    }


}
