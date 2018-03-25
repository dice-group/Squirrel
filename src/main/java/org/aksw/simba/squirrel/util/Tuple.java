package org.aksw.simba.squirrel.util;

/**
 * A useful class containing two elements with arbitrary types.
 *
 * @param <X> The first element.
 * @param <Y> The second element.
 */
public class Tuple<X, Y> {

    public X x;
    public Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}
