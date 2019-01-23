package org.dice_research.squirrel.analyzer.mime;

import org.apache.jena.riot.Lang;

/**
 * Finite state machine.
 */
public interface FiniteStateMachine {

    /**
     * Follow a transition, switch the state of the machine.
     *
     * @param c Char.
     * @return A new finite state machine with the new state.
     */
    void switchState(final String c);

    /**
     * Is the current state a error state?
     *
     * @return true or false.
     */

    boolean isError();

    Lang getMimeType();
}
