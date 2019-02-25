package org.dice_research.squirrel.frontier.impl;

public class EndlessCrawlingTerminationCheck implements TerminationCheck {

    @Override
    public boolean shouldFrontierTerminate(FrontierImpl frontier) {
        // return always false
        return false;
    }
}
