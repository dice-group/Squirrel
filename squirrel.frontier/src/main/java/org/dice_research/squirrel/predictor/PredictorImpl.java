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
import de.jungblut.online.minimizer.StochasticGradientDescent.StochasticGradientDescentBuilder;
import de.jungblut.online.ml.FeatureOutcomePair;
import de.jungblut.online.regression.RegressionClassifier;
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


public final class PredictorImpl implements Predictor {


    public WeightUpdater updater;
    public RegressionLearn learner;
    public RegressionModel model;
    public RegressionClassifier classifier;
    public MultinomialRegressionModel multinomialModel;
    public MultinomialRegressionLearner multinomialLearner;
    public MultinomialRegressionClassifier multinomialClassifier;
    private static final double DEFAULT_LEARNING_RATE = 0.01;
    protected double learningRate = DEFAULT_LEARNING_RATE;
    private static final double DEFAULT_BETA = 1;
    protected  double beta = DEFAULT_BETA;
    private  static final  double DEFAULT_L1 = 1;
    protected  double l1 = DEFAULT_L1;
    private static final double DEFAULT_L2 = 1;
    protected  double l2 = DEFAULT_L2;
    public TrainingDataProvider trainingDataProvider;
    private static final Logger LOGGER = LoggerFactory.getLogger(PredictorImpl.class);

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
                2<<14));
            double[] d;
            d = feature.toArray();
            uri.addData(Constants.FEATURE_VECTOR, d);

        }catch (Exception e){
            LOGGER.info("Exception caused while adding the feature vector to the URI map"+e);
        }

    }

    public ArrayList tokenCreation(CrawleableUri uri, ArrayList tokens){
        String[] uriToken;
        uriToken = uri.getUri().toString().split("/|\\.");
        tokens.addAll(Arrays.asList(uriToken));
        return tokens;
    }


    @Override
    public void train(String filePath) {
        updater = new AdaptiveFTRLRegularizer(beta,l1 ,l2);
        StochasticGradientDescent sgd = StochasticGradientDescentBuilder
            .create(learningRate) // learning rate
            .holdoutValidationPercentage(0.05d) // 5% as validation set
            .historySize(10_000) // keep 10k samples to compute relative improvement
            .weightUpdater(updater) // FTRL updater
            .progressReportInterval(1_000) // report every n iterations
            .build();

        // simple regression with Sigmoid and LogLoss
        learner = new RegressionLearn(sgd,
            new SigmoidActivationFunction(), new LogLoss());
        learner.setNumPasses(2);
        learner.verbose();
        // train the model
        trainingDataProvider = new TrainingDataProviderImpl();
        this.model = learner.train(() -> trainingDataProvider.setUpStream(filePath));
        // output the weights
        //model.getWeights().iterateNonZero().forEachRemaining(System.out::println);

    }


    public void multiNomialTrain(String filepath){
        trainingDataProvider = new TrainingDataProviderImpl();
        multinomialLearner = new MultinomialRegressionLearner(factory);
        multinomialLearner.verbose();
        this.multinomialModel = multinomialLearner.train(() -> trainingDataProvider.setUpStream(filepath));
    }

    IntFunction<RegressionLearner> factory = (i) -> {
        // take care of not sharing any state from the outside, since classes are trained in parallel
        StochasticGradientDescent minimizer = StochasticGradientDescentBuilder
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

    @Override
    public int predict(CrawleableUri uri) {
        int pred = 0;
        try {
            //Get the feature vector
            if (uri.getData(Constants.FEATURE_VECTOR) != null) {
                Object featureArray = uri.getData(Constants.FEATURE_VECTOR);
                double[] doubleFeatureArray = (double[]) featureArray;
                DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);
                //initialize the regression classifier with updated model and predict
                //classifier = new RegressionClassifier(model);
                multinomialClassifier = new MultinomialRegressionClassifier(multinomialModel);
                ////DoubleVector prediction = classifier.predict(features);
                DoubleVector prediction = multinomialClassifier.predict(features);
                pred = prediction.maxIndex();

                }else {
                LOGGER.info("Feature vector of this "+ uri.getUri().toString() +" is null");
            }
        } catch (Exception e) {
            LOGGER.warn("Prediction for this "+ uri.getUri().toString() +" failed " + e);
            pred = 0;
        }
        return  pred ;
    }

    @Override
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
                DoubleVector new_weights = this.updater.prePredictionWeightUpdate(realResult, update.getWeight(),learningRate,0);


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

    @Override
    public void setL1Parameter (double l1){
        this.l1 = l1;
    }

    @Override
    public double getL1Parameter(){
        return this.l1;
    }

    @Override
    public void setL2Parameter (double l2){
        this.l2 = l2;
    }

    @Override
    public double getL2Parameter(){
        return this.l2;
    }

    @Override
    public void setBetaParameter(double beta){
        this.beta = beta;
    }

    @Override
    public double getBetaParameter(){
        return this.beta;
    }

    @Override
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public double getLearningRate() {
        return learningRate;
    }
}
