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
package com.sitewhere.connectors.aws.sqs;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.sitewhere.connectors.SerialOutboundConnector;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Outbound connector that forwards events to Amazon SQS.
 */
public class SqsOutboundConnector extends SerialOutboundConnector {

    /** SQS client */
    private AmazonSQSClient sqs;

    /** Access key */
    private String accessKey;

    /** Secret key */
    private String secretKey;

    /** URL of queue to send message to */
    private String queueUrl;

    /*
     * @see
     * com.sitewhere.connectors.FilteredOutboundConnector#start(com.sitewhere.spi.
     * microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	ClientConfiguration config = new ClientConfiguration();
	config.setMaxConnections(250);
	config.setMaxErrorRetry(5);

	if (getAccessKey() == null) {
	    throw new SiteWhereException("Amazon access key not provided.");
	}

	if (getSecretKey() == null) {
	    throw new SiteWhereException("Amazon secret key not provided.");
	}

	sqs = new AmazonSQSClient(new BasicAWSCredentials(getAccessKey(), getSecretKey()), config);
	Region usEast1 = Region.getRegion(Regions.US_EAST_1);
	sqs.setRegion(usEast1);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onMeasurement(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurement(IDeviceEventContext context, IDeviceMeasurement mx) throws SiteWhereException {
	sendSqsMessage(mx);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onLocation(com.sitewhere.spi
     * .device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocation(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	sendSqsMessage(location);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onAlert(com.sitewhere.spi.
     * device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlert(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	sendSqsMessage(alert);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onStateChange(com.sitewhere.
     * spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChange(IDeviceEventContext context, IDeviceStateChange state) throws SiteWhereException {
	sendSqsMessage(state);
    }

    /*
     * @see
     * com.sitewhere.connectors.SerialOutboundConnector#onCommandInvocation(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocation(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	sendSqsMessage(invocation);
    }

    /*
     * @see com.sitewhere.connectors.SerialOutboundConnector#onCommandResponse(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponse(IDeviceEventContext context, IDeviceCommandResponse response)
	    throws SiteWhereException {
	sendSqsMessage(response);
    }

    /**
     * Send an event message to SQS.
     * 
     * @param event
     * @throws SiteWhereException
     */
    protected void sendSqsMessage(IDeviceEvent event) throws SiteWhereException {
	SendMessageRequest message = new SendMessageRequest();
	message.setMessageBody(MarshalUtils.marshalJsonAsString(event));
	message.setQueueUrl(getQueueUrl());
	SendMessageResult result = getSqs().sendMessage(message);
	getLogger().debug("Sent SQS message with id: " + result.getMessageId());
    }

    public AmazonSQSClient getSqs() {
	return sqs;
    }

    public void setSqs(AmazonSQSClient sqs) {
	this.sqs = sqs;
    }

    public String getAccessKey() {
	return accessKey;
    }

    public void setAccessKey(String accessKey) {
	this.accessKey = accessKey;
    }

    public String getSecretKey() {
	return secretKey;
    }

    public void setSecretKey(String secretKey) {
	this.secretKey = secretKey;
    }

    public String getQueueUrl() {
	return queueUrl;
    }

    public void setQueueUrl(String queueUrl) {
	this.queueUrl = queueUrl;
    }
}