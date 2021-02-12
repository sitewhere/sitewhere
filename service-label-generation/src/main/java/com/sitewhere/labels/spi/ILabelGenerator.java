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
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Generates label which correspond to SiteWhere entities.
 */
public interface ILabelGenerator extends ITenantEngineLifecycleComponent {

    /**
     * Get unique generator id.
     * 
     * @return
     * @throws SiteWhereException
     */
    String getId() throws SiteWhereException;

    /**
     * Get name of generator.
     * 
     * @return
     * @throws SiteWhereException
     */
    String getName() throws SiteWhereException;

    /**
     * Get label for a customer type.
     * 
     * @param customerType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getCustomerTypeLabel(ICustomerType customerType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for a customer.
     * 
     * @param customer
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getCustomerLabel(ICustomer customer, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for an area type.
     * 
     * @param areaType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getAreaTypeLabel(IAreaType areaType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for an area.
     * 
     * @param area
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getAreaLabel(IArea area, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for a device type.
     * 
     * @param deviceType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getDeviceTypeLabel(IDeviceType deviceType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for a device.
     * 
     * @param device
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getDeviceLabel(IDevice device, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for a device group.
     * 
     * @param group
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getDeviceGroupLabel(IDeviceGroup group, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for a device assignment.
     * 
     * @param assignment
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getDeviceAssignmentLabel(IDeviceAssignment assignment, IEntityUriProvider provider)
	    throws SiteWhereException;

    /**
     * Get label for an asset type.
     * 
     * @param assetType
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getAssetTypeLabel(IAssetType assetType, IEntityUriProvider provider) throws SiteWhereException;

    /**
     * Get label for an asset.
     * 
     * @param asset
     * @param provider
     * @return
     * @throws SiteWhereException
     */
    byte[] getAssetLabel(IAsset asset, IEntityUriProvider provider) throws SiteWhereException;
}