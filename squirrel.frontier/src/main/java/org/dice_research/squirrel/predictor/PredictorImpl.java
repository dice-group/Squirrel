package org.dice_research.squirrel.predictor;


import com.google.common.hash.Hashing;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.activation.SigmoidActivationFunction;
import de.jungblut.math.dense.DenseDoubleVector;
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
import java.util.ArrayList;


public final class PredictorImpl implements Predictor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PredictorImpl.class);
    private static final double beta = 0;
    private static final double l1 = 0;
    private static final double l2 = 0;

    private FeatureOutcomePair featureOutcome;
    private DoubleVector features;

    protected CrawleableUri uri;

    public RegressionModel model;
    public RegressionClassifier classifier;
    // TODO weights to be initialised by the trainer
    public DoubleVector weights = new DenseDoubleVector(new double[]{1, 2, 3, 4, 5, 6, 7, 3, 2, 1});

    @Override
    public void featureHashing(CrawleableUri uri)  {
        ArrayList<String> tokens1 = new ArrayList<String>();
        tokens1 = tokenCreation(uri, tokens1);
        CrawleableUri referUri;
        if(uri.getData(Constants.REFERRING_URI) != null) {
            referUri = new CrawleableUri((URI) uri.getData(Constants.REFERRING_URI));
            if (referUri != null)
                tokens1 = tokenCreation(referUri, tokens1);
        }
        String[] tokens = new String[tokens1.size()];
        for(int i =0; i<tokens1.size(); i++){
            tokens[i] = tokens1.get(i);
        }

        try {
            DoubleVector feature = VectorizerUtils.sparseHashVectorize(tokens, Hashing.murmur3_128(), () -> new SequentialSparseDoubleVector(
                2<<2));
            double[] d;
            d = feature.toArray();
            uri.addData(Constants.FEATURE_VECTOR, d);
        }catch (Exception e){
            LOGGER.info("Exception caused while adding the feature vector to the URI map"+e);
        }

    }

    public ArrayList tokenCreation(CrawleableUri uri, ArrayList tokens){

        String authority, scheme, userInfo, host, path, query, fragment;
        URI furi = null;
        try {
            furi = new URI(uri.getUri().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (furi != null) {
            authority = furi.getAuthority();
            if(authority == null) authority = "aaaa";
            tokens.add(authority);
            scheme = furi.getScheme();
            if(scheme == null) scheme = "ssss";
            tokens.add(scheme);
            userInfo = furi.getUserInfo();
            if(userInfo == null) userInfo = "uuuu";
            tokens.add(userInfo);
            host = furi.getHost();
            if(host == null) host = "hhhh";
            tokens.add(host);
            path = furi.getPath();
            if(path == null) path = "pppp";
            tokens.add(path);
            query = furi.getQuery();
            if(query == null) query = "qqqq";
            tokens.add(query);
            fragment = furi.getFragment();
            if(fragment == null) fragment = "ffff";
            tokens.add(fragment);
        }
        return tokens;
    }



    @Override
    public void train (){

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
        model = learner.train(null);

        // output the weights
        //model.getWeights().iterateNonZero().forEachRemaining(System.out::println);

    }

    @Override
    public double predict(CrawleableUri uri) {
        double pred = 0.0;
        try {
            //Get the feature vector
            if (uri.getData(Constants.FEATURE_VECTOR) != null) {
                Object featureArray = uri.getData(Constants.FEATURE_VECTOR);
                double[] doubleFeatureArray = (double[]) featureArray;
                DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);

                classifier = new RegressionClassifier(model);
                // add the bias to the feature and predict it
                DoubleVector prediction = classifier.predict(features);
                double[] predictVal = prediction.toArray();
                pred = predictVal[0];
            }else {
                LOGGER.info("Feature vector of this "+ uri.getUri().toString() +" is null");
            }
        } catch (Exception e) {
            LOGGER.warn("Prediction for this "+ uri.getUri().toString() +" failed " + e);
            pred = 0.0;
        }
        return  pred ;
    }

    @Override
    public void weightUpdate(CrawleableUri uri) {

    }


}
