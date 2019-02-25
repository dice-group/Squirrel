package org.dice_research.squirrel.frontier.impl;

public class QueueBasedTerminationCheck implements TerminationCheck {

    protected boolean wasNotEmpty = false;
    
    @Override
    public boolean shouldFrontierTerminate(FrontierImpl frontier) {
        if(frontier.queue.isEmpty()) {
            return wasNotEmpty;
        } else {
            wasNotEmpty = true;
            return false;
        }
    }
}
