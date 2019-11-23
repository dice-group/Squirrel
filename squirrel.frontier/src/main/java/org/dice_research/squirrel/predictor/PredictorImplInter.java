package org.dice_research.squirrel.predictor;


import com.google.common.hash.Hashing;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.activation.SigmoidActivationFunction;
import de.jungblut.math.dense.SingleEntryDoubleVector;
import de.jungblut.math.loss.LogLoss;
import de.jungblut.math.minimize.CostGradientTuple;
import de.jungblut.math.sparse.SequentialSparseDoubleVector;
import de.jungblut.nlp.VectorizerUtils;
import de.jungblut.online.minimizer.StochasticGradientDescent;
import de.jungblut.online.minimizer.StochasticMinimizer;
import de.jungblut.online.ml.FeatureOutcomePair;
import de.jungblut.online.regression.RegressionClassifier;
import de.jungblut.online.regression.RegressionModel;
import de.jungblut.online.regularization.AdaptiveFTRLRegularizer;
import de.jungblut.online.regularization.CostWeightTuple;
import de.jungblut.online.regularization.WeightUpdater;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;


public final class PredictorImplInter {

    public WeightUpdater updater;
    public RegressionLearn learner;
    public RegressionModel model;
    public RegressionClassifier classifier;


    private static final Logger LOGGER = LoggerFactory.getLogger(PredictorImpl.class);


    public static class PredictorImplBuilder {
        protected TrainingDataProvider trainingDataProvider;
        private String filePath;

        //Beta
        private static final double DEFAULT_BETA = 1;
        protected double beta = DEFAULT_BETA;

        //L1
        private static final double DEFAULT_L1 = 1;
        protected double l1 = DEFAULT_L1;

        //L2
        private static final double DEFAULT_L2 = 1;
        protected double l2 = DEFAULT_L2;

        //Updater
        private final WeightUpdater DEFAULT_UPDATER = new AdaptiveFTRLRegularizer(beta, l1, l2);
        protected WeightUpdater updater = DEFAULT_UPDATER;

        public WeightUpdater getUpdater() {
            return updater;
        }

        public void setUpdater(WeightUpdater updater) {
            this.updater = updater;
        }

        //Learning rate
        private final double DEFAULT_LEARNING_RATE = 0.01;
        protected double learningRate = DEFAULT_LEARNING_RATE;

        //Minimizer
        protected final StochasticGradientDescent DEFAULT_MINIMIZER = StochasticGradientDescent.StochasticGradientDescentBuilder
            .create(learningRate) // learning rate
            .holdoutValidationPercentage(0.05d) // 5% as validation set
            .historySize(10_000) // keep 10k samples to compute relative improvement
            .weightUpdater(updater) // FTRL updater
            .progressReportInterval(1_000) // report every n iterations
            .build();
        protected StochasticMinimizer sgd = DEFAULT_MINIMIZER;

        public StochasticGradientDescent getDEFAULT_MINIMIZER() {
            return DEFAULT_MINIMIZER;
        }

        //Learner
        private final RegressionLearn DEFAULT_LEARNER =new RegressionLearn(sgd, new SigmoidActivationFunction(), new LogLoss());
        private RegressionLearn learner = DEFAULT_LEARNER;            ;

        public RegressionLearn getLearner() {
            return learner;
        }

        public void setLearner(RegressionLearn learner) {
            this.learner = learner;
        }

        //Model
        public RegressionModel model;

        public RegressionModel getModel() {
            return model;
        }

        public void setModel(RegressionModel model) {
            this.model = model;
        }

        //Classifier
        public RegressionClassifier classifier;

        public RegressionClassifier getClassifier() {
            return classifier;
        }

        public void setClassifier(RegressionClassifier classifier) {
            this.classifier = classifier;
        }


        public PredictorImplBuilder(String filepath) {
            this.filePath = filepath;
        }

        public PredictorImplBuilder withUpdater(WeightUpdater updater) {
            this.updater = updater;
            return this;
        }

        public PredictorImplBuilder withLearner(RegressionLearn learner) {
            this.learner = learner;
            return this;
        }

        public PredictorImplBuilder withModel(RegressionModel model) {
            this.model = model;
            return this;
        }

        public PredictorImpl build() {
            PredictorImpl predictor = new PredictorImpl();
            predictor.learner = this.learner;
            predictor.updater = this.updater;
            predictor.model = this.model;
            model = learner.train(() -> trainingDataProvider.setUpStream(filePath));
            return predictor;

        }

