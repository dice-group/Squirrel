package org.aksw.simba.squirrel.deduplication.hashing.impl;

import org.aksw.simba.squirrel.deduplication.hashing.HashValue;

/**
 * A hash value as a Array of integers.
 */
public class ArrayHashValue implements HashValue {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

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
        for (int i = 0; i < hashValues.length; i++) {
            sb.append(hashValues[i]);
            if (i < hashValues.length - 1) {
                sb.append(DELIMETER);
            }
        }
        return sb.toString();
    }

    @Override
    public HashValue decodeFromString(String s) {
        String[] array = s.split(DELIMETER);
        Integer[] hashValues = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            hashValues[i] = Integer.parseInt(array[i]);
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
            for (int i = 0; i < hashValues.length; i++) {
                if (hashValues[i] == null ^ arrayHashValue.hashValues[i] == null) {
                    return false;
                } else if (!(hashValues[i] == null && arrayHashValue.hashValues[i] == null)) {
                    if (!hashValues[i].equals(arrayHashValue.hashValues[i])) {
                        // in this case they are equal
                        return false;
                    }
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ArrayHashValue [");
        for (int i = 0; i < hashValues.length; i++) {
            if (hashValues[i] != null) {
                stringBuilder.append(hashValues[i] + DELIMETER);
            } else {
                stringBuilder.append("null" + DELIMETER);
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
