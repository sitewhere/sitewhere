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
package com.sitewhere.sources.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Provides support for filtering events that are duplicates of events already
 * in the system. The deduplication logic is implemented in subclasses.
 */
public interface IDeviceEventDeduplicator extends ITenantEngineLifecycleComponent {

    /**
     * Detects whether the given device event is a duplicate of another event in the
     * system.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public boolean isDuplicate(IDecodedDeviceRequest<?> request) throws SiteWhereException;
}