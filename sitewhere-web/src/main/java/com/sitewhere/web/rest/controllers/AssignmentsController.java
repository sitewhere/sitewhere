/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.device.charting.ChartBuilder;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.device.marshaling.DeviceCommandInvocationMarshalHelper;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceStateChangeCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceStreamDataCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceAssignmentType;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.charting.IChartSeries;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for assignment operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/assignments")
@Api(value = "assignments", description = "Operations related to SiteWhere device assignments.")
public class AssignmentsController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(AssignmentsController.class);

	/**
	 * Used by AJAX calls to create a device assignment.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceAssignment createDeviceAssignment(@RequestBody DeviceAssignmentCreateRequest request)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createDeviceAssignment", LOGGER);
		try {
			if (StringUtils.isEmpty(request.getDeviceHardwareId())) {
				throw new SiteWhereException("Hardware id required.");
			}
			if (request.getAssignmentType() == null) {
				throw new SiteWhereException("Assignment type required.");
			}
			if (request.getAssignmentType() != DeviceAssignmentType.Unassociated) {
				if (request.getAssetModuleId() == null) {
					throw new SiteWhereException("Asset module id required.");
				}
				if (request.getAssetId() == null) {
					throw new SiteWhereException("Asset id required.");
				}
			}
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement();
			IDeviceAssignment created = management.createDeviceAssignment(request);
			DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
			helper.setIncludeAsset(true);
			helper.setIncludeDevice(true);
			helper.setIncludeSite(true);
			return helper.convert(created, SiteWhere.getServer().getAssetModuleManager());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get an assignment by its unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get a device assignment by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceAssignment getDeviceAssignment(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getDeviceAssignment", LOGGER);
		try {
			IDeviceAssignment assignment = assureAssignment(token);
			DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
			helper.setIncludeAsset(true);
			helper.setIncludeDevice(true);
			helper.setIncludeSite(true);
			return helper.convert(assignment, SiteWhere.getServer().getAssetModuleManager());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get an assignment by its unique token.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceAssignment deleteDeviceAssignment(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteDeviceAssignment", LOGGER);
		try {
			IDeviceAssignment assignment =
					SiteWhere.getServer().getDeviceManagement().deleteDeviceAssignment(token, force);
			DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
			helper.setIncludeAsset(true);
			helper.setIncludeDevice(true);
			helper.setIncludeSite(true);
			return helper.convert(assignment, SiteWhere.getServer().getAssetModuleManager());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Update metadata associated with an assignment.
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{token}/metadata", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update metadata for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceAssignment updateDeviceAssignmentMetadata(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@RequestBody MetadataProvider metadata) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateDeviceAssignmentMetadata", LOGGER);
		try {
			IDeviceAssignment result =
					SiteWhere.getServer().getDeviceManagement().updateDeviceAssignmentMetadata(token,
							metadata);
			DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
			helper.setIncludeAsset(true);
			helper.setIncludeDevice(true);
			helper.setIncludeSite(true);
			return helper.convert(result, SiteWhere.getServer().getAssetModuleManager());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List all device events for an assignment that match the given criteria.
	 * 
	 * @param token
	 * @param page
	 * @param pageSize
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/events", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List all events for device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceEvent> listEvents(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listEvents", LOGGER);
		try {
			DateRangeSearchCriteria criteria =
					new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
			return SiteWhere.getServer().getDeviceManagement().listDeviceEvents(token, criteria);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List all device measurements for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/measurements", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List measurement events for device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceMeasurements> listMeasurements(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listMeasurements", LOGGER);
		try {
			DateRangeSearchCriteria criteria =
					new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
			return SiteWhere.getServer().getDeviceManagement().listDeviceMeasurements(token, criteria);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List device measurements for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/measurements/series", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List measurement events for device assignment in chart format")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public List<IChartSeries<Double>> listMeasurementsAsChartSeries(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
			@ApiParam(value = "Measurement Ids", required = false) @RequestParam(required = false) String[] measurementIds)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listMeasurementsAsChartSeries", LOGGER);
		try {
			DateRangeSearchCriteria criteria =
					new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
			ISearchResults<IDeviceMeasurements> measurements =
					SiteWhere.getServer().getDeviceManagement().listDeviceMeasurements(token, criteria);
			ChartBuilder builder = new ChartBuilder();
			return builder.process(measurements.getResults(), measurementIds);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Create measurements to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @param updateState
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/measurements", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create measurements event for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceMeasurements createMeasurements(@RequestBody DeviceMeasurementsCreateRequest input,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createMeasurements", LOGGER);
		try {
			IDeviceMeasurements result =
					SiteWhere.getServer().getDeviceManagement().addDeviceMeasurements(token, input);
			return DeviceMeasurements.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List device locations for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/locations", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List location events for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceLocation> listLocations(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listLocations", LOGGER);
		try {
			DateRangeSearchCriteria criteria =
					new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
			return SiteWhere.getServer().getDeviceManagement().listDeviceLocations(token, criteria);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Create location to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @param updateState
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/locations", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a location event for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceLocation createLocation(@RequestBody DeviceLocationCreateRequest input,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createLocation", LOGGER);
		try {
			IDeviceLocation result =
					SiteWhere.getServer().getDeviceManagement().addDeviceLocation(token, input);
			return DeviceLocation.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List device alerts for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/alerts", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List alert events for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceAlert> listAlerts(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listAlerts", LOGGER);
		try {
			DateRangeSearchCriteria criteria =
					new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
			return SiteWhere.getServer().getDeviceManagement().listDeviceAlerts(token, criteria);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Create alert to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @param updateState
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/alerts", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create an alert event for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceAlert createAlert(@RequestBody DeviceAlertCreateRequest input,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createAlert", LOGGER);
		try {
			IDeviceAlert result = SiteWhere.getServer().getDeviceManagement().addDeviceAlert(token, input);
			return DeviceAlert.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Create a stream to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/streams", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a new stream for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceStream createDeviceStream(@RequestBody DeviceStreamCreateRequest request,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createDeviceStream", LOGGER);
		try {
			IDeviceStream result =
					SiteWhere.getServer().getDeviceManagement().createDeviceStream(token, request);
			return DeviceStream.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	@RequestMapping(value = "/{token}/streams", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List streams for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceStream> listDeviceStreams(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listDeviceStreams", LOGGER);
		try {
			DateRangeSearchCriteria criteria =
					new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
			ISearchResults<IDeviceStream> matches =
					SiteWhere.getServer().getDeviceManagement().listDeviceStreams(token, criteria);
			List<IDeviceStream> converted = new ArrayList<IDeviceStream>();
			for (IDeviceStream stream : matches.getResults()) {
				converted.add(DeviceStream.copy(stream));
			}
			return new SearchResults<IDeviceStream>(converted);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get an existing device stream associated with an assignment.
	 * 
	 * @param token
	 * @param streamId
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/streams/{streamId:.+}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	@ApiOperation(value = "Get an existing stream for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceStream getDeviceStream(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Stream Id", required = true) @PathVariable String streamId)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getDeviceStream", LOGGER);
		try {
			IDeviceStream result =
					SiteWhere.getServer().getDeviceManagement().getDeviceStream(token, streamId);
			if (result == null) {
				throw new SiteWhereSystemException(ErrorCode.InvalidStreamId, ErrorLevel.ERROR,
						HttpServletResponse.SC_NOT_FOUND);
			}
			return DeviceStream.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Adds data to an existing device stream.
	 * 
	 * @param token
	 * @param streamId
	 * @param sequenceNumber
	 * @param svtRequest
	 * @param svtResponse
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/streams/{streamId:.+}", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Add data to a stream for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public void addDeviceStreamData(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Stream Id", required = true) @PathVariable String streamId,
			@ApiParam(value = "Sequence Number", required = false) @RequestParam(required = false) Long sequenceNumber,
			HttpServletRequest svtRequest, HttpServletResponse svtResponse) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "addDeviceStreamData", LOGGER);
		try {
			ServletInputStream inData = svtRequest.getInputStream();
			ByteArrayOutputStream byteData = new ByteArrayOutputStream();
			int data;
			while ((data = inData.read()) != -1) {
				byteData.write(data);
			}
			byte[] payload = byteData.toByteArray();

			DeviceStreamDataCreateRequest request = new DeviceStreamDataCreateRequest();
			request.setStreamId(streamId);
			request.setSequenceNumber(sequenceNumber);
			request.setEventDate(new Date());
			request.setUpdateState(false);
			request.setData(payload);
			SiteWhere.getServer().getDeviceManagement().addDeviceStreamData(token, request);
			svtResponse.setStatus(HttpServletResponse.SC_CREATED);
		} catch (SiteWhereSystemException e) {
			if (e.getCode() == ErrorCode.InvalidStreamId) {
				svtResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				LOGGER.error("Unhandled SiteWhere exception.", e);
				svtResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (IOException e) {
			LOGGER.error(e);
			svtResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	@RequestMapping(value = "/{token}/streams/{streamId:.+}/content", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get all content from stream")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public void getAllDeviceStreamData(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Stream Id", required = true) @PathVariable String streamId,
			HttpServletResponse svtResponse) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getAllDeviceStreamData", LOGGER);
		IDeviceStream stream = SiteWhere.getServer().getDeviceManagement().getDeviceStream(token, streamId);
		if (stream == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidStreamId, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		svtResponse.setContentType(stream.getContentType());

		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(1, 0, null, null);
		ISearchResults<IDeviceStreamData> data =
				SiteWhere.getServer().getDeviceManagement().listDeviceStreamData(token, streamId, criteria);
		for (IDeviceStreamData chunk : data.getResults()) {
			try {
				svtResponse.getOutputStream().write(chunk.getData());
			} catch (IOException e) {
				LOGGER.error("Error writing chunk to servlet output stream.", e);
			}
		}
	}

	/**
	 * Create command invocation to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/invocations", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create a command invocation event for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceCommandInvocation createCommandInvocation(
			@RequestBody DeviceCommandInvocationCreateRequest request,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createCommandInvocation", LOGGER);
		try {
			if (request.getInitiator() == null) {
				throw new SiteWhereException("Command initiator is required.");
			}
			if (request.getTarget() == null) {
				throw new SiteWhereException("Command target is required.");
			}
			IDeviceCommand command = assureDeviceCommand(request.getCommandToken());
			IDeviceCommandInvocation result =
					SiteWhere.getServer().getDeviceManagement().addDeviceCommandInvocation(token, command,
							request);
			DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper();
			return helper.convert(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List device command invocations for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/invocations", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List alert events for a device command invocations")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceCommandInvocation> listCommandInvocations(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Include command information", required = false) @RequestParam(defaultValue = "true") boolean includeCommand,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listCommandInvocations", LOGGER);
		try {
			DateRangeSearchCriteria criteria =
					new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
			ISearchResults<IDeviceCommandInvocation> matches =
					SiteWhere.getServer().getDeviceManagement().listDeviceCommandInvocations(token, criteria);
			DeviceCommandInvocationMarshalHelper helper = new DeviceCommandInvocationMarshalHelper();
			helper.setIncludeCommand(includeCommand);
			List<IDeviceCommandInvocation> converted = new ArrayList<IDeviceCommandInvocation>();
			for (IDeviceCommandInvocation invocation : matches.getResults()) {
				converted.add(helper.convert(invocation));
			}
			return new SearchResults<IDeviceCommandInvocation>(converted);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Create state change to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/statechanges", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create an state change event for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceStateChange createStateChange(@RequestBody DeviceStateChangeCreateRequest input,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createStateChange", LOGGER);
		try {
			IDeviceStateChange result =
					SiteWhere.getServer().getDeviceManagement().addDeviceStateChange(token, input);
			return DeviceStateChange.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List device state changes for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/statechanges", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List state change events for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceStateChange> listStateChanges(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listStateChanges", LOGGER);
		try {
			DateRangeSearchCriteria criteria =
					new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
			return SiteWhere.getServer().getDeviceManagement().listDeviceStateChanges(token, criteria);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Create command response to be associated with a device assignment.
	 * 
	 * @param input
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/responses", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Create an command response event for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceCommandResponse createCommandResponse(@RequestBody DeviceCommandResponseCreateRequest input,
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "createCommandResponse", LOGGER);
		try {
			IDeviceCommandResponse result =
					SiteWhere.getServer().getDeviceManagement().addDeviceCommandResponse(token, input);
			return DeviceCommandResponse.copy(result);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * List device command responses for a given assignment.
	 * 
	 * @param assignmentToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/responses", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "List command response events for a device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public ISearchResults<IDeviceCommandResponse> listCommandResponses(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listCommandResponses", LOGGER);
		try {
			DateRangeSearchCriteria criteria =
					new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
			return SiteWhere.getServer().getDeviceManagement().listDeviceCommandResponses(token, criteria);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * End an existing device assignment.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/end", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "End an active device assignment")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceAssignment endDeviceAssignment(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "endDeviceAssignment", LOGGER);
		try {
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement();
			IDeviceAssignment updated = management.endDeviceAssignment(token);
			DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
			helper.setIncludeAsset(true);
			helper.setIncludeDevice(true);
			helper.setIncludeSite(true);
			return helper.convert(updated, SiteWhere.getServer().getAssetModuleManager());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Mark a device assignment as missing.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{token}/missing", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "Mark a device assignment as missing")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public DeviceAssignment missingDeviceAssignment(
			@ApiParam(value = "Assignment token", required = true) @PathVariable String token)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "missingDeviceAssignment", LOGGER);
		try {
			IDeviceManagement management = SiteWhere.getServer().getDeviceManagement();
			IDeviceAssignment updated =
					management.updateDeviceAssignmentStatus(token, DeviceAssignmentStatus.Missing);
			DeviceAssignmentMarshalHelper helper = new DeviceAssignmentMarshalHelper();
			helper.setIncludeAsset(true);
			helper.setIncludeDevice(true);
			helper.setIncludeSite(true);
			return helper.convert(updated, SiteWhere.getServer().getAssetModuleManager());
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Get an assignment by unique token. Throw an exception if not found.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceAssignment assureAssignment(String token) throws SiteWhereException {
		IDeviceAssignment assignment =
				SiteWhere.getServer().getDeviceManagement().getDeviceAssignmentByToken(token);
		if (assignment == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceAssignmentToken, ErrorLevel.ERROR);
		}
		return assignment;
	}

	/**
	 * Get a device command by unique token. Throw an exception if not found.
	 * 
	 * @param token
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceCommand assureDeviceCommand(String token) throws SiteWhereException {
		IDeviceCommand command = SiteWhere.getServer().getDeviceManagement().getDeviceCommandByToken(token);
		if (command == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidDeviceCommandToken, ErrorLevel.ERROR);
		}
		return command;
	}
}