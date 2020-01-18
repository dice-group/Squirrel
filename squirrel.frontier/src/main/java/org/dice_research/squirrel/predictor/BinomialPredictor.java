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

/**
 * A predictor that predicts the RDF-relevance of a URI by performing binary classification.
 */
public final class BinomialPredictor implements Predictor{

    private static final Logger LOGGER = LoggerFactory.getLogger(BinomialPredictor.class);
    /**
     * {@link WeightUpdater} Used to update the weights of the predictor model used.
     */
    private WeightUpdater updater;
    /**
     * {@link RegressionLearn} Used to train the model with training data
     */
    private RegressionLearn learner;
    /**
     * {@link RegressionModel} Represents the regression model used for the prediction of the RDF-relevance of the URI
     */
    private RegressionModel model;
    /**
     * {@link RegressionClassifier} Classifier for regression model. Takes a model or the atomic parts of it and predicts the outcome for a given feature.
     *
     */
    private RegressionClassifier classifier;
    /**
     * Used to store the location of the training data file.
     */
    private String filepath;
    /**
     * The rate at which the model learns.
     */
    private Double learningRate;
    /**
     * Regularizing parameter L2
     */
    private Double l2;
    /**
     *  Regularizing parameter L1
     */
    private Double l1;
    /**
     * Hyper parameter Beta
     */
    private Double beta;
    /**
     * Validation percentage which is between 0 and 1
     */
    private Double holdoutValidationPercentage;
    /**
     * The threshold above which a URI is classified into positive class
     */
    private Double threshold;
    /**
     * The positive class for the classification
     */
    private String positiveClass;
    /**
     * {@link FeatureVectorGenerator} Used to generate the feature vector of the URI
     */
    private FeatureVectorGenerator featureGenerator = new FeatureVectorGenerator();

