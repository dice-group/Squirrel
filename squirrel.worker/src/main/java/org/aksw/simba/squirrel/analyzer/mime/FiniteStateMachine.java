package org.aksw.simba.squirrel.analyzer.mime;

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
    FiniteStateMachine switchState(final String c);

    /**
     * Is the current state a final one?
     *
     * @return true or false.
     */
    boolean canStop();

    boolean isError();

    Lang getMimeType();
}
