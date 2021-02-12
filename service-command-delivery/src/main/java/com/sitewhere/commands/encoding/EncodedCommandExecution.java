/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.commands.encoding;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.device.command.ISystemCommand;

/**
 * Wraps data sent when executing a command so it can be marshaled to JSON.
 */
@JsonInclude(Include.NON_NULL)
public class EncodedCommandExecution {

    /** Command information */
    private IDeviceCommandExecution command;

    /** System command information */
    private ISystemCommand systemCommand;

    /** Device nesting information */
    private IDeviceNestingContext nestingContext;

    /** Device assignment information */
    private List<? extends IDeviceAssignment> assignments;

    public EncodedCommandExecution(IDeviceCommandExecution command, IDeviceNestingContext nestingContext,
	    List<? extends IDeviceAssignment> assignments) {
	this.command = command;
	this.nestingContext = nestingContext;
	this.assignments = assignments;
    }

    public EncodedCommandExecution(ISystemCommand systemCommand, IDeviceNestingContext nestingContext,
	    List<? extends IDeviceAssignment> assignments) {
	this.systemCommand = systemCommand;
	this.nestingContext = nestingContext;
	this.assignments = assignments;
    }

    public IDeviceCommandExecution getCommand() {
	return command;
    }

    public void setCommand(IDeviceCommandExecution command) {
	this.command = command;
    }

    public ISystemCommand getSystemCommand() {
	return systemCommand;
    }

    public void setSystemCommand(ISystemCommand systemCommand) {
	this.systemCommand = systemCommand;
    }

    public IDeviceNestingContext getNestingContext() {
	return nestingContext;
    }

    public void setNestingContext(IDeviceNestingContext nestingContext) {
	this.nestingContext = nestingContext;
    }

    public List<? extends IDeviceAssignment> getAssignments() {
	return assignments;
    }

    public void setAssignments(List<? extends IDeviceAssignment> assignments) {
	this.assignments = assignments;
    }
}