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
package com.sitewhere.search.spi;

import java.util.List;

import com.sitewhere.microservice.api.search.ISearchProvider;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleComponent;

/**
 * Manages a list of search providers that can be used by SiteWhere.
 */
public interface ISearchProvidersManager extends ILifecycleComponent {

    /**
     * Get list of available search providers.
     * 
     * @return
     */
    public List<ISearchProvider> getSearchProviders();

    /**
     * Get search provider with the given unique id.
     * 
     * @param id
     * @return
     */
    public ISearchProvider getSearchProvider(String id);
}