    /**
     * Predicts the type of the URI
     * @param uri the URI to which the prediction has to be made
     * @return  the type of the URI
     */
    public String predict(CrawleableUri uri) {

        String predictedClass = null;
        try {
            featureGenerator.featureHashing(uri);
            Object featureArray = uri.getData(Constants.FEATURE_VECTOR);
            double[] doubleFeatureArray = (double[]) featureArray;
            DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);
            //initialize the regression classifier with updated model and predict
            this.setClassifier(new RegressionClassifier(this.getModel()));
            DoubleVector prediction = this.classifier.predict(features);

            if(prediction.get(0) >= this.getThreshold())
                predictedClass = this.getPositiveClass();
            else
                predictedClass = "NEGATIVE_CLASS";
        } catch (Exception e) {
            LOGGER.warn("Prediction for this " + uri.getUri().toString() + " failed " + e);
            e.printStackTrace();
        }
        return predictedClass;
    }

    /**
     * Updates the predictor model based on the this URI
     * @param curi based on which the model weights has to be updated
     */
    public void weightUpdate(CrawleableUri curi) {
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
                //update weights using the updated parameters
                DoubleVector newWeights = this.updater.prePredictionWeightUpdate(realResult, this.model.getWeights(), learningRate, 0);
                CostGradientTuple observed = this.learner.observeExample(realResult, newWeights);
                // calculate new weights (note that the iteration count is not used)
                CostWeightTuple update = this.updater.computeNewWeights(newWeights, observed.getGradient(), learningRate, 0, observed.getCost());
                // update model and classifier
                this.model = new RegressionModel(update.getWeight(), this.model.getActivationFunction());
            } else {
                LOGGER.warn("Feature vector or true label of this " + curi.getUri().toString() + " is null");
            }
        } catch (Exception e) {
            LOGGER.warn("Exception happened while updating the weights for the URI type predictor model", e);
        }
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

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double getL2() {
        return l2;
    }

    public void setL2(double l2) {
        this.l2 = l2;
    }

    public double getL1() {
        return l1;
    }

    public void setL1(double l1) {
        this.l1 = l1;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public String getFilepath() {
        return filepath;
    }

    public double getLearningRate() {
        return learningRate;
    }

    private Double getHoldoutValidationPercentage() {
        return holdoutValidationPercentage;
    }

    private void setHoldoutValidationPercentage(Double holdoutValidationPercentage) {
        this.holdoutValidationPercentage = holdoutValidationPercentage;
    }
    public Double getThreshold(){ return this.threshold; }

    public void setThreshold(Double threshold) { this.threshold = threshold; }

    public String getPositiveClass() { return this.positiveClass; }

    public void setPositiveClass(String positiveClass) { this.positiveClass = positiveClass; }


    /**
     * A builder pattern for the Binomialpredictor, that uses
     * Regression Model, Regression Learner along with default training data  and other default hyperparameters
     */
    public static class BinomialPredictorBuilder {

        private TrainingDataProvider trainingDataProvider = new BinomialTrainDataProviderImpl();

        protected StochasticGradientDescent sgd; //Minimizer

        private RegressionLearn learner; //Learner

        private RegressionModel model; //Model

        private WeightUpdater updater; //Updater

        private Double learningRate;//Learning rate

        private Double beta;   //Beta

        private Double l1;   //L1

        private Double l2;  //L2

        private Double holdoutValidationPercentage; //Validation percentage which is between 0 and 1

        private RegressionClassifier classifier;   //Classifier

        private String filePath; // file path for the training data file

        private double threshold; // threshold above which a URI is classified into the positive class

        public String positiveClass; // the positive class of the binary classification

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

        public BinomialPredictorBuilder withLearningRate(Double learningRate) {
            this.setLearningRate(learningRate);
            return this;
        }

        public BinomialPredictorBuilder withL1(Double L1) {
            this.setL1(L1);
            return this;
        }

        public BinomialPredictorBuilder withL2(Double L2) {
            this.setL2(L2);
            return this;
        }

        public BinomialPredictorBuilder withBeta(Double Beta) {
            this.setBeta(Beta);
            return this;
        }

        public BinomialPredictorBuilder withThreshold(Double threshold) {
            this.setThreshold(threshold);
            return this;
        }

        public BinomialPredictorBuilder withPositiveClass(String positiveClass){
            this.setPostiveClass(positiveClass);
            return this;
        }

        public BinomialPredictor build() {
            BinomialPredictor predictor = new BinomialPredictor();

            if (this.getLearningRate() == null)
                this.setLearningRate(0.7);
            predictor.setLearningRate(this.learningRate);

            if (this.getBeta() == null)
                this.setBeta(1);
            predictor.setBeta(this.beta);

            if (this.getL1() == null) {
                this.setL1(1);
            }
            predictor.setL1(this.l1);

            if (this.getL2() == null)
                this.setL2(1);

            predictor.setL2(this.getL2());

            if(this.getThreshold() == null) {
                this.setThreshold(0.5);
            }

            predictor.setThreshold(this.getThreshold());

            predictor.setPositiveClass(this.getPositiveClass());

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

            sgd = StochasticGradientDescent.StochasticGradientDescentBuilder
                .create(this.getLearningRate()) // learning rate
                .holdoutValidationPercentage(this.getHoldoutValidationPercentage()) // 5% as validation set
                .historySize(10_000) // keep 10k samples to compute relative improvement
                .weightUpdater(updater) // FTRL updater
                .progressReportInterval(1_000) // report every n iterations
                .build();


            //learner
            if (this.getLearner() == null)
                this.setLearner(new RegressionLearn(sgd, new SigmoidActivationFunction(), new LogLoss()));
            predictor.setLearner(this.getLearner());

            //model
            ArrayList<String> classList = new ArrayList<>();
            classList.add(this.positiveClass);
            if (this.getModel() == null)
                this.setModel(this.learner.train(() -> trainingDataProvider.setUpStream(this.filePath, classList)));
            predictor.setModel(this.getModel());

            //this.train(filePath);

            //classifier
            if (this.getClassifier() == null)
                if (this.getModel() != null)
                    this.setClassifier(new RegressionClassifier(this.getModel()));
            predictor.setClassifier(this.getClassifier());

            return predictor;
        }

        private RegressionLearn getLearner() {
            return this.learner;
        }

        private void setLearner(RegressionLearn learner) {
            this.learner = learner;
        }

        private RegressionModel getModel() {
            return this.model;
        }

        private void setModel(RegressionModel model) {
            this.model = model;
        }

        private RegressionClassifier getClassifier() {
            return this.classifier;
        }

        private void setClassifier(RegressionClassifier classifier) {
            this.classifier = classifier;
        }

        private WeightUpdater getUpdater() {
            return this.updater;
        }

        private void setUpdater(WeightUpdater updater) {
            this.updater = updater;
        }

        private Double getLearningRate() {
            return this.learningRate;
        }

        private void setLearningRate(double learningRate) {
            this.learningRate = learningRate;
        }

        private Double getBeta() {
            return this.beta;
        }

        private void setBeta(double beta) {
            this.beta = beta;
        }

        private Double getL1() {
            return this.l1;
        }

        private void setL1(double l1) {
            this.l1 = l1;
        }

        private Double getL2() {
            return this.l2;
        }

        private void setL2(double l2) {
            this.l2 = l2;
        }

        private String getFilePath() {
            return this.filePath;
        }

        private void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        private Double getHoldoutValidationPercentage() {
            return this.holdoutValidationPercentage;
        }

        private void setHoldoutValidationPercentage(Double holdoutValidationPercentage) {
            this.holdoutValidationPercentage = holdoutValidationPercentage;
        }

        private Double getThreshold(){ return this.threshold; }

        private void setThreshold(Double threshold) { this.threshold = threshold; }

        private String getPositiveClass() { return  this.positiveClass; }

        private void setPostiveClass(String postiveClass) { this.positiveClass = postiveClass; }

    }

}
