package org.dice_research.squirrel.predictor;

import org.dice_research.squirrel.data.uri.CrawleableUri;

/**
 * Interface of an Online leaner predicting the URI type.
 * This interface is for building a learner,training it and predicting the type of URI
 *
 */

public interface Predictor {
    /**
     * Return the feature vector that can be used in the prediction. It considers
     * the intrinsic URI features and the intrinsic features of the
     * referring URI.
     * Feature hashing uses the hash function MurmurHash32
     * to map the feature vectors into binary vectors. It uses a random sparse projection matrix
     * (where n >> m ) in order to reduce the dimension of the data from n to m.
     *
     * @param uri a URI from which intrinsic features are obtained.
     */
    public void featureHashing(CrawleableUri uri);

    /**
     * Initialize a regression model with using a Stochastic Gradient Descent builder
     * with FTRL-Proximal online learning algorithm
     * Train a new model using the supplied streams.
     */

    public void train(String filePath);
    /**
     * Return a prediction value of the type of the given URI, the prediction should be between 0 and 1.
     * e.g. In case of RDF type prediction, the more the predicted value is close to 1
     * the more the URI is of type RDF otherwise is not.
     *
     * @param uri a URI containing the feature vector stored in its map.
     *
     *  @return the predicted value.
     */
    public double predict(CrawleableUri uri);
    /**
     * Update the weight of the model. It uses the predicted value and the true label value
     * with the feature vector from the URI map to calculate the new weight.
     *
     * @param uri a URI containing the predicted label and true label stored in its map.
     */
    public void weightUpdate(CrawleableUri uri);
}
