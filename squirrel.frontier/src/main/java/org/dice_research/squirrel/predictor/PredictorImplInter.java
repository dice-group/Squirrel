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
import java.util.Optional;

public class PredictorImplInter {

    private WeightUpdater updater;
    private RegressionLearn learner;
    public RegressionModel model;
    public RegressionClassifier classifier;
    private String filepath;
    private double learningRate;
    private double l2;
    private double l1;
    private double beta;

    private static final Logger LOGGER = LoggerFactory.getLogger(PredictorImpl.class);

    public static class PredictorImplBuilder {
        private String filepath;

        protected TrainingDataProvider trainingDataProvider;

        protected StochasticGradientDescent sgd; //Minimizer

        private RegressionLearn learner; //Learner

        public RegressionModel model; //Model

        public RegressionClassifier classifier;   //Classifier

        private WeightUpdater updater; //Updater

        protected double learningRate = 0.01;//Learning rate

        protected double beta;   //Beta

        protected double l1;   //L1

        protected double l2;  //L2

        private String filePath; //filepath to train

        public PredictorImplBuilder(RegressionLearn learner, RegressionModel model, RegressionClassifier classifier, WeightUpdater updater) {
            this.learner = learner;
            this.model = model;
            this.classifier = classifier;
            this.updater = updater;
        }

        public PredictorImplBuilder() {
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

        public PredictorImplBuilder withClassifier(RegressionClassifier regressionClassifier) {
            this.classifier = regressionClassifier;
            return this;
        }

        public PredictorImplBuilder withFile(String filepath) {
            this.filepath = filepath;
            return this;

        }

        public PredictorImplInter build() {
            PredictorImplInter predictor = new PredictorImplInter();


            sgd = StochasticGradientDescent.StochasticGradientDescentBuilder
                .create(learningRate) // learning rate
                .holdoutValidationPercentage(0.05d) // 5% as validation set
                .historySize(10_000) // keep 10k samples to compute relative improvement
                .weightUpdater(updater) // FTRL updater
                .progressReportInterval(1_000) // report every n iterations
                .build();

            //learner
            if (this.learner == null)
                predictor.learner = new RegressionLearn(sgd, new SigmoidActivationFunction(), new LogLoss());

            if (this.learningRate == 0)
                predictor.learningRate = 0.7;

            if (this.beta == 0)
                predictor.setBetaParameter(1);

            if (this.l1 == 0)
                predictor.setL1Parameter(1);

            if (this.l2 == 0)
                predictor.setL2Parameter(1);

            //model
            if (this.model == null)
                predictor.model = new RegressionModel();

            //classifier
            if (this.classifier == null)
                if (model != null) {
                    predictor.classifier = new RegressionClassifier(model);
                }

            //updater
            if (this.updater == null) {
                predictor.updater = new AdaptiveFTRLRegularizer(beta, l1, l2);
            }

            if (this.filepath == null) {
                predictor.filepath = " ";
            }
            train(filePath);
            return predictor;
        }

        protected void train(String filePath) {
            learner.setNumPasses(2);
            learner.verbose();
            this.model = learner.train(() -> trainingDataProvider.setUpStream(filePath));
        }
    }

    public void featureHashing(CrawleableUri uri) {
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

    public double predict(CrawleableUri uri) {
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
            }
            else {
                LOGGER.info("Feature vector of this " + uri.getUri().toString() + " is null");
            }
        } catch (Exception e) {
            LOGGER.warn("Prediction for this " + uri.getUri().toString() + " failed " + e);
            pred = 0.0;
        }
        return pred;
    }


    public void weightUpdate(CrawleableUri curi) {
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
            }
            else {
                LOGGER.info("Feature vector or true label of this " + curi.getUri().toString() + " is null");
            }
        } catch (Exception e) {
            LOGGER.info("Error while updating the weight " + e);
        }


    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setL1Parameter(double l1) {
        this.l1 = l1;
    }

    public double getL1Parameter() {
        return l1;
    }


    public void setL2Parameter(double l2) {
        this.l2 = l2;
    }


    public double getL2Parameter() {
        return l2;
    }


    public void setBetaParameter(double beta) {
        this.beta = beta;
    }

    public double getBetaParameter() {
        return beta;
    }


    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }


    public double getLearningRate() {
        return learningRate;
    }


    public ArrayList tokenCreation(CrawleableUri uri, ArrayList tokens) {

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



