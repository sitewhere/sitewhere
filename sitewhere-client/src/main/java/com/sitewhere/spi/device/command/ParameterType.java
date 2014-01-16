/*
 * ParameterType.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.command;

/**
 * Indicates parameter datatype.
 * 
 * @author Derek
 */
public enum ParameterType {

	/** Byte value */
	Byte,

	/** Short value */
	Short,

	/** Integer value */
	Integer,

	/** Long value */
	Long,

	/** Float value */
	Float,

	/** Double value */
	Double,

	/** Boolean value */
	Boolean,

	/** String value */
	String,
}