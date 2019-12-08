package org.dice_research.squirrel.predictor;

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
    public void featureHashing(CrawleableUri uri);

    /**
     * Trains the model with positive and negative examples of the URIs.
     */

    public void train(String filepath);

    /**
     * Trains a multinomial regression model with URLs of 4 different types
     * @param filepath
     */
    void multiNomialTrain(String filepath);
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
    public int predict(CrawleableUri uri);
    /**
     * Update the weight of the model. It uses the predicted value and the true label value
     * with the feature vector from the URI map to calculate the new weight.
     *
     * @param uri
     *          {@link CrawleableUri} URI whose feature vector is used to update weights
     */
    public void weightUpdate(CrawleableUri uri);

    /**
     * Update the weight of the multinomial model. It updates the individual models and then
     * generates a new multinomial model.
     * @param uri
     * {@link CrawleableUri} URI whose class is to be predicted.
     */

    public void multinomialModelWeightUpdate(CrawleableUri uri);

    /**
     * Method to set regularizing parameter L1
     * @param l1
     */
    public void setL1Parameter (double l1);
    /**
     * Method that returns regularizing parameter L1
     * @return l1
     */
    public double getL1Parameter();

    /**
     * Method to set regularizing parameter L2
      * @param l2
     */
    public void setL2Parameter (double l2);

    /**
     * Method that returns the regularizing parameter L2
     * @return l2
     */
    public double getL2Parameter();

    /**
     * Method to set Beta parameter
     * @param beta
     */
    public void setBetaParameter(double beta);

    /**
     * Method that returns Beta parameter
     * @return beta
     */
    public double getBetaParameter();

    /**
     * Method to set learning rate
     * @param learningRate
     */
    public void setLearningRate(double learningRate);

    /**
     * Method that returns the learning rate
     * @return learningRate
     */
    public double getLearningRate();

}
