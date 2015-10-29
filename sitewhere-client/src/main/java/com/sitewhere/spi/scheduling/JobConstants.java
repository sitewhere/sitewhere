/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.scheduling;

/**
 * Metadata constants for values used by various job types.
 * 
 * @author Derek
 */
public interface JobConstants {

	/**
	 * Constants used for single command invocation.
	 * 
	 * @author Derek
	 */
	public static interface CommandInvocation {

		/** Token of assignment to target */
		public static final String ASSIGNMENT_TOKEN = "assignmentToken";

		/** Token of command to invoke */
		public static final String COMMAND_TOKEN = "commandToken";

		/** Prefix for command parameter values */
		public static final String PARAMETER_PREFIX = "param_";
	}

	/**
	 * Constants used for batch command invocations.
	 * 
	 * @author Derek
	 */
	public static interface BatchCommandInvocation {

		/** Indicates if results should be narrowed by device specification */
		public static final String SPECIFICATION_TOKEN = "specificationToken";

		/** Flag that excludes assigned devices */
		public static final String EXCLUDE_ASSIGNED = "excludeAssigned";

		/** Indicates if results should be narrowed by group */
		public static final String GROUP_TOKEN = "groupToken";

		/** Indicates if results should be narrowed by groups that have a given role */
		public static final String GROUP_ROLE = "groupRole";

		/** Indicates if results should be narrowed by devices created on or after a date */
		public static final String START_DATE = "startDate";

		/** Indicates if results should be narrowed by devices created on or before a date */
		public static final String END_DATE = "endDate";
	}
}