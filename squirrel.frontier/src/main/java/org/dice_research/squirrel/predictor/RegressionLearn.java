package org.dice_research.squirrel.predictor;

import com.google.common.base.Preconditions;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.activation.ActivationFunction;
import de.jungblut.math.dense.SingleEntryDoubleVector;
import de.jungblut.math.loss.LossFunction;
import de.jungblut.math.minimize.CostGradientTuple;
import de.jungblut.online.minimizer.StochasticMinimizer;
import de.jungblut.online.ml.FeatureOutcomePair;
import de.jungblut.online.regression.RegressionLearner;


public class RegressionLearn extends RegressionLearner {
    private StochasticMinimizer minimizer;

    private final ActivationFunction activationFunction;
    private final LossFunction lossFunction;

    public RegressionLearn(StochasticMinimizer minimizer,
                             ActivationFunction activationFunction, LossFunction lossFunction) {
        super(minimizer, activationFunction, lossFunction);
        this.activationFunction = Preconditions.checkNotNull(activationFunction,
            "activation function");
        this.lossFunction = Preconditions.checkNotNull(lossFunction,
            "loss function");
    }

    public CostGradientTuple observeExample(FeatureOutcomePair next, DoubleVector weights) {

        DoubleVector hypothesis = new SingleEntryDoubleVector(this.activationFunction.apply(next.getFeature().dot(weights)));
        double cost = this.lossFunction.calculateLoss(next.getOutcome(), hypothesis);
        DoubleVector gradient = this.lossFunction.calculateGradient(next.getFeature(), next.getOutcome(), hypothesis);
        return new CostGradientTuple(cost, gradient);
    }
}


    

