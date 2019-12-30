package org.dice_research.squirrel.predictor;

import de.jungblut.online.regression.RegressionModel;
import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * Interface of an Online leaner predicting the URI type.
 * This interface is for building a learner,training it and predicting the type of URI
 *
 */

public interface Predictor {
    /**
     * Creates the feature vector that can be used in the prediction. It considers
     * the intrinsic URI features and the intrinsic features of the
     * referring URI.
     * Feature hashing uses the hash function MurmurHash32
     * to map the feature vectors into binary vectors. It uses a random sparse projection matrix
     * (where n >> m ) in order to reduce the dimension of the data from n to m.
     *
     * @param uri
     *          {@link CrawleableUri}  URI whose feature vector is to be created.
     */
    void featureHashing(CrawleableUri uri);

    /**
     * Return a prediction value of the type of the given URI, the prediction should be between 0 and 1.
     * e.g. In case of RDF type prediction, the more the predicted value is close to 1
     * the more the URI is of type RDF otherwise is not.
     *
     * @param uri
     *           {@link CrawleableUri} URI whose class is to be predicted.
     *
     *  @return the predicted value.
     */
    Integer predict(CrawleableUri uri);
    /**
     * Update the weight of the model. It uses the predicted value and the true label value
     * with the feature vector from the URI map to calculate the new weight.
     *
     * @param uri
     *          {@link CrawleableUri} URI whose feature vector is used to update weights
     */
    void weightUpdate(CrawleableUri uri);


    /**
     * Gets the model being used by the predictor
     * @return the models
     */
    RegressionModel getModel();

    /**
     * Method to set regularizing parameter L1
     * @param l1
     */
    void setL1(double l1);
    /**
     * Method that returns regularizing parameter L1
     * @return l1
     */
    double getL1();

    /**
     * Method to set regularizing parameter L2
      * @param l2
     */
    void setL2(double l2);

    /**
     * Method that returns the regularizing parameter L2
     * @return l2
     */
    double  getL2();

    /**
     * Method to set Beta parameter
     * @param beta
     */
    void setBeta(double beta);

    /**
     * Method that returns Beta parameter
     * @return beta
     */
    double getBeta();

    /**
     * Method to set learning rate
     * @param learningRate
     */
    void setLearningRate(double learningRate);

    /**
     * Method that returns the learning rate
     * @return learningRate
     */
    double getLearningRate();

}
