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

public final class BinomialPredictor{

    private static final Logger LOGGER = LoggerFactory.getLogger(BinomialPredictor.class);
    private WeightUpdater updater;
    private RegressionLearn learner;
    private RegressionModel model;
    private RegressionClassifier classifier;
    private String filepath;
    private double learningRate;
    private double l2;
    private double l1;
    private double beta;

    public static class BinomialPredictorBuilder {

        private TrainingDataProvider trainingDataProvider;

        protected StochasticGradientDescent sgd; //Minimizer

        private RegressionLearn learner; //Learner

        private RegressionModel model; //Model

        private WeightUpdater updater; //Updater

        private double learningRate = 0.01;//Learning rate

        private double beta;   //Beta

        private double l1;   //L1

        private double l2;  //L2

        private RegressionClassifier classifier;   //Classifier

        private String filePath;

        public BinomialPredictorBuilder(RegressionLearn learner, RegressionModel model, RegressionClassifier classifier, WeightUpdater updater) {
            this.learner = learner;
            this.model = model;
            this.classifier = classifier;
            this.updater = updater;
        }

        public BinomialPredictorBuilder() {
        }

        public BinomialPredictorBuilder withUpdater(WeightUpdater updater) {
            this.setUpdater(updater);
            return this;
        }

        public BinomialPredictorBuilder withLearner(RegressionLearn learner) {
            this.setLearner(learner);
            return this;
        }

        public BinomialPredictorBuilder withModel(RegressionModel model) {
            this.setModel(model);
            return this;
        }

        public BinomialPredictorBuilder withClassifier(RegressionClassifier regressionClassifier) {
            this.setClassifier(regressionClassifier);
            return this;
        }

        public BinomialPredictorBuilder withFile(String filepath) {
            this.setFilePath(filepath);
            return this;
        }

        public BinomialPredictorBuilder withLearningRate(Double learningRate){
            this.setLearningRate(learningRate);
            return this;
        }
        public BinomialPredictorBuilder withL1(Double L1){
            this.setL1(L1);
            return this;
        }
        public BinomialPredictorBuilder withL2(Double L2){
            this.setL2(L2);
            return this;
        }
        public BinomialPredictorBuilder withBeta(Double Beta){
            this.setBeta(Beta);
            return this;
        }

        public BinomialPredictor build() {
            BinomialPredictor predictor = new BinomialPredictor();

            if (this.getLearningRate() == 0)
                this.setLearningRate(0.7);
            predictor.setLearningRate(learningRate);

            if (this.getBeta() == 0)
                this.setBeta(1);
            predictor.setBeta(beta);

            if (this.getL1() == 0) {
                this.setL1(1);
            }
            predictor.setL1(l1);

            if (this.getL2() == 0)
                this.setL2(1);

            predictor.setL2(this.getL2());

            //updater
            if (this.getUpdater() == null) {
                this.setUpdater(new AdaptiveFTRLRegularizer(beta, l1, l2));
            }
            predictor.setUpdater(updater);

            sgd = StochasticGradientDescent.StochasticGradientDescentBuilder
                .create(learningRate) // learning rate
                .holdoutValidationPercentage(0.05d) // 5% as validation set
                .historySize(10_000) // keep 10k samples to compute relative improvement
                .weightUpdater(updater) // FTRL updater
                .progressReportInterval(1_000) // report every n iterations
                .build();

            //model
            if (this.getModel() == null)
                this.setModel(new RegressionModel());
            predictor.setModel(model);

            //classifier
            if (this.getClassifier() == null)
                if (this.getModel() != null)
                    this.setClassifier(new RegressionClassifier(this.getModel()));
            predictor.setClassifier(classifier);

            //learner
            if (this.getLearner() == null)
                this.setLearner(new RegressionLearn(sgd, new SigmoidActivationFunction(), new LogLoss()));
            predictor.setLearner(learner);

            //filepath
            if (this.getFilePath() == null)
                if (this.getModel() != null)
                    this.setFilePath(" ");
            predictor.setFilepath(this.getFilePath());

            train(filePath);

            return predictor;

        }

