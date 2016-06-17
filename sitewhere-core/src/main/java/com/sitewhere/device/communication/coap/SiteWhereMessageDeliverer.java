/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication.coap;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.MessageDeliverer;

import com.sitewhere.SiteWhere;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.device.communication.DeviceRequest;
import com.sitewhere.rest.model.device.communication.DeviceRequest.Type;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.communication.IInboundEventReceiver;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Take care of all SiteWhere message handling.
 * 
 * @author Derek
 */
public class SiteWhereMessageDeliverer implements MessageDeliverer {

	/** Static logger instance */
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(SiteWhereMessageDeliverer.class);

	/** Receiver that handles incoming events */
	private IInboundEventReceiver<byte[]> eventReceiver;

	public SiteWhereMessageDeliverer(IInboundEventReceiver<byte[]> eventReceiver) {
		this.eventReceiver = eventReceiver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.californium.core.server.MessageDeliverer#deliverRequest(org.eclipse.
	 * californium.core.network.Exchange)
	 */
	@Override
	public void deliverRequest(Exchange exchange) {
		OptionSet options = exchange.getRequest().getOptions();
		List<String> paths = options.getUriPath();
		if (paths.size() == 0) {
			createAndSendResponse(ResponseCode.BAD_REQUEST, "No path provided for CoAP processing.",
					exchange);
		}
		try {
			String tenantId = paths.remove(0);
			ITenant tenant = SiteWhere.getServer().getTenantManagement().getTenantById(tenantId);
			handleTenantRequest(tenant, paths, exchange);
		} catch (SiteWhereException e) {
			createAndSendResponse(ResponseCode.BAD_REQUEST, "Tenant id was invalid.", exchange);
		}
	}

	/**
	 * Handle a tenant-specific CoAP request.
	 * 
	 * @param tenant
	 * @param paths
	 * @param exchange
	 */
	protected void handleTenantRequest(ITenant tenant, List<String> paths, Exchange exchange) {
		String resourceType = paths.remove(0);
		if ("devices".equals(resourceType)) {
			handleGlobalDeviceRequest(tenant, paths, exchange);
		} else {
			createAndSendResponse(ResponseCode.BAD_REQUEST, "Unknown tenant resource type.", exchange);
		}
	}

	/**
	 * Handle a request for device-specific functionality.
	 * 
	 * @param tenant
	 * @param paths
	 * @param exchange
	 */
	protected void handleGlobalDeviceRequest(ITenant tenant, List<String> paths, Exchange exchange) {
		if (paths.size() == 0) {
			switch (exchange.getRequest().getCode()) {
			case POST: {
				try {
					DeviceRegistrationRequest registration =
							MarshalUtils.unmarshalJson(exchange.getRequest().getPayload(),
									DeviceRegistrationRequest.class);
					DeviceRequest request = new DeviceRequest();
					request.setHardwareId(registration.getHardwareId());
					request.setType(Type.RegisterDevice);
					request.setRequest(registration);
					getEventReceiver().onEventPayloadReceived(MarshalUtils.marshalJson(request), null);
					createAndSendResponse(ResponseCode.CONTENT, "Device created successfully.", exchange);
				} catch (SiteWhereException e) {
					createAndSendResponse(ResponseCode.BAD_REQUEST,
							"Device registration request was invalid.", exchange);
				}
				break;
			}
			default: {
				createAndSendResponse(ResponseCode.BAD_REQUEST, "Device operation not available.", exchange);
			}
			}
		} else {
			String hardwareId = paths.remove(0);
			try {
				IDevice device =
						SiteWhere.getServer().getDeviceManagement(tenant).getDeviceByHardwareId(hardwareId);
				handlePerDeviceRequest(tenant, device, paths, exchange);
			} catch (SiteWhereException e) {
				createAndSendResponse(ResponseCode.BAD_REQUEST, "Device hardware id is invalid.", exchange);
			}
		}
	}

	/**
	 * Handle request scoped to a specific device.
	 * 
	 * @param tenant
	 * @param device
	 * @param paths
	 * @param exchange
	 */
	protected void handlePerDeviceRequest(ITenant tenant, IDevice device, List<String> paths,
			Exchange exchange) {
		if (paths.size() > 0) {
			String operation = paths.remove(0);
			if ("measurements".equals(operation)) {
				handleDeviceMeasurements(tenant, device, paths, exchange);
			}
		}
	}

	/**
	 * Handle operations related to device measurements.
	 * 
	 * @param tenant
	 * @param device
	 * @param paths
	 * @param exchange
	 */
	protected void handleDeviceMeasurements(ITenant tenant, IDevice device, List<String> paths,
			Exchange exchange) {
		switch (exchange.getRequest().getCode()) {
		case POST: {
			try {
				DeviceMeasurementsCreateRequest mxs =
						MarshalUtils.unmarshalJson(exchange.getRequest().getPayload(),
								DeviceMeasurementsCreateRequest.class);
				DeviceRequest request = new DeviceRequest();
				request.setHardwareId(device.getHardwareId());
				request.setType(Type.DeviceMeasurements);
				request.setRequest(mxs);
				getEventReceiver().onEventPayloadReceived(MarshalUtils.marshalJson(request), null);
				createAndSendResponse(ResponseCode.CONTENT, "Device measurements created successfully.",
						exchange);
			} catch (SiteWhereException e) {
				createAndSendResponse(ResponseCode.BAD_REQUEST, "Device measurements request was invalid.",
						exchange);
			}
			break;
		}
		default: {
			createAndSendResponse(ResponseCode.BAD_REQUEST, "Device measurements operation not available.",
					exchange);
		}
		}
	}

	/**
	 * Send an error response on the given exchange.
	 * 
	 * @param code
	 * @param message
	 * @param exchange
	 */
	protected void createAndSendResponse(ResponseCode code, String message, Exchange exchange) {
		Response response = new Response(code);
		response.setPayload(message);
		exchange.sendResponse(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.californium.core.server.MessageDeliverer#deliverResponse(org.eclipse.
	 * californium.core.network.Exchange, org.eclipse.californium.core.coap.Response)
	 */
	@Override
	public void deliverResponse(Exchange exchange, Response response) {
		if (response == null) {
			throw new NullPointerException("Response must not be null");
		} else if (exchange == null) {
			throw new NullPointerException("Exchange must not be null");
		} else if (exchange.getRequest() == null) {
			throw new IllegalArgumentException("Exchange does not contain request");
		} else {
			exchange.getRequest().setResponse(response);
		}
	}

	public IInboundEventReceiver<byte[]> getEventReceiver() {
		return eventReceiver;
	}

	public void setEventReceiver(IInboundEventReceiver<byte[]> eventReceiver) {
		this.eventReceiver = eventReceiver;
	}
}