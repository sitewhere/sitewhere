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
 */
public interface ICompositeLifecycleStep extends ILifecycleStep {

    /**
     * Add a step to the list.
     * 
     * @param step
     */
    public void addStep(ILifecycleStep step);

    /**
     * Add step that initializes a component.
     * 
     * @param owner
     * @param component
     * @param require
     */
    public void addInitializeStep(ILifecycleComponent owner, ILifecycleComponent component, boolean require);

    /**
     * Add step that starts a component.
     * 
     * @param owner
     * @param component
     * @param require
     */
    public void addStartStep(ILifecycleComponent owner, ILifecycleComponent component, boolean require);

    /**
     * Add step that stops a component.
     * 
     * @param owner
     * @param component
     */
    public void addStopStep(ILifecycleComponent owner, ILifecycleComponent component);

    /**
     * Add step that terminates a component.
     * 
     * @param owner
     * @param component
     */
    public void addTerminateStep(ILifecycleComponent owner, ILifecycleComponent component);

    /**
     * Get an ordered list of steps to be executed.
     * 
     * @return
     */
    public List<ILifecycleStep> getSteps();
}