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
import de.jungblut.online.regression.RegressionLearner;
import de.jungblut.online.regression.RegressionModel;
import de.jungblut.online.regression.multinomial.MultinomialRegressionClassifier;
import de.jungblut.online.regression.multinomial.MultinomialRegressionLearner;
import de.jungblut.online.regression.multinomial.MultinomialRegressionModel;
import de.jungblut.online.regularization.AdaptiveFTRLRegularizer;
import de.jungblut.online.regularization.CostWeightTuple;
import de.jungblut.online.regularization.L2Regularizer;
import de.jungblut.online.regularization.WeightUpdater;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.IntFunction;

public final class MultinomialPredictor {

    public static final Logger LOGGER = LoggerFactory.getLogger(MultinomialPredictor.class);

    private MultinomialRegressionModel multinomialModel;
    private MultinomialRegressionLearner multinomialLearner;
    private MultinomialRegressionClassifier multinomialClassifier;
    private WeightUpdater updater;
    private RegressionLearn learner;
    private String filepath;
    private Double learningRate;
    private Double l2;
    private Double l1;
    private Double beta;
    private Double holdoutValidationPercentage; //Validation percentage which is between 0 and 1

    public static class MultinomialPredictorBuilder {

        private TrainingDataProvider trainingDataProvider = new TrainingDataProviderImpl(); //Training Data Provider

        protected StochasticGradientDescent sgd; //Minimizer

        private RegressionLearn learner; //Learner

        private WeightUpdater updater; //Updater

        private MultinomialRegressionLearner multinomialLearner; //Multinomial learner

        private MultinomialRegressionModel multinomialModel; //Multinomial odel

        private MultinomialRegressionClassifier multinomialClassifier; //Multinomial Classifier

        private Double learningRate; //Learning rate

        private Double beta;   //Beta

        private Double l1;   //L1

        private Double l2;  //L2

        private Double holdoutValidationPercentage; //Validation percentage which is between 0 and 1

        private String filePath; //filepath to train

        public MultinomialPredictorBuilder(MultinomialRegressionLearner learner, MultinomialRegressionModel model, MultinomialRegressionClassifier classifier, WeightUpdater updater) {
            this.multinomialLearner = learner;
            this.multinomialModel = model;
            this.multinomialClassifier = classifier;
            this.updater = updater;
        }

        public MultinomialPredictorBuilder() {
        }

        public MultinomialPredictorBuilder withUpdater(WeightUpdater updater) {
            this.setUpdater(updater);
            return this;
        }

        public MultinomialPredictorBuilder withLearner(MultinomialRegressionLearner multinomialLearner) {
            this.setMultinomialLearner(multinomialLearner);
            return this;
        }

        public MultinomialPredictorBuilder withModel(MultinomialRegressionModel multinomialModel) {
            this.setMultinomialModel(multinomialModel);
            return this;
        }

        public MultinomialPredictorBuilder withClassifier(MultinomialRegressionClassifier multinomialClassifier) {
            this.setMultinomialRegressionClassifier(multinomialClassifier);
            return this;
        }

        public MultinomialPredictorBuilder withFile(String filepath) {
            this.setFilePath(filepath);
            return this;

        }

        public MultinomialPredictorBuilder withLearningRate(Double learningRate) {
            this.setLearningRate(learningRate);
            return this;
        }

        public MultinomialPredictorBuilder withL1(Double L1) {
            this.setL1(L1);
            return this;
        }

        public MultinomialPredictorBuilder withL2(Double L2) {
            this.setL2(L2);
            return this;
        }

        public MultinomialPredictorBuilder withBeta(Double Beta) {
            this.setBeta(Beta);
            return this;
        }

        IntFunction<RegressionLearner> factory = (i) -> {
            // take care of not sharing any state from the outside, since classes are trained in parallel
            StochasticGradientDescent minimizer = StochasticGradientDescent.StochasticGradientDescentBuilder
                .create(0.01)
                .holdoutValidationPercentage(0.1d)
                .weightUpdater(new L2Regularizer(0.1))
                .progressReportInterval(1_000)
                .build();
            RegressionLearner learner =  new RegressionLearner(minimizer,
                new SigmoidActivationFunction(), new LogLoss());
            learner.setNumPasses(5);
            learner.verbose();
            return learner;
        };

