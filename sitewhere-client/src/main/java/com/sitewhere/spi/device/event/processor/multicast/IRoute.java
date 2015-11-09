/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.processor.multicast;

/**
 * Represent a route to which an event can be sent.
 * 
 * @author Derek
 *
 * @param <T>
 */
public interface IRoute<T> {

	public T getRoute();
}