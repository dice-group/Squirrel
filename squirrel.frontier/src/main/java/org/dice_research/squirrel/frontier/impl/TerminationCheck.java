package org.dice_research.squirrel.frontier.impl;

import org.dice_research.squirrel.queue.UriQueue;

public interface TerminationCheck {

    public boolean shouldFrontierTerminate(UriQueue queue);
}