        public void train(String filePath) {
            learner.setNumPasses(2);
            learner.verbose();
            model = learner.train(() -> trainingDataProvider.setUpStream(filePath));
        }

    }

    public void featureHashing (CrawleableUri uri){
        ArrayList<String> tokens1 = new ArrayList<String>();
        tokens1 = tokenCreation(uri, tokens1);
        CrawleableUri referUri;
        if (uri.getData(Constants.REFERRING_URI) != null) {
            referUri = new CrawleableUri((URI) uri.getData(Constants.REFERRING_URI));
            if (referUri != null)
                tokens1 = tokenCreation(referUri, tokens1);
        }
        String[] tokens = new String[tokens1.size()];
        for (int i = 0; i < tokens1.size(); i++) {
            tokens[i] = tokens1.get(i);
        }

        try {
            DoubleVector feature = VectorizerUtils.sparseHashVectorize(tokens, Hashing.murmur3_128(), () -> new SequentialSparseDoubleVector(
                2 << 14));
            double[] d;
            d = feature.toArray();
            uri.addData(Constants.FEATURE_VECTOR, d);

        } catch (Exception e) {
            LOGGER.info("Exception caused while adding the feature vector to the URI map" + e);
        }

    }

    public double predict (CrawleableUri uri){
        double pred = 0.0;
        try {
            //Get the feature vector
            if (uri.getData(Constants.FEATURE_VECTOR) != null) {
                Object featureArray = uri.getData(Constants.FEATURE_VECTOR);
                double[] doubleFeatureArray = (double[]) featureArray;
                DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);
                //initialize the regression classifier with updated model and predict
                classifier = new RegressionClassifier(model);
                DoubleVector prediction = classifier.predict(features);
                double[] predictVal = prediction.toArray();
                pred = predictVal[0];
            } else {
                LOGGER.info("Feature vector of this " + uri.getUri().toString() + " is null");
            }
        } catch (Exception e) {
            LOGGER.warn("Prediction for this " + uri.getUri().toString() + " failed " + e);
            pred = 0.0;
        }
        return pred;
    }


    public void weightUpdate (CrawleableUri curi){
        // Learning Rate used at runtime
        double learningRate = 0.7;
        try {
            if (curi.getData(Constants.FEATURE_VECTOR) != null && curi.getData(Constants.URI_TRUE_LABEL) != null) {
                Object featureArray = curi.getData(Constants.FEATURE_VECTOR);
                double[] doubleFeatureArray = (double[]) featureArray;
                DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);

                Object real_value = curi.getData(Constants.URI_TRUE_LABEL);
                int rv = (int) real_value;
                DoubleVector rv_DoubleVector = new SingleEntryDoubleVector(rv);

                DoubleVector nextExample = features;
                FeatureOutcomePair realResult = new FeatureOutcomePair(nextExample, rv_DoubleVector); // real outcome
                CostGradientTuple observed = this.learner.observeExample(realResult, this.model.getWeights());
                // calculate new weights (note that the iteration count is not used)
                CostWeightTuple update = this.updater.computeNewWeights(this.model.getWeights(), observed.getGradient(), learningRate, 0, observed.getCost());

                //update weights using the updated parameters
                DoubleVector new_weights = this.updater.prePredictionWeightUpdate(realResult, update.getWeight(), learningRate, 0);


                // update model and classifier
                //this.model = new RegressionModel(new_weights, this.model.getActivationFunction());
                model = new RegressionModel(new_weights, model.getActivationFunction());
            } else {
                LOGGER.info("Feature vector or true label of this " + curi.getUri().toString() + " is null");
            }
        } catch (Exception e) {
            LOGGER.info("Error while updating the weight " + e);
        }


    }


    public ArrayList tokenCreation(CrawleableUri uri, ArrayList tokens){

        //String authority, scheme, host, path, query;
        //URI furi = null;
        String[] uriToken;
        uriToken = uri.getUri().toString().split("/|\\.");
        tokens.addAll(Arrays.asList(uriToken));
        /*try {
            furi = new URI(uri.getUri().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (furi != null) {
            authority = furi.getAuthority();
            if(authority != null)
                tokens.add(authority);
            scheme = furi.getScheme();
            if(scheme != null)
                tokens.add(scheme);

            host = furi.getHost();
            if(host != null)
                tokens.add(host);
            path = furi.getPath();

            if(path != null) ;
                tokens.add(path);
            query = furi.getQuery();
            if(query != null)
                tokens.add(query);

        }*/
        return tokens;
    }

}

//
//    public PredictorImpl(PredictorBuilderImpl builder){
//    }package org.dice_research.squirrel.predictor;





