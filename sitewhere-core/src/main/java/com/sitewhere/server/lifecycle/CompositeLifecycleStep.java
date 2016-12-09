package com.sitewhere.server.lifecycle;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.monitoring.ProgressMessage;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.ILifecycleStep;

/**
 * Implementation of {@link ILifecycleStep} that is composed of multiple
 * lifecycle steps that are executed in order.
 * 
 * @author Derek
 */
public class CompositeLifecycleStep implements ICompositeLifecycleStep {

    /** Step name */
    private String name;

    /** List of lifecycle steps to be executed */
    private List<ILifecycleStep> steps = new ArrayList<ILifecycleStep>();

    public CompositeLifecycleStep(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleStep#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleStep#getOperationCount()
     */
    @Override
    public int getOperationCount() {
	return steps.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ILifecycleStep#execute(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void execute(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	int i = 0;
	for (ILifecycleStep step : steps) {
	    i++;
	    ProgressMessage message = new ProgressMessage();
	    message.setTaskName(getName());
	    message.setTotalOperations(steps.size());
	    message.setCurrentOperation(i);
	    message.setMessage(step.getName());
	    monitor.reportProgress(message);
	    step.execute(monitor);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#addStep(com.
     * sitewhere.spi.server.lifecycle.ILifecycleStep)
     */
    public void addStep(ILifecycleStep step) {
	getSteps().add(step);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep#getSteps()
     */
    public List<ILifecycleStep> getSteps() {
	return steps;
    }

    public void setSteps(List<ILifecycleStep> steps) {
	this.steps = steps;
    }
}