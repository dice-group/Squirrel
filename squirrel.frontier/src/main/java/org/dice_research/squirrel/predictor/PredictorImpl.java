package org.dice_research.squirrel.predictor;

import java.io.IOException;

import com.google.common.hash.Hashing;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.activation.SigmoidActivationFunction;
import de.jungblut.math.loss.LogLoss;
import de.jungblut.math.sparse.SequentialSparseDoubleVector;
import de.jungblut.nlp.VectorizerUtils;
import de.jungblut.online.ml.FeatureOutcomePair;
import de.jungblut.online.regularization.AdaptiveFTRLRegularizer;
import de.jungblut.online.minimizer.StochasticGradientDescent;
import de.jungblut.online.minimizer.StochasticGradientDescent.StochasticGradientDescentBuilder;
import de.jungblut.online.regression.*;

import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.*;


public class PredictorImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(PredictorImpl.class);
    private static final double beta = 0;
    private static final double l1 = 0;
    private static final double l2 = 0;

    private FeatureOutcomePair featureOutcome;
    private DoubleVector features;

    protected CrawleableUri uri;

    protected static RegressionModel model;


    public PredictorImpl(CrawleableUri uri) { this.uri =  uri; }

    public void FeatureHashing(CrawleableUri uri)  {


        String[] tokens = new String[7];
        URI furi = null;
        try {
            furi = new URI(uri.getUri().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (furi != null) {
            String authority = furi.getAuthority();
            if(authority == null) authority = "aaaa";
            tokens[0] = authority;


            String scheme = furi.getScheme();
            if(scheme == null) scheme = "ssss";
            tokens[1] = scheme;


            String userInfo = furi.getUserInfo();
            if(userInfo == null) userInfo = "uuuu";
            tokens[2] = userInfo;


            String host = furi.getHost();
            if(host == null) host = "hhhh";
            tokens[3] = host;


            String path = furi.getPath();
            if(path == null) path = "pppp";
            tokens[4] = path;


            String query = furi.getQuery();
            if(query == null) query = "qqqq";
            tokens[5] = query;


            String fragment = furi.getFragment();
            if(fragment == null) fragment = "ffff";
            tokens[6] = fragment;

        }

        try {
            DoubleVector feature = VectorizerUtils.sparseHashVectorize(tokens, Hashing.murmur3_128(), () -> new SequentialSparseDoubleVector(
                2 << 7));
            double[] d;
            d = feature.toArray();
            uri.addData(Constants.FEATURE_VECTOR, d);
        }catch (Exception e){
            LOGGER.info("Exception caused while adding the feature vector to the URI map"+e);
        }


    }

    public static void main(String[] args) throws IOException {

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

    public void predict(CrawleableUri uri) {
        try {
            Object featureArray = uri.getData(Constants.FEATURE_VECTOR);
            double[] doubleFeatureArray = (double[]) featureArray;
            DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);
            RegressionClassifier classifier = new RegressionClassifier(model);
            // add the bias to the feature and predict it
            DoubleVector prediction = classifier.predict(features);
            double[] predictVal = prediction.toArray();
            uri.addData(Constants.URI_PREDICTED_LABEL, predictVal[0]);

        } catch (Exception e) {
            LOGGER.warn("Prediction for "+ uri.getUri().toString() +" failed " + e);

        }
    }


}
