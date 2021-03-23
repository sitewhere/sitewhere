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
package com.sitewhere.commands.destination.sms;

import java.util.List;
import java.util.Map;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;

/**
 * Implements {@link ICommandDeliveryParameterExtractor} for
 * {@link SmsParameters}.
 */
public class SmsParameterExtractor extends TenantEngineLifecycleComponent
	implements ICommandDeliveryParameterExtractor<SmsParameters> {

    /** Default metadata field for SMS phone number */
    public static final String DEFAULT_SMS_PHONE_NUMBER_METADATA = "sms_phone";

    /** SMS phone number metadata field name */
    private String phoneNumberMetadataField = DEFAULT_SMS_PHONE_NUMBER_METADATA;

    public SmsParameterExtractor() {
	super(LifecycleComponentType.CommandParameterExtractor);
    }

    /*
     * @see com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor#
     * extractDeliveryParameters(com.sitewhere.commands.spi.ICommandDestination,
     * com.sitewhere.spi.device.IDeviceNestingContext, java.util.List,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    public SmsParameters extractDeliveryParameters(ICommandDestination<?, ?> destination, IDeviceNestingContext nesting,
	    List<? extends IDeviceAssignment> assignments, IDeviceCommandExecution execution)
	    throws SiteWhereException {
	SmsParameters params = new SmsParameters();

	// Load hostname and port from device metadata.
	Map<String, String> deviceMeta = nesting.getGateway().getMetadata();
	String phone = deviceMeta.get(getPhoneNumberMetadataField());
	params.setSmsPhoneNumber(phone);
	if (phone == null) {
	    throw new SiteWhereException("No phone number found in device metadata. Unable to deliver.");
	}

	return params;
    }

    public String getPhoneNumberMetadataField() {
	return phoneNumberMetadataField;
    }

    public void setPhoneNumberMetadataField(String phoneNumberMetadataField) {
	this.phoneNumberMetadataField = phoneNumberMetadataField;
    }
}
