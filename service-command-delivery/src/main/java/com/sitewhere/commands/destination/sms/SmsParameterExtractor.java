/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.destination.sms;

import java.util.List;
import java.util.Map;

import com.sitewhere.commands.spi.ICommandDeliveryParameterExtractor;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implements {@link ICommandDeliveryParameterExtractor} for
 * {@link SmsParameters}.
 * 
 * @author Derek
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
     * extractDeliveryParameters(com.sitewhere.spi.device.IDeviceNestingContext,
     * java.util.List, com.sitewhere.spi.device.command.IDeviceCommandExecution)
     */
    @Override
    public SmsParameters extractDeliveryParameters(IDeviceNestingContext nesting, List<IDeviceAssignment> assignments,
	    IDeviceCommandExecution execution) throws SiteWhereException {
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
