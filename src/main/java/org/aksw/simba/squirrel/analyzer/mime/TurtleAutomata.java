package org.aksw.simba.squirrel.analyzer.mime;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

/**
 * Default implementation of a finite state machine.
 * This class is immutable and thread-safe.
 */

public final class TurtleAutomata implements FiniteStateMachine {

    /**
     * Current state.
     */
    private State current;
    private Lang mimeType = RDFLanguages.TURTLE;

    /**
     * Ctor.
     *
     * @param initial Initial state of this machine.
     */
    private TurtleAutomata(final State initial) {
        this.current = initial;
    }

    public FiniteStateMachine switchState(final String c) {
        return new TurtleAutomata(this.current.transit(c));
    }

    public boolean canStop() {
        return this.current.isFinal();
    }

    public boolean isError() {
        return this.current.isError();
    }

    public Lang getMimeType() {
        return this.mimeType;
    }

    /**
     * Builds a finite state machine to validate a simple
     * RDF file
     *
     * @return
     */
    public static FiniteStateMachine buildTurtleStateMachine() {
        State first = new RtState();
        State second = new RtState();
        State third = new RtState();
        State fourth = new RtState();
        State fifth = new RtState();
        State sixth = new RtState();
        State seventh = new RtState();
        State eighth = new RtState(true, false);
        State errorState = new RtState(true, true);

        first.with(new RtTransition("\\@", second));
        first.with(new RtTransition("[^\\@]", errorState));
        second.with(new RtTransition("p", third));
        second.with(new RtTransition("[^p]", errorState));
        third.with(new RtTransition("r", fourth));
        third.with(new RtTransition("[^r]", errorState));
        fourth.with(new RtTransition("e", fifth));
        fourth.with(new RtTransition("[^e]", errorState));
        fifth.with(new RtTransition("f", sixth));
        fifth.with(new RtTransition("[^f]", errorState));
        sixth.with(new RtTransition("i", seventh));
        sixth.with(new RtTransition("[^i]", errorState));
        seventh.with(new RtTransition("x", eighth));
        seventh.with(new RtTransition("[^x]", errorState));

        return new TurtleAutomata(first);
    }
}
