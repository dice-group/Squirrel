package org.aksw.simba.squirrel.deduplication.hashing;

import java.io.Serializable;

/**
 * An abstract representation of a hash value computed by a {@link TripleSetHashFunction}.
 * It can be encoded to a String, and it can be decoded from a String.
 */
public interface HashValue extends Serializable {

    /**
     * Encode to String in order to easily store in a database.
     *
     * @return The encoded String.
     */
    String encodeToString();

    /**
     * Decode a {@link HashValue} from the given String. This is necessary when you load a hash from a database.
     *
     * @param s The given String representation.
     * @return The decoded {@link HashValue}.
     */
    HashValue decodeFromString(String s);
}
