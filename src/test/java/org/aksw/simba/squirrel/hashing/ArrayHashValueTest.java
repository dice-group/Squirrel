package org.aksw.simba.squirrel.hashing;

import org.aksw.simba.squirrel.deduplication.hashing.impl.ArrayHashValue;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link ArrayHashValue}.
 */
public class ArrayHashValueTest {

    @Test
    public void testDecodeAndEncode() {
        Integer[] hashValues = new Integer[10];
        for (int i = 0; i < hashValues.length; i++) {
            hashValues[i] = i;
        }
        ArrayHashValue arrayHashValue = new ArrayHashValue(hashValues);

        String encodedString = arrayHashValue.encodeToString();
        ArrayHashValue decodedHashValue = (ArrayHashValue) new ArrayHashValue().decodeFromString(encodedString);
        Assert.assertTrue(decodedHashValue.equals(arrayHashValue));
    }
}
