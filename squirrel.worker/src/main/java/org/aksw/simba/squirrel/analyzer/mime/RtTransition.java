package org.aksw.simba.squirrel.analyzer.mime;

/**
 * Transition in finite state machine.
 */
public final class RtTransition implements Transition {

    private String rule;
    private State next;

    /**
     * Ctor.
     *
     * @param rule Rule that a character has to meet
     *             in order to get to the next state.
     * @param next Next state.
     */
    public RtTransition(String rule, State next) {
        this.rule = rule;
        this.next = next;
    }

    public State state() {
        return this.next;
    }

    public boolean isPossible(String c) {
        return c.matches(this.rule);
    }

}
