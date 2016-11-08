package com.sitewhere.groovy.device.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import com.sitewhere.SiteWhere;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Common base class for Groovy command delivery parameter extractors.
 * 
 * @author Derek
 *
 * @param <T>
 */
public class GroovyParameterExtractor<T> extends TenantLifecycleComponent
	implements ICommandDeliveryParameterExtractor<T> {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Path to script used for decoder */
    private String scriptPath;

    public GroovyParameterExtractor() {
	super(LifecycleComponentType.CommandParameterExtractor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.ICommandDeliveryParameterExtractor
     * #extractDeliveryParameters(com.sitewhere.spi.device.
     * IDeviceNestingContext, com.sitewhere.spi.device.IDeviceAssignment,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    @SuppressWarnings("unchecked")
    public T extractDeliveryParameters(IDeviceNestingContext nesting, IDeviceAssignment assignment,
	    IDeviceCommandExecution execution) throws SiteWhereException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_NESTING_CONTEXT, nesting);
	    binding.setVariable(IGroovyVariables.VAR_ASSIGNMENT, assignment);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	    return (T) SiteWhere.getServer().getTenantGroovyConfiguration(getTenant()).getGroovyScriptEngine()
		    .run(getScriptPath(), binding);
	} catch (ResourceException e) {
	    throw new EventDecodeException("Unable to access Groovy decoder script.", e);
	} catch (ScriptException e) {
	    throw new EventDecodeException("Unable to run Groovy decoder script.", e);
	} catch (CompilationFailedException e) {
	    throw new EventDecodeException("Error compiling Groovy script.", e);
	} catch (Throwable e) {
	    throw new EventDecodeException("Unhandled exception in Groovy decoder script.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }
}