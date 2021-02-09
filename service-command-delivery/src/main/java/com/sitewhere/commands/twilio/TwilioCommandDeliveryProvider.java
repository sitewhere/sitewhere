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
package com.sitewhere.commands.twilio;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.sitewhere.commands.destination.sms.SmsParameters;
import com.sitewhere.commands.spi.ICommandDeliveryProvider;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceNestingContext;
import com.sitewhere.spi.device.command.IDeviceCommandExecution;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.lifecycle.LifecycleComponentType;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Account;

/**
 * Implementation of {@link ICommandDeliveryProvider} that sends an SMS message
 * via Twilio.
 */
public class TwilioCommandDeliveryProvider extends TenantEngineLifecycleComponent
	implements ICommandDeliveryProvider<String, SmsParameters> {

    /** Account SID */
    private String accountSid;

    /** Auth token */
    private String authToken;

    /** Phone number to send command from */
    private String fromPhoneNumber;

    /** Client for Twilio REST calls */
    private TwilioRestClient twilio;

    /** Twilio account */
    private Account account;

    public TwilioCommandDeliveryProvider() {
	super(LifecycleComponentType.CommandDeliveryProvider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getAccountSid() == null) {
	    throw new SiteWhereException("Twilio command delivery provider missing account SID.");
	}
	if (getAuthToken() == null) {
	    throw new SiteWhereException("Twilio command delivery provider missing auth token.");
	}
	this.twilio = new TwilioRestClient(getAccountSid(), getAuthToken());
	this.account = twilio.getAccount();
	getLogger().info("Twilio delivery provider started. Calls will originate from " + getFromPhoneNumber() + ".");
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandDeliveryProvider#deliver(com.sitewhere.spi
     * .device.IDeviceNestingContext, java.util.List,
     * com.sitewhere.spi.device.command.IDeviceCommandExecution, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void deliver(IDeviceNestingContext nested, List<? extends IDeviceAssignment> assignments,
	    IDeviceCommandExecution execution, String encoded, SmsParameters params) throws SiteWhereException {
	getLogger().info("Delivering SMS command to " + params.getSmsPhoneNumber() + ".");
	sendSms(encoded, getFromPhoneNumber(), params.getSmsPhoneNumber());
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandDeliveryProvider#deliverSystemCommand(com.
     * sitewhere.spi.device.IDeviceNestingContext, java.util.List, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void deliverSystemCommand(IDeviceNestingContext nested, List<? extends IDeviceAssignment> assignments,
	    String encoded, SmsParameters params) throws SiteWhereException {
	throw new UnsupportedOperationException();
    }

    /**
     * Send an SMS message.
     * 
     * @param message
     * @param from
     * @param to
     * @throws SiteWhereException
     */
    protected void sendSms(String message, String from, String to) throws SiteWhereException {
	MessageFactory messageFactory = account.getMessageFactory();
	List<NameValuePair> params = new ArrayList<NameValuePair>();
	params.add(new BasicNameValuePair("To", to));
	params.add(new BasicNameValuePair("From", from));
	params.add(new BasicNameValuePair("Body", message));
	try {
	    messageFactory.create(params);
	} catch (TwilioRestException e) {
	    throw new SiteWhereException("Unable to send Twilio SMS message.", e);
	}
    }

    public String getAccountSid() {
	return accountSid;
    }

    public void setAccountSid(String accountSid) {
	this.accountSid = accountSid;
    }

    public String getAuthToken() {
	return authToken;
    }

    public void setAuthToken(String authToken) {
	this.authToken = authToken;
    }

    public String getFromPhoneNumber() {
	return fromPhoneNumber;
    }

    public void setFromPhoneNumber(String fromPhoneNumber) {
	this.fromPhoneNumber = fromPhoneNumber;
    }
}