        public MultinomialPredictor build() {
            MultinomialPredictor predictor = new MultinomialPredictor();

            //Learning Rate
            if (this.getLearningRate() == null)
                this.setLearningRate(0.7);
            predictor.setLearningRate(this.getLearningRate());

            //Beta
            if (this.getBeta() == null)
                this.setBeta(1);
            predictor.setBeta(this.getBeta());

            //L1
            if (this.getL1() == null)
                this.setL1(1);
            predictor.setL1(this.getL1());

            //L2
            if (this.getL2() == null)
                this.setL2(1);
            predictor.setL2(this.getL2());

            //updater
            if (this.getUpdater() == null) {
                this.setUpdater(new AdaptiveFTRLRegularizer(this.getBeta(), this.getL1(), this.getL2()));
            }
            predictor.setUpdater(this.getUpdater());

            //holdout validation percentage
            if (this.getHoldoutValidationPercentage() == null) {
                this.setHoldoutValidationPercentage(0.05d);
            }
            predictor.setHoldoutValidationPercentage(this.getHoldoutValidationPercentage());

            if (this.getFilePath() == null)
                this.setFilePath("multiNomialTrainData.txt");
            predictor.setFilepath(this.getFilePath());

            sgd = StochasticGradientDescent.StochasticGradientDescentBuilder
                .create(this.getLearningRate()) // learning rate
                .holdoutValidationPercentage(this.getHoldoutValidationPercentage())// 5% as validation set
                .historySize(10_000) // keep 10k samples to compute relative improvement
                .weightUpdater(this.getUpdater()) // FTRL updater
                .progressReportInterval(1_000) // report every n iterations
                .build();

            //regression learner
            if (this.getLearner() == null)
                this.setLearner(new RegressionLearn(sgd, new SigmoidActivationFunction(), new LogLoss()));
            predictor.setLearner(this.getLearner());

            //multinomial learner
            if (this.getMultinomialLearner() == null)
                this.setMultinomialLearner(new MultinomialRegressionLearner(factory));
            predictor.setMultinomialLearner(this.getMultinomialLearner());


            //model
            if (this.getMultinomialModel() == null)
                this.setMultinomialModel(multinomialLearner.train(() -> trainingDataProvider.setUpStream(this.getFilePath())));
            predictor.setMultinomialModel(this.getMultinomialModel());

            //classifier
            if (this.getMultinomialClassifier() == null)
                if (this.getMultinomialModel() != null)
                    this.setMultinomialRegressionClassifier(new MultinomialRegressionClassifier(this.getMultinomialModel()));
            predictor.setMultinomialClassifier(this.getMultinomialClassifier());

            return predictor;
        }

        /*private void train(String filepath) {
            this.multinomialLearner.verbose();
            this.multinomialModel = multinomialLearner.train(() -> trainingDataProvider.setUpStream(filepath));
        }*/

        //Learner
        private RegressionLearn getLearner() {
            return learner;
        }

        private void setLearner(RegressionLearn regressionLearn) {
            this.learner = regressionLearn;
        }

        //Updater
        private WeightUpdater getUpdater() {
            return updater;
        }

        private void setUpdater(WeightUpdater updater) {
            this.updater = updater;
        }

        //Multinomial Model
        private MultinomialRegressionModel getMultinomialModel() {
            return multinomialModel;
        }

        private void setMultinomialModel(MultinomialRegressionModel multinomialRegressionModel) {
            this.multinomialModel = multinomialRegressionModel;
        }

        //Multinomial Classifier
        private MultinomialRegressionClassifier getMultinomialClassifier() {
            return multinomialClassifier;
        }

        private void setMultinomialRegressionClassifier(MultinomialRegressionClassifier multinomialRegressionClassifier) {
            this.multinomialClassifier = multinomialRegressionClassifier;
        }

        //Multinomial Regression Learner
        private MultinomialRegressionLearner getMultinomialLearner() {
            return multinomialLearner;
        }

        private void setMultinomialLearner(MultinomialRegressionLearner multinomialRegressionLearner) {
            this.multinomialLearner = multinomialRegressionLearner;
        }

        public Double getLearningRate() {
            return learningRate;
        }

        private void setLearningRate(double learningRate) {
            this.learningRate = learningRate;
        }

        private Double getBeta() {
            return beta;
        }

        private void setBeta(double beta) {
            this.beta = beta;
        }

        private Double getL1() {
            return l1;
        }

        private void setL1(double l1) {
            this.l1 = l1;
        }

