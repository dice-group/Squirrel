package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.HashValue;

/**
 * A hash value as a Array of integers.
 */
public class ArrayHashValue implements HashValue {
    /**
     * The Array of HashValues.
     */
    private Integer[] hashValues;

    /**
     * The delimeter between the individual HashValues
     */
    private static final String DELIMETER = ",";

    /**
     * Constructor.
     */
    public ArrayHashValue() {

    }

    /**
     * Constructor.
     *
     * @param hashValues The Array of Hash values.
     */
    public ArrayHashValue(Integer[] hashValues) {
        this.hashValues = hashValues;
    }

    @Override
    public String encodeToString() {
        StringBuilder sb = new StringBuilder();
        for (int hashValue : hashValues) {
            sb.append(hashValue);
            sb.append(DELIMETER);
        }
        return sb.toString();
    }

    @Override
    public HashValue decodeFromString(String s) {
        String[] array = s.split(DELIMETER);
        Integer[] hashValues = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            if (!array[i].equals("")) {
                hashValues[i] = Integer.parseInt(array[i]);
            }
        }
        return new ArrayHashValue(hashValues);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArrayHashValue) {
            ArrayHashValue arrayHashValue = (ArrayHashValue) obj;
            if (hashValues.length != arrayHashValue.hashValues.length) {
                return false;
            }
            boolean equal = true;
            for (int i = 0; i < hashValues.length; i++) {
                if (hashValues[i].equals(arrayHashValue.hashValues[i])) {
                    equal = false;
                    break;
                }
            }

            return equal;
        }
        return false;
    }
}
