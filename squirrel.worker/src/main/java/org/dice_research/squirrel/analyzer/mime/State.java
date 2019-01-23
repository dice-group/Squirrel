package org.dice_research.squirrel.analyzer.mime;

import java.util.NoSuchElementException;

/**
 * State. Part of a finite state machine.
 */
public interface State {

    /**
     * Add a Transition to this state.
     *
     * @param tr Given transition.
     * @return Modified State.
     */
    State with(final Transition tr);

    /**
     * Follow one of the transitions, to get
     * to the next state.
     *
     * @param c String.
     * @return State.
     * @throws IllegalStateException if the char is not accepted.
     */
    State transit(final String c) throws NoSuchElementException;

    /**
     * Can the automaton stop on this state?
     *
     * @return true or false
     */
    boolean isFinal();

    boolean isError();
}
