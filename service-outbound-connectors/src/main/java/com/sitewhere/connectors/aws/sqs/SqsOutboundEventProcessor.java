/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors.aws.sqs;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.connectors.FilteredOutboundConnector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Outbound event processor that forwards events to Amazon SQS.
 * 
 * @author Derek
 */
public class SqsOutboundEventProcessor extends FilteredOutboundConnector {

    /** SQS client */
    private AmazonSQSClient sqs;

    /** Access key */
    private String accessKey;

    /** Secret key */
    private String secretKey;

    /** URL of queue to send message to */
    private String queueUrl;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#start
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
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
     * com.sitewhere.connectors.FilteredOutboundConnector#onMeasurementNotFiltered(
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceMeasurement)
     */
    @Override
    public void onMeasurementNotFiltered(IDeviceEventContext context, IDeviceMeasurement mx) throws SiteWhereException {
	sendSqsMessage(mx);
    }

    /*
     * @see
     * com.sitewhere.outbound.FilteredOutboundEventProcessor#onLocationNotFiltered(
     * com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceLocation)
     */
    @Override
    public void onLocationNotFiltered(IDeviceEventContext context, IDeviceLocation location) throws SiteWhereException {
	sendSqsMessage(location);
    }

    /*
     * @see
     * com.sitewhere.outbound.FilteredOutboundEventProcessor#onAlertNotFiltered(com.
     * sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceAlert)
     */
    @Override
    public void onAlertNotFiltered(IDeviceEventContext context, IDeviceAlert alert) throws SiteWhereException {
	sendSqsMessage(alert);
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onStateChangeNotFiltered(com.sitewhere.spi.device.event.IDeviceEventContext,
     * com.sitewhere.spi.device.event.IDeviceStateChange)
     */
    @Override
    public void onStateChangeNotFiltered(IDeviceEventContext context, IDeviceStateChange state)
	    throws SiteWhereException {
	sendSqsMessage(state);
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onCommandInvocationNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceCommandInvocation)
     */
    @Override
    public void onCommandInvocationNotFiltered(IDeviceEventContext context, IDeviceCommandInvocation invocation)
	    throws SiteWhereException {
	sendSqsMessage(invocation);
    }

    /*
     * @see com.sitewhere.outbound.FilteredOutboundEventProcessor#
     * onCommandResponseNotFiltered(com.sitewhere.spi.device.event.
     * IDeviceEventContext, com.sitewhere.spi.device.event.IDeviceCommandResponse)
     */
    @Override
    public void onCommandResponseNotFiltered(IDeviceEventContext context, IDeviceCommandResponse response)
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