package org.dice_research.squirrel.frontier.impl;

import org.dice_research.squirrel.queue.UriQueue;

public class QueueBasedTerminationCheck implements TerminationCheck {

    
    @Override
    public boolean shouldFrontierTerminate(UriQueue queue) {

        if(queue.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}
