package org.dice_research.squirrel.predictor;

import com.google.common.hash.Hashing;
import de.jungblut.math.DoubleVector;
import de.jungblut.math.sparse.SequentialSparseDoubleVector;
import de.jungblut.nlp.VectorizerUtils;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Creates the feature vector that can be used in the prediction. It considers
 * the intrinsic URI features and the intrinsic features of the
 * referring URI.
 * Feature hashing uses the hash function MurmurHash3 to map the feature vectors into binary vectors.
 *
 */

public class FeatureVectorGenerator {
    public static final Logger LOGGER = LoggerFactory.getLogger(FeatureVectorGenerator.class);

    /**
     * Method to perfrom feature hashing to reduce the dimension of the features of the URIs
     * @param uri URI whose feature vector is to be calculated
     */
    public void featureHashing(CrawleableUri uri) {
        ArrayList<String> tokens1 = new ArrayList<>();

        // Creating tokens of the current URI
        tokenCreation(uri, tokens1);
        CrawleableUri referUri;

        // Creating tokens of the referring URI
        if (uri.getData(Constants.REFERRING_URI) != null) {
            referUri = new CrawleableUri((URI) uri.getData(Constants.REFERRING_URI));
            if (referUri != null)
                tokenCreation(referUri, tokens1);
        }
        String[] tokens = tokens1.toArray(new String[0]);
        try {
            DoubleVector feature = VectorizerUtils.sparseHashVectorize(tokens, Hashing.murmur3_128(), () -> new SequentialSparseDoubleVector(
                2 << 14));
            double[] d;
            d = feature.toArray();
            uri.addData(Constants.FEATURE_VECTOR, d);

        } catch (Exception e) {
            LOGGER.warn("Exception caused while adding the feature vector to the URI map", e);
        }

    }

    /**
     * Method to convert the URI in to small tokens
     * @param uri whose tokens are to be obtained
     * @param tokens the list in which the tokens are to be stored
     */
    public void tokenCreation(CrawleableUri uri, ArrayList tokens) {
        String[] uriToken;
        uriToken = uri.getUri().toString().split("/|\\.");
        tokens.addAll(Arrays.asList(uriToken));
    }
}
