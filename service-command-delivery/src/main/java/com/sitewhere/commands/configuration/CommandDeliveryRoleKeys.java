/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.configuration;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum CommandDeliveryRoleKeys implements IRoleKey {

    /** Command destinations */
    CommandDelivery("cmd_delivery"),

    /** Command router */
    CommandRouter("cmd_router"),

    /** Device type mapping router */
    DeviceTypeMappingRouter("devtype_map_router"),

    /** Device type mapping router mapping */
    DeviceTypeMappingRouterMapping("dmr_mapping"),

    /** Command destinations */
    CommandDestinations("cmd_dests"),

    /** Command destination */
    CommandDestination("cmd_dest"),

    /** Command encoder */
    CommandEncoder("cmd_encd"),

    /** Binary command encoder */
    BinaryCommandEncoder("bin_cmd_encd"),

    /** String command encoder */
    StringCommandEncoder("str_cmd_encd"),

    /** Parameter extractor */
    ParameterExtractor("param_ext"),

    /** MQTT parameter extractor */
    MqttParameterExtractor("mqtt_param_ext"),

    /** SMS parameter extractor */
    SmsParameterExtractor("sms_param_ext"),

    /** CoAP parameter extractor */
    CoapParameterExtractor("coaps_param_ext");

    private String id;

    private CommandDeliveryRoleKeys(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IRoleKey#getId()
     */
    @Override
    public String getId() {
	return id;
    }
}