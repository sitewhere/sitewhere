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
package com.sitewhere.sources;

import java.util.Map;

import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.sources.spi.IInboundEventReceiver;
import com.sitewhere.sources.spi.IInboundEventSource;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Common base class for creating {@link IInboundEventReceiver} implementations.
 *
 * @param <T>
 */
public class InboundEventReceiver<T> extends TenantEngineLifecycleComponent implements IInboundEventReceiver<T> {

    /** Parent event source */
    private IInboundEventSource<T> eventSource;

    public InboundEventReceiver() {
	super(LifecycleComponentType.InboundEventReceiver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getDisplayName()
     */
    @Override
    public String getDisplayName() {
	return getClass().getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * onEventPayloadReceived (java.lang.Object, java.util.Map)
     */
    @Override
    public void onEventPayloadReceived(T payload, Map<String, Object> metadata) {
	getEventSource().onEncodedEventReceived(InboundEventReceiver.this, payload, metadata);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * getEventSource()
     */
    public IInboundEventSource<T> getEventSource() {
	return eventSource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.communication.IInboundEventReceiver#
     * setEventSource(com. sitewhere.spi.device.communication.IInboundEventSource)
     */
    public void setEventSource(IInboundEventSource<T> eventSource) {
	this.eventSource = eventSource;
    }
}