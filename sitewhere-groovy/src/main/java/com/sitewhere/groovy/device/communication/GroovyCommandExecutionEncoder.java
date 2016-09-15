package com.sitewhere.groovy.device.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;
import com.sitewhere.spi.device.communication.EventDecodeException;
import com.sitewhere.spi.device.communication.ICommandExecutionEncoder;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

/**
 * Implementation of {@link ICommandExecutionEncoder} that defers encoding to a
 * Groovy script.
 * 
 * @author Derek
 */
public class GroovyCommandExecutionEncoder extends TenantLifecycleComponent
	implements ICommandExecutionEncoder<byte[]> {

    /** Injected global Groovy configuration */
    private GroovyConfiguration configuration;

    /** Path to script used for decoder */
    private String scriptPath;

    public GroovyCommandExecutionEncoder() {
	super(LifecycleComponentType.CommandExecutionEncoder);
    }

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
     */
    @Override
    public void start() throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
     */
    @Override
    public void stop() throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.ICommandExecutionEncoder#encode(
     * com.sitewhere.spi.device.command.IDeviceCommandExecution,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public byte[] encode(IDeviceCommandExecution command, IDeviceNestingContext nested, IDeviceAssignment assignment)
	    throws SiteWhereException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_COMMAND_EXCUTION, command);
	    binding.setVariable(IGroovyVariables.VAR_NESTING_CONTEXT, nested);
	    binding.setVariable(IGroovyVariables.VAR_ASSIGNMENT, assignment);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	    return (byte[]) getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
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
     * @see com.sitewhere.spi.device.communication.ICommandExecutionEncoder#
     * encodeSystemCommand(com.sitewhere.spi.device.command.ISystemCommand,
     * com.sitewhere.spi.device.IDeviceNestingContext,
     * com.sitewhere.spi.device.IDeviceAssignment)
     */
    @Override
    public byte[] encodeSystemCommand(ISystemCommand command, IDeviceNestingContext nested,
	    IDeviceAssignment assignment) throws SiteWhereException {
	try {
	    Binding binding = new Binding();
	    binding.setVariable(IGroovyVariables.VAR_SYSTEM_COMMAND, command);
	    binding.setVariable(IGroovyVariables.VAR_NESTING_CONTEXT, nested);
	    binding.setVariable(IGroovyVariables.VAR_ASSIGNMENT, assignment);
	    binding.setVariable(IGroovyVariables.VAR_LOGGER, LOGGER);
	    return (byte[]) getConfiguration().getGroovyScriptEngine().run(getScriptPath(), binding);
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

    public GroovyConfiguration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(GroovyConfiguration configuration) {
	this.configuration = configuration;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }
}