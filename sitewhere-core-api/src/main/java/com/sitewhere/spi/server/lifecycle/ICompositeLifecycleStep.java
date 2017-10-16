/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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