        private void train(String filePath) {
            learner.setNumPasses(2);
            learner.verbose();
            this.model = learner.train(() -> trainingDataProvider.setUpStream(filePath));
        }

        private RegressionLearn getLearner() {
            return learner;
        }

        private void setLearner(RegressionLearn learner) {
            this.learner = learner;
        }

        private RegressionModel getModel() {
            return model;
        }

        private void setModel(RegressionModel model) {
            this.model = model;
        }

        private RegressionClassifier getClassifier() {
            return classifier;
        }

        private void setClassifier(RegressionClassifier classifier) {
            this.classifier = classifier;
        }

        private WeightUpdater getUpdater() {
            return updater;
        }

        private void setUpdater(WeightUpdater updater) {
            this.updater = updater;
        }

        private double getLearningRate() {
            return learningRate;
        }

        private void setLearningRate(double learningRate) {
            this.learningRate = learningRate;
        }

        private double getBeta() {
            return beta;
        }

        private void setBeta(double beta) {
            this.beta = beta;
        }

        private double getL1() {
            return l1;
        }

        private void setL1(double l1) {
            this.l1 = l1;
        }

        private double getL2() {
            return l2;
        }

        private void setL2(double l2) {
            this.l2 = l2;
        }

        private String getFilePath() {
            return filePath;
        }

        private void setFilePath(String filePath) {
            this.filePath = filePath;
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
        int pred = 0;
        try {
            //Get the feature vector
            if (uri.getData(Constants.FEATURE_VECTOR) != null) {
                Object featureArray = uri.getData(Constants.FEATURE_VECTOR);
                double[] doubleFeatureArray = (double[]) featureArray;
                DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);
                //initialize the regression classifier with updated model and predict
                classifier = new RegressionClassifier(model);

                DoubleVector prediction = classifier.predict(features);

                pred = prediction.maxIndex();

            } else {
                LOGGER.info("Feature vector of this " + uri.getUri().toString() + " is null");
            }
        } catch (Exception e) {
            LOGGER.warn("Prediction for this " + uri.getUri().toString() + " failed " + e);
            pred = 0;
        }
        return pred;
    }


    public void weightUpdate(CrawleableUri curi) {
        // Learning Rate used at runtime
        double learningRate = 0.7;
        try {
            if (curi.getData(Constants.FEATURE_VECTOR) != null && curi.getData(Constants.URI_TRUE_LABEL) != null) {
                Object featureArray = curi.getData(Constants.FEATURE_VECTOR);//                DoubleVector prediction = multinomialClassifier.predict(features);
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

    public WeightUpdater getUpdater() {
        return updater;
    }

    public String getFilepath() {
        return filepath;
    }

    public double getL1Parameter() {
        return l1;
    }


    public double getL2Parameter() {
        return l2;
    }


    public double getBetaParameter() {
        return beta;
    }


    public double getLearningRate() {
        return learningRate;
    }


    protected void setUpdater(WeightUpdater updater) {
        this.updater = updater;
    }

    public RegressionLearn getLearner() {
        return learner;
    }

    protected void setLearner(RegressionLearn learner) {
        this.learner = learner;
    }

    public RegressionModel getModel() {
        return model;
    }

    protected void setModel(RegressionModel model) {
        this.model = model;
    }

    public RegressionClassifier getClassifier() {
        return classifier;
    }

    protected void setClassifier(RegressionClassifier classifier) {
        this.classifier = classifier;
    }

    protected void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    protected void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double getL2() {
        return l2;
    }

    protected void setL2(double l2) {
        this.l2 = l2;
    }

    public double getL1() {
        return l1;
    }

    protected void setL1(double l1) {
        this.l1 = l1;
    }

    public double getBeta() {
        return beta;
    }

    protected void setBeta(double beta) {
        this.beta = beta;
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
