/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.aws;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.device.event.processor.FilteredOutboundEventProcessor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;

/**
 * Outbound event processor that forwards events to Amazon SQS.
 * 
 * @author Derek
 */
public class SqsOutboundEventProcessor extends FilteredOutboundEventProcessor {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

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
	 * ()
	 */
	@Override
	public void start() throws SiteWhereException {
		super.start();

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
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onMeasurementsNotFiltered(com.sitewhere.spi.device.event.
	 * IDeviceMeasurements)
	 */
	@Override
	public void onMeasurementsNotFiltered(IDeviceMeasurements measurements) throws SiteWhereException {
		sendSqsMessage(measurements);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onLocationNotFiltered(com.sitewhere.spi.device.event.IDeviceLocation)
	 */
	@Override
	public void onLocationNotFiltered(IDeviceLocation location) throws SiteWhereException {
		sendSqsMessage(location);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onAlertNotFiltered(com.sitewhere.spi.device.event.IDeviceAlert)
	 */
	@Override
	public void onAlertNotFiltered(IDeviceAlert alert) throws SiteWhereException {
		sendSqsMessage(alert);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onStateChangeNotFiltered(com.sitewhere.spi.device.event.
	 * IDeviceStateChange)
	 */
	@Override
	public void onStateChangeNotFiltered(IDeviceStateChange state) throws SiteWhereException {
		sendSqsMessage(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onCommandInvocationNotFiltered(com.sitewhere.spi.device.event.
	 * IDeviceCommandInvocation)
	 */
	@Override
	public void onCommandInvocationNotFiltered(IDeviceCommandInvocation invocation) throws SiteWhereException {
		sendSqsMessage(invocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.device.event.processor.FilteredOutboundEventProcessor#
	 * onCommandResponseNotFiltered(com.sitewhere.spi.device.event.
	 * IDeviceCommandResponse)
	 */
	@Override
	public void onCommandResponseNotFiltered(IDeviceCommandResponse response) throws SiteWhereException {
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
		LOGGER.debug("Sent SQS message with id: " + result.getMessageId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
		return LOGGER;
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