        private Double getL2() {
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

        private Double getHoldoutValidationPercentage() {
            return holdoutValidationPercentage;
        }

        private void setHoldoutValidationPercentage(Double holdoutValidationPercentage) {
            this.holdoutValidationPercentage = holdoutValidationPercentage;
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
                multinomialClassifier = new MultinomialRegressionClassifier(multinomialModel);
                DoubleVector prediction = multinomialClassifier.predict(features);
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


    public void weightUpdate(CrawleableUri uri) {

        double learningRate = 0.7;
        RegressionModel[] newModels = new RegressionModel[this.getMultinomialModel().getModels().length];
        int i=0;
        if (uri.getData(Constants.FEATURE_VECTOR) != null && uri.getData(Constants.URI_TRUE_CLASS) != null) {

            for (RegressionModel s : this.getMultinomialModel().getModels()) {
                Object featureArray = uri.getData(Constants.FEATURE_VECTOR);
                double[] doubleFeatureArray = (double[]) featureArray;
                DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);

                Object real_value = uri.getData(Constants.URI_TRUE_CLASS);

                int rv = (int) real_value;
                DoubleVector zero = new SingleEntryDoubleVector(0); //To initialize perCoordinateWeights and squaredPreviousGradient

                DoubleVector old_weights = this.updater.prePredictionWeightUpdate( new FeatureOutcomePair(zero,zero), s.getWeights(),learningRate,0);

                DoubleVector rv_DoubleVector = new SingleEntryDoubleVector(rv);

                DoubleVector nextExample = features;

                FeatureOutcomePair realResult = new FeatureOutcomePair(nextExample, rv_DoubleVector); // real outcome

                CostGradientTuple observed = this.learner.observeExample(realResult, s.getWeights());

                // calculate new weights (note that the iteration count is not used)
                CostWeightTuple update = this.updater.computeNewWeights(s.getWeights(), observed.getGradient(), learningRate, 0, observed.getCost());

                CostGradientTuple newCostGradientTuple = this.updater.updateGradient(s.getWeights(),observed.getGradient(),0,0,observed.getCost());

                //update weights using the updated parameters
                DoubleVector new_weights = this.updater.prePredictionWeightUpdate(realResult, update.getWeight(),learningRate,0);

                // update model and classifier
                //this.model = new RegressionModel(new_weights, this.model.getActivationFunction());
                newModels[i] = new RegressionModel(update.getWeight(), s.getActivationFunction());
                i++;
            }
            //create a new multinomial model with the update weights
            this.setMultinomialModel(new MultinomialRegressionModel(newModels));
        } else
            LOGGER.info("URI is null");

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

    //Learning rate
    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    //L2
    public double getL2() {
        return l2;
    }

    protected void setL2(double l2) {
        this.l2 = l2;
    }

    //L1
    public double getL1() {
        return l1;
    }

    protected void setL1(double l1) {
        this.l1 = l1;
    }

    //Beta
    public double getBeta() {
        return beta;
    }

    protected void setBeta(double beta) {
        this.beta = beta;
    }

    //Learner
    public RegressionLearn getLearner() {
        return learner;
    }

    protected void setLearner(RegressionLearn learner) {
        this.learner = learner;
    }

    //Filepath
    public String getFilepath() {
        return filepath;
    }

    protected void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    //Updater
    public WeightUpdater getUpdater() {
        return updater;
    }

    protected void setUpdater(WeightUpdater updater) {
        this.updater = updater;
    }

    //Multinomial Model
    public MultinomialRegressionModel getMultinomialModel() {
        return multinomialModel;
    }

    protected void setMultinomialModel(MultinomialRegressionModel multinomialModel) {
        this.multinomialModel = multinomialModel;
    }

    //Multinomial Learner
    public MultinomialRegressionLearner getMultinomialLearner() {
        return multinomialLearner;
    }

    protected void setMultinomialLearner(MultinomialRegressionLearner multinomialLearner) {
        this.multinomialLearner = multinomialLearner;
    }

    //Multinomial Classifier
    public MultinomialRegressionClassifier getMultinomialClassifier() {
        return multinomialClassifier;
    }

    protected void setMultinomialClassifier(MultinomialRegressionClassifier multinomialClassifier) {
        this.multinomialClassifier = multinomialClassifier;
    }


    public Double getHoldoutValidationPercentage() {
        return holdoutValidationPercentage;
    }

    private void setHoldoutValidationPercentage(Double holdoutValidationPercentage) {
        this.holdoutValidationPercentage = holdoutValidationPercentage;
    }



}
