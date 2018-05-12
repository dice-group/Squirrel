package org.aksw.simba.squirrel.deduplication.hashing;

import java.io.Serializable;

public interface HashValue extends Serializable {

    String encodeToString();

    HashValue decodeFromString(String s);
}
