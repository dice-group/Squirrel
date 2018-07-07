package org.aksw.simba.squirrel.analyzer.mime;

/**
 * Transition in a finite State machine.
 */
public interface Transition {

    /**
     * Is the transition possible with the given character?
     *
     * @param c String.
     * @return true or false.
     */
    boolean isPossible(final String c);

    /**
     * The state to which this transition leads.
     *
     * @return State.
     */
    State state();
}
