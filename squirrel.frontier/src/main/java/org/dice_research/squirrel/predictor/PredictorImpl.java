package org.dice_research.squirrel.predictor;

import java.io.IOException;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.activation.SigmoidActivationFunction;
import de.jungblut.math.loss.LogLoss;
import de.jungblut.online.ml.FeatureOutcomePair;
import de.jungblut.online.regularization.AdaptiveFTRLRegularizer;
import de.jungblut.online.minimizer.StochasticGradientDescent;
import de.jungblut.online.minimizer.StochasticGradientDescent.StochasticGradientDescentBuilder;
import de.jungblut.online.regression.*;

import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;


public class PredictorImpl {

    private static final double beta = 0;
    private static final double l1 = 0;
    private static final double l2 = 0;

    private FeatureOutcomePair featureOutcome;
    private DoubleVector features;

    protected CrawleableUri uri;

    protected static RegressionModel model;


    public PredictorImpl(CrawleableUri uri) { this.uri =  uri; }

    public static void main(String[] args) throws IOException {

        // TODO update the strucutre

        StochasticGradientDescent sgd = StochasticGradientDescentBuilder
            .create(0.01) // learning rate
            .holdoutValidationPercentage(0.05d) // 5% as validation set
            .historySize(10_000) // keep 10k samples to compute relative improvement
            .weightUpdater(new AdaptiveFTRLRegularizer(beta, l1, l2)) // FTRL updater
            .progressReportInterval(1_000) // report every n iterations
            .build();


        // simple regression with Sigmoid and LogLoss
        RegressionLearner learner = new RegressionLearner(sgd,
            new SigmoidActivationFunction(), new LogLoss());

        // train the model
        // TODO train the the learner
        model = new RegressionModel();

        // output the weights
        //model.getWeights().iterateNonZero().forEachRemaining(System.out::println);

    }

    public static DoubleVector predict( DoubleVector features){

        RegressionClassifier classifier = new RegressionClassifier(model);
        // add the bias to the feature and predict it
        DoubleVector prediction = classifier.predict(features);

        return prediction;
    }


}
