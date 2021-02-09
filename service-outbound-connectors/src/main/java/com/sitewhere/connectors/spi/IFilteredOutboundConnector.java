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
package com.sitewhere.connectors.spi;

import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IProcessedEventPayload;

/**
 * Adds concept of filtering to outbound connectors.
 */
public interface IFilteredOutboundConnector extends IOutboundConnector {

    /**
     * Get the list of configured filters.
     * 
     * @return
     */
    public List<IDeviceEventFilter> getFilters();

    /**
     * Process a batch of event payloads after filters have been applied.
     * 
     * @param payloads
     * @throws SiteWhereException
     */
    public void processFilteredEventBatch(List<IProcessedEventPayload> payloads) throws SiteWhereException;
}