/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.destination.sms;

/**
 * Parameters needed to send an SMS message.
 * 
 * @author Derek
 */
public class SmsParameters {

    /** SMS phone number */
    private String smsPhoneNumber;

    public String getSmsPhoneNumber() {
	return smsPhoneNumber;
    }

    public void setSmsPhoneNumber(String smsPhoneNumber) {
	this.smsPhoneNumber = smsPhoneNumber;
    }
}