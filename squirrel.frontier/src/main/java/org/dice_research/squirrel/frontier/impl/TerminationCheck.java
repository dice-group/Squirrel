package org.dice_research.squirrel.frontier.impl;

import org.dice_research.squirrel.queue.IpAddressBasedQueue;

public interface TerminationCheck {

    public boolean shouldFrontierTerminate(IpAddressBasedQueue queue);
}
