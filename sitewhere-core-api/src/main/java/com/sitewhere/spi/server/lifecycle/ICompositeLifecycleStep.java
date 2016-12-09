package com.sitewhere.spi.server.lifecycle;

import java.util.List;

/**
 * Implementation of {@link ILifecycleStep} that executes multiple steps in
 * sequence.
 * 
 * @author Derek
 *
 */
public interface ICompositeLifecycleStep extends ILifecycleStep {

    /**
     * Add a step to the list.
     * 
     * @param step
     */
    public void addStep(ILifecycleStep step);

    /**
     * Get an ordered list of steps to be executed.
     * 
     * @return
     */
    public List<ILifecycleStep> getSteps();
}