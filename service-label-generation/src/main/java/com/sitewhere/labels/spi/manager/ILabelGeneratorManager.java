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
package com.sitewhere.labels.spi.manager;

import java.util.List;

import com.sitewhere.labels.spi.ILabelGenerator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages a list of label generators.
 */
public interface ILabelGeneratorManager extends ITenantEngineLifecycleComponent {

    /**
     * Get the list of available label generators.
     * 
     * @return
     * @throws SiteWhereException
     */
    List<ILabelGenerator> getLabelGenerators() throws SiteWhereException;

    /**
     * Get a label generator by id.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    ILabelGenerator getLabelGenerator(String id) throws SiteWhereException;
}