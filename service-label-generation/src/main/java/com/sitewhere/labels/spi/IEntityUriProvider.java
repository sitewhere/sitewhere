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
package com.sitewhere.labels.spi;

import java.net.URI;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.group.IDeviceGroup;

/**
 * Translates a SiteWhere entity into a unique URI that identifies it.
 */
public interface IEntityUriProvider {

    /**
     * Get unique identifier for a customer type.
     * 
     * @param customerType
     * @return
     * @throws SiteWhereException
     */
    URI getCustomerTypeIdentifier(ICustomerType customerType) throws SiteWhereException;

    /**
     * Get unique identifier for a customer.
     * 
     * @param customer
     * @return
     * @throws SiteWhereException
     */
    URI getCustomerIdentifier(ICustomer customer) throws SiteWhereException;

    /**
     * Get unique identifier for an area type.
     * 
     * @param areaType
     * @return
     * @throws SiteWhereException
     */
    URI getAreaTypeIdentifier(IAreaType areaType) throws SiteWhereException;

    /**
     * Get unique identifier for an area.
     * 
     * @param area
     * @return
     * @throws SiteWhereException
     */
    URI getAreaIdentifier(IArea area) throws SiteWhereException;

    /**
     * Get unique identifier for a device type.
     * 
     * @param deviceType
     * @return
     * @throws SiteWhereException
     */
    URI getDeviceTypeIdentifier(IDeviceType deviceType) throws SiteWhereException;

    /**
     * Get unique identifier for a device.
     * 
     * @param device
     * @return
     * @throws SiteWhereException
     */
    URI getDeviceIdentifier(IDevice device) throws SiteWhereException;

    /**
     * Get unique identifier for a device group.
     * 
     * @param group
     * @return
     * @throws SiteWhereException
     */
    URI getDeviceGroupIdentifier(IDeviceGroup group) throws SiteWhereException;

    /**
     * Get unique identifier for a device assignment.
     * 
     * @param assignment
     * @return
     * @throws SiteWhereException
     */
    URI getDeviceAssignmentIdentifier(IDeviceAssignment assignment) throws SiteWhereException;

    /**
     * Get unique identifier for an asset type.
     * 
     * @param assetType
     * @return
     * @throws SiteWhereException
     */
    URI getAssetTypeIdentifier(IAssetType assetType) throws SiteWhereException;

    /**
     * Get unique identifier for an asset.
     * 
     * @param asset
     * @return
     * @throws SiteWhereException
     */
    URI getAssetIdentifier(IAsset asset) throws SiteWhereException;
}