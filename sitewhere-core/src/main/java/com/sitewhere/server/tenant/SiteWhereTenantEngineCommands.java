/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.tenant;

import com.sitewhere.rest.model.command.CommandResponse;
import com.sitewhere.spi.command.CommandResult;
import com.sitewhere.spi.command.ICommandResponse;
import com.sitewhere.spi.server.lifecycle.LifecycleStatus;

/**
 * Classes used to execute tenant engine commands asynchronously.
 * 
 * @author Derek
 */
public class SiteWhereTenantEngineCommands {

    public static enum Command {
	Start("start", StartCommand.class), Stop("stop", StopCommand.class);

	/** Command string */
	private String command;

	/** Command class */
	private Class<? extends TenantEngineCommand> commandClass;

	private Command(String command, Class<? extends TenantEngineCommand> commandClass) {
	    this.command = command;
	    this.commandClass = commandClass;
	}

	public String getCommand() {
	    return command;
	}

	public void setCommand(String command) {
	    this.command = command;
	}

	public Class<? extends TenantEngineCommand> getCommandClass() {
	    return commandClass;
	}

	public void setCommandClass(Class<? extends TenantEngineCommand> commandClass) {
	    this.commandClass = commandClass;
	}

	/**
	 * Based on the command phrase, return the implementing command class.
	 * 
	 * @param command
	 * @return
	 */
	public static Class<? extends TenantEngineCommand> getCommandClass(String command) {
	    for (Command current : Command.values()) {
		if (current.getCommand().equals(command)) {
		    return current.getCommandClass();
		}
	    }
	    return null;
	}
    }

    /**
     * Command that starts a tenant engine.
     * 
     * @author Derek
     */
    public static class StartCommand extends TenantEngineCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public ICommandResponse call() throws Exception {
	    try {
		if (getEngine().initialize()) {
		    getEngine().lifecycleStart();
		    if (getEngine().getLifecycleStatus() == LifecycleStatus.Error) {
			return new CommandResponse(CommandResult.Failed, getEngine().getLifecycleError().getMessage());
		    }
		} else {
		    return new CommandResponse(CommandResult.Failed, "Engine initialization failed.");
		}
	    } catch (Exception e) {
		return new CommandResponse(CommandResult.Failed, e.getMessage());
	    }
	    return new CommandResponse(CommandResult.Successful, "Tenant engine started.");
	}
    }

    /**
     * Command that stops a tenant engine.
     * 
     * @author Derek
     */
    public static class StopCommand extends TenantEngineCommand {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public ICommandResponse call() throws Exception {
	    try {
		getEngine().lifecycleStop();
		if (getEngine().getLifecycleStatus() == LifecycleStatus.Error) {
		    return new CommandResponse(CommandResult.Failed, getEngine().getLifecycleError().getMessage());
		}
	    } catch (Exception e) {
		return new CommandResponse(CommandResult.Failed, e.getMessage());
	    }
	    return new CommandResponse(CommandResult.Successful, "Tenant engine stopped.");
	}
    }
}
