/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.configuration.model;

/**
 * Indicates number of elements of a given role that can appear in a container.
 * 
 * @author Derek
 */
public enum ElementRoleStrategy {

	/** Exactly one element in the role can be added */
	ExactlyOne,

	/** Maximum of one element in the role can be added */
	ZeroOrOne,

	/** At least one element in the role must be added */
	AtLeastOne,

	/** Any number of elements in the role may be added */
	ZeroOrMore;
}