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
     * Return a prediction value of the type of the given URI, the prediction should be between 0 and 1.
     * e.g. In case of RDF type prediction, the more the predicted value is close to 1
     * the more the URI is of type RDF otherwise is not.
     *
     * @param uri
     *           {@link CrawleableUri} URI whose class is to be predicted.
     *
     *  @return the predicted class.
     */
    String predict(CrawleableUri uri);
    /**
     * Update the weight of the model. It uses the predicted value and the true label value
     * with the feature vector from the URI map to calculate the new weight.
     *
     * @param uri
     *          {@link CrawleableUri} URI whose feature vector is used to update weights
     */
    void weightUpdate(CrawleableUri uri);

}
