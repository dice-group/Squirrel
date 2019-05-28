package org.dice_research.squirrel.frontier.impl;

import org.dice_research.squirrel.queue.UriQueue;

public class QueueBasedTerminationCheck implements TerminationCheck {

    protected boolean wasNotEmpty = false;
    
    @Override
    public boolean shouldFrontierTerminate(UriQueue queue) {
        if(queue.isEmpty()) {
            return wasNotEmpty;
        } else {
            wasNotEmpty = true;
            return false;
        }
    }
}
