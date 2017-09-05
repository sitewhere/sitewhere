/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.command;

/**
 * Indicates parameter datatype. These are the same datatypes used in
 * <a href="https://developers.google.com/protocol-buffers/docs/proto">Google
 * Protocol Buffers</a>.
 * 
 * @author Derek
 */
public enum ParameterType {

    Double,

    Float,

    Int32,

    Int64,

    UInt32,

    UInt64,

    SInt32,

    SInt64,

    Fixed32,

    Fixed64,

    SFixed32,

    SFixed64,

    Bool,

    String,

    Bytes;
}