package org.dice_research.squirrel.analyzer.mime;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * State in a finite state machine.
 */
public final class RtState implements State {

    private List<Transition> transitions;
    private boolean isFinal;
    private boolean isError;

    public RtState() {
        this(false, false);
    }

    public RtState(final boolean isFinal, final boolean isError) {
        this.transitions = new ArrayList<>();
        this.isFinal = isFinal;
        this.isError = isError;
    }

    public State transit(final String c) throws NoSuchElementException {
        return transitions
            .stream()
            .filter(t -> t.isPossible(c))
            .map(Transition::state)
            .findAny()
            .get();
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public boolean isError() {
        return this.isError;
    }

    @Override
    public State with(Transition tr) {
        this.transitions.add(tr);
        return this;
    }

}
