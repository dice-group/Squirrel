package org.aksw.simba.squirrel.analyzer.mime;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

/**
 * Default implementation of a finite state machine.
 * This class is immutable and thread-safe.
 */

public final class RdfAutomata implements FiniteStateMachine {

    /**
     * Current state.
     */
    private State current;
    private Lang mimeType = RDFLanguages.RDFXML;

    /**
     * Ctor.
     *
     * @param initial Initial state of this machine.
     */
    private RdfAutomata(final State initial) {
        this.current = initial;
    }

    public FiniteStateMachine switchState(final String c) {
        return new RdfAutomata(this.current.transit(c));
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
    public static FiniteStateMachine buildRDFStateMachine() {
        State first = new RtState();
        State second = new RtState();
        State third = new RtState();
        State fourth = new RtState();
        State fifth = new RtState();
        State sixth = new RtState(true, false);
        State errorState = new RtState(true, true);

        first.with(new RtTransition("<", second));
        first.with(new RtTransition("[^<]", errorState));
        second.with(new RtTransition("\\?", third));
        second.with(new RtTransition("[^\\?]", errorState));
        third.with(new RtTransition("x", fourth));
        third.with(new RtTransition("[^x]", errorState));
        fourth.with(new RtTransition("m", fifth));
        fourth.with(new RtTransition("[^m]", errorState));
        fifth.with(new RtTransition("l", sixth));
        fifth.with(new RtTransition("[^l]", errorState));

        return new RdfAutomata(first);
    }
}
