/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.sitewhere.grpc.model.CommonModel.GAreaReference;
import com.sitewhere.grpc.model.CommonModel.GDeviceAssignmentStatus;
import com.sitewhere.grpc.model.CommonModel.GDeviceContainerPolicy;
import com.sitewhere.grpc.model.CommonModel.GDeviceGroupElementType;
import com.sitewhere.grpc.model.CommonModel.GOptionalBoolean;
import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.grpc.model.CommonModel.GPaging;
import com.sitewhere.grpc.model.CommonModel.GParameterType;
import com.sitewhere.grpc.model.DeviceModel.GArea;
import com.sitewhere.grpc.model.DeviceModel.GAreaCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GAreaMapData;
import com.sitewhere.grpc.model.DeviceModel.GAreaSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GAreaSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GAreaType;
import com.sitewhere.grpc.model.DeviceModel.GAreaTypeCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GAreaTypeSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GAreaTypeSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GAssetsForAssignmentSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GCommandParameter;
import com.sitewhere.grpc.model.DeviceModel.GDevice;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignment;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentHistoryCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommand;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommandCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommandSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceElementMapping;
import com.sitewhere.grpc.model.DeviceModel.GDeviceElementSchema;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroup;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupElement;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupElementCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupElementsSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupElementsSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupsWithRoleSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSlot;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatus;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatusCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatusSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStream;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStreamCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStreamSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStreamSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceType;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeReference;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceUnit;
import com.sitewhere.grpc.model.DeviceModel.GZone;
import com.sitewhere.grpc.model.DeviceModel.GZoneCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GZoneSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GZoneSearchResults;
import com.sitewhere.rest.model.area.Area;
import com.sitewhere.rest.model.area.AreaMapData;
import com.sitewhere.rest.model.area.AreaType;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.rest.model.area.request.AreaCreateRequest;
import com.sitewhere.rest.model.area.request.AreaTypeCreateRequest;
import com.sitewhere.rest.model.area.request.ZoneCreateRequest;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.DeviceStatus;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStatusCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStreamCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceTypeCreateRequest;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.area.AreaSearchCriteria;
import com.sitewhere.rest.model.search.device.AssignmentSearchCriteria;
import com.sitewhere.rest.model.search.device.AssignmentsForAssetSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaMapData;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.area.request.IAreaCreateRequest;
import com.sitewhere.spi.area.request.IAreaTypeCreateRequest;
import com.sitewhere.spi.area.request.IZoneCreateRequest;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.ParameterType;
import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.element.IDeviceSlot;
import com.sitewhere.spi.device.element.IDeviceUnit;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Convert device entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class DeviceModelConverter {

    /**
     * Convert device container policy from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceContainerPolicy asApiDeviceContainerPolicy(GDeviceContainerPolicy grpc)
	    throws SiteWhereException {
	switch (grpc) {
	case CONTAINER_COMPOSITE:
	    return DeviceContainerPolicy.Composite;
	case CONTAINER_STANDALONE:
	    return DeviceContainerPolicy.Standalone;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown device container policy: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert device container policy from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceContainerPolicy asGrpcDeviceContainerPolicy(DeviceContainerPolicy api)
	    throws SiteWhereException {
	switch (api) {
	case Composite:
	    return GDeviceContainerPolicy.CONTAINER_COMPOSITE;
	case Standalone:
	    return GDeviceContainerPolicy.CONTAINER_STANDALONE;
	}
	throw new SiteWhereException("Unknown device container policy: " + api.name());
    }

    /**
     * Convert device slot from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceSlot asApiDeviceSlot(GDeviceSlot grpc) throws SiteWhereException {
	DeviceSlot api = new DeviceSlot();
	api.setName(grpc.getName());
	api.setPath(grpc.getPath());
	return api;
    }

    /**
     * Convert device slot from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceSlot asGrpcDeviceSlot(IDeviceSlot api) throws SiteWhereException {
	GDeviceSlot.Builder grpc = GDeviceSlot.newBuilder();
	grpc.setName(api.getName());
	grpc.setPath(api.getPath());
	return grpc.build();
    }

    /**
     * Convert list of device slots from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceSlot> asApiDeviceSlots(List<GDeviceSlot> grpcs) throws SiteWhereException {
	List<DeviceSlot> api = new ArrayList<DeviceSlot>();
	for (GDeviceSlot gslot : grpcs) {
	    api.add(DeviceModelConverter.asApiDeviceSlot(gslot));
	}
	return api;
    }

    /**
     * Convert list of device slots from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceSlot> asGrpcDeviceSlots(List<IDeviceSlot> apis) throws SiteWhereException {
	List<GDeviceSlot> grpcs = new ArrayList<GDeviceSlot>();
	for (IDeviceSlot api : apis) {
	    grpcs.add(DeviceModelConverter.asGrpcDeviceSlot(api));
	}
	return grpcs;
    }

    /**
     * Convert device unit from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceUnit asApiDeviceUnit(GDeviceUnit grpc) throws SiteWhereException {
	DeviceUnit api = new DeviceUnit();
	api.setName(grpc.getName());
	api.setPath(grpc.getPath());
	api.setDeviceSlots(DeviceModelConverter.asApiDeviceSlots(grpc.getSlotsList()));
	api.setDeviceUnits(DeviceModelConverter.asApiDeviceUnits(grpc.getUnitsList()));
	return api;
    }

    /**
     * Convert device unit from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceUnit asGrpcDeviceUnit(IDeviceUnit api) throws SiteWhereException {
	GDeviceUnit.Builder grpc = GDeviceUnit.newBuilder();
	grpc.setName(api.getName());
	grpc.setPath(api.getPath());
	grpc.addAllSlots(DeviceModelConverter.asGrpcDeviceSlots(api.getDeviceSlots()));
	grpc.addAllUnits(DeviceModelConverter.asGrpcDeviceUnits(api.getDeviceUnits()));
	return grpc.build();
    }

    /**
     * Convert list of device units from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceUnit> asGrpcDeviceUnits(List<IDeviceUnit> apis) throws SiteWhereException {
	List<GDeviceUnit> grpcs = new ArrayList<GDeviceUnit>();
	for (IDeviceUnit api : apis) {
	    grpcs.add(DeviceModelConverter.asGrpcDeviceUnit(api));
	}
	return grpcs;
    }

    /**
     * Convert list of device units from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceUnit> asApiDeviceUnits(List<GDeviceUnit> grpcs) throws SiteWhereException {
	List<DeviceUnit> api = new ArrayList<DeviceUnit>();
	for (GDeviceUnit gunit : grpcs) {
	    api.add(DeviceModelConverter.asApiDeviceUnit(gunit));
	}
	return api;
    }

    /**
     * Convert device element schema from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceElementSchema asApiDeviceElementSchema(GDeviceElementSchema grpc) throws SiteWhereException {
	DeviceElementSchema api = new DeviceElementSchema();
	api.setDeviceSlots(DeviceModelConverter.asApiDeviceSlots(grpc.getSlotsList()));
	api.setDeviceUnits(DeviceModelConverter.asApiDeviceUnits(grpc.getUnitsList()));
	return api;
    }

    /**
     * Convert device element schema from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceElementSchema asGrpcDeviceElementSchema(IDeviceElementSchema api) throws SiteWhereException {
	GDeviceElementSchema.Builder grpc = GDeviceElementSchema.newBuilder();
	grpc.addAllSlots(DeviceModelConverter.asGrpcDeviceSlots(api.getDeviceSlots()));
	grpc.addAllUnits(DeviceModelConverter.asGrpcDeviceUnits(api.getDeviceUnits()));
	return grpc.build();
    }

    /**
     * Convert device type search criteria from API to GRPC.
     * 
     * @param includeDeleted
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceTypeSearchCriteria asApiDeviceTypeSearchCriteria(boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	GDeviceTypeSearchCriteria.Builder gcriteria = GDeviceTypeSearchCriteria.newBuilder();
	if (includeDeleted) {
	    gcriteria.setIncludeDeleted(GOptionalBoolean.newBuilder().setValue(true).build());
	}
	if (criteria != null) {
	    gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	}
	return gcriteria.build();
    }

    /**
     * Convert device type search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceType> asApiDeviceTypeSearchResults(GDeviceTypeSearchResults response)
	    throws SiteWhereException {
	List<IDeviceType> results = new ArrayList<IDeviceType>();
	for (GDeviceType grpc : response.getDeviceTypesList()) {
	    results.add(DeviceModelConverter.asApiDeviceType(grpc));
	}
	return new SearchResults<IDeviceType>(results, response.getCount());
    }

    /**
     * Convert device type create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceTypeCreateRequest asApiDeviceTypeCreateRequest(GDeviceTypeCreateRequest grpc)
	    throws SiteWhereException {
	DeviceTypeCreateRequest api = new DeviceTypeCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setName(grpc.getName());
	api.setAssetTypeToken(grpc.getAssetTypeToken());
	api.setContainerPolicy(DeviceModelConverter.asApiDeviceContainerPolicy(grpc.getContainerPolicy()));
	api.setDeviceElementSchema(DeviceModelConverter.asApiDeviceElementSchema(grpc.getDeviceElementSchema()));
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert device type create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceTypeCreateRequest asGrpcDeviceTypeCreateRequest(IDeviceTypeCreateRequest api)
	    throws SiteWhereException {
	GDeviceTypeCreateRequest.Builder grpc = GDeviceTypeCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setName(api.getName());
	grpc.setAssetTypeToken(api.getAssetTypeToken());
	grpc.setContainerPolicy(DeviceModelConverter.asGrpcDeviceContainerPolicy(api.getContainerPolicy()));
	if (api.getDeviceElementSchema() != null) {
	    grpc.setDeviceElementSchema(DeviceModelConverter.asGrpcDeviceElementSchema(api.getDeviceElementSchema()));
	}
	grpc.putAllMetadata(api.getMetadata());
	return grpc.build();
    }

    /**
     * Convert device type from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceType asApiDeviceType(GDeviceType grpc) throws SiteWhereException {
	DeviceType api = new DeviceType();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setToken(grpc.getToken());
	api.setAssetTypeId(CommonModelConverter.asApiUuid(grpc.getAssetTypeId()));
	api.setName(grpc.getName());
	api.setContainerPolicy(DeviceModelConverter.asApiDeviceContainerPolicy(grpc.getContainerPolicy()));
	api.setDeviceElementSchema(DeviceModelConverter.asApiDeviceElementSchema(grpc.getDeviceElementSchema()));
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert device type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceType asGrpcDeviceType(IDeviceType api) throws SiteWhereException {
	GDeviceType.Builder grpc = GDeviceType.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setToken(api.getToken());
	grpc.setAssetTypeId(CommonModelConverter.asGrpcUuid(api.getAssetTypeId()));
	grpc.setName(api.getName());
	grpc.setContainerPolicy(DeviceModelConverter.asGrpcDeviceContainerPolicy(api.getContainerPolicy()));
	if (api.getDeviceElementSchema() != null) {
	    grpc.setDeviceElementSchema(DeviceModelConverter.asGrpcDeviceElementSchema(api.getDeviceElementSchema()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert parameter type from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ParameterType asApiParameterType(GParameterType grpc) throws SiteWhereException {
	switch (grpc) {
	case PARAMETER_BOOL:
	    return ParameterType.Bool;
	case PARAMETER_BYTES:
	    return ParameterType.Bytes;
	case PARAMETER_DOUBLE:
	    return ParameterType.Double;
	case PARAMETER_FIXED32:
	    return ParameterType.Fixed32;
	case PARAMETER_FIXED64:
	    return ParameterType.Fixed64;
	case PARAMETER_FLOAT:
	    return ParameterType.Float;
	case PARAMETER_INT32:
	    return ParameterType.Int32;
	case PARAMETER_INT64:
	    return ParameterType.Int64;
	case PARAMETER_SFIXED32:
	    return ParameterType.SFixed32;
	case PARAMETER_SFIXED64:
	    return ParameterType.SFixed64;
	case PARAMETER_SINT32:
	    return ParameterType.SInt32;
	case PARAMETER_SINT64:
	    return ParameterType.SInt64;
	case PARAMETER_STRING:
	    return ParameterType.String;
	case PARAMETER_UINT32:
	    return ParameterType.UInt32;
	case PARAMETER_UINT64:
	    return ParameterType.UInt64;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown parameter type: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert parameter type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GParameterType asGrpcParameterType(ParameterType api) throws SiteWhereException {
	switch (api) {
	case Bool:
	    return GParameterType.PARAMETER_BOOL;
	case Bytes:
	    return GParameterType.PARAMETER_BYTES;
	case Double:
	    return GParameterType.PARAMETER_DOUBLE;
	case Fixed32:
	    return GParameterType.PARAMETER_FIXED32;
	case Fixed64:
	    return GParameterType.PARAMETER_FIXED64;
	case Float:
	    return GParameterType.PARAMETER_FLOAT;
	case Int32:
	    return GParameterType.PARAMETER_INT32;
	case Int64:
	    return GParameterType.PARAMETER_INT64;
	case SFixed32:
	    return GParameterType.PARAMETER_SFIXED32;
	case SFixed64:
	    return GParameterType.PARAMETER_SFIXED64;
	case SInt32:
	    return GParameterType.PARAMETER_SINT32;
	case SInt64:
	    return GParameterType.PARAMETER_SINT64;
	case String:
	    return GParameterType.PARAMETER_STRING;
	case UInt32:
	    return GParameterType.PARAMETER_UINT32;
	case UInt64:
	    return GParameterType.PARAMETER_UINT64;
	}
	throw new SiteWhereException("Unknown parameter type: " + api.name());
    }

    /**
     * Convert command parameter from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static CommandParameter asApiCommandParameter(GCommandParameter grpc) throws SiteWhereException {
	CommandParameter api = new CommandParameter();
	api.setName(grpc.getName());
	api.setRequired(grpc.getRequired());
	api.setType(DeviceModelConverter.asApiParameterType(grpc.getType()));
	return api;
    }

    /**
     * Convert list of command parameters from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<CommandParameter> asApiCommandParameters(List<GCommandParameter> grpcs)
	    throws SiteWhereException {
	List<CommandParameter> api = new ArrayList<CommandParameter>();
	for (GCommandParameter grpc : grpcs) {
	    api.add(DeviceModelConverter.asApiCommandParameter(grpc));
	}
	return api;
    }

    /**
     * Convert command parameter from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GCommandParameter asGrpcCommandParameter(ICommandParameter api) throws SiteWhereException {
	GCommandParameter.Builder grpc = GCommandParameter.newBuilder();
	grpc.setName(api.getName());
	grpc.setRequired(api.isRequired());
	grpc.setType(DeviceModelConverter.asGrpcParameterType(api.getType()));
	return grpc.build();
    }

    /**
     * Convert command parameter from GRPC to API.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GCommandParameter> asGrpcCommandParameters(List<ICommandParameter> apis)
	    throws SiteWhereException {
	List<GCommandParameter> grpcs = new ArrayList<GCommandParameter>();
	for (ICommandParameter api : apis) {
	    grpcs.add(DeviceModelConverter.asGrpcCommandParameter(api));
	}
	return grpcs;
    }

    /**
     * Convert device command search criteria from API to GRPC.
     * 
     * @param includeDeleted
     * @param deviceTypeId
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandSearchCriteria asApiDeviceCommandSearchCriteria(boolean includeDeleted,
	    UUID deviceTypeId) throws SiteWhereException {
	GDeviceCommandSearchCriteria.Builder gcriteria = GDeviceCommandSearchCriteria.newBuilder();
	if (includeDeleted) {
	    gcriteria.setIncludeDeleted(GOptionalBoolean.newBuilder().setValue(true).build());
	}
	if (deviceTypeId != null) {
	    gcriteria.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
	}
	return gcriteria.build();
    }

    /**
     * Convert device command search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static List<IDeviceCommand> asApiDeviceCommandSearchResults(List<GDeviceCommand> response)
	    throws SiteWhereException {
	List<IDeviceCommand> results = new ArrayList<IDeviceCommand>();
	for (GDeviceCommand grpc : response) {
	    results.add(DeviceModelConverter.asApiDeviceCommand(grpc));
	}
	return results;
    }

    /**
     * Convert device command create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandCreateRequest asApiDeviceCommandCreateRequest(GDeviceCommandCreateRequest grpc)
	    throws SiteWhereException {
	DeviceCommandCreateRequest api = new DeviceCommandCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setNamespace(grpc.getNamespace());
	api.setParameters(DeviceModelConverter.asApiCommandParameters(grpc.getParametersList()));
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert device command create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandCreateRequest asGrpcDeviceCommandCreateRequest(IDeviceCommandCreateRequest api)
	    throws SiteWhereException {
	GDeviceCommandCreateRequest.Builder grpc = GDeviceCommandCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setNamespace(api.getNamespace());
	grpc.addAllParameters(DeviceModelConverter.asGrpcCommandParameters(api.getParameters()));
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device command from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommand asApiDeviceCommand(GDeviceCommand grpc) throws SiteWhereException {
	DeviceCommand api = new DeviceCommand();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setToken(grpc.getToken());
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setNamespace(grpc.getNamespace());
	api.setParameters(DeviceModelConverter.asApiCommandParameters(grpc.getParametersList()));
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert device command from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommand asGrpcDeviceCommand(IDeviceCommand api) throws SiteWhereException {
	GDeviceCommand.Builder grpc = GDeviceCommand.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setToken(api.getToken());
	grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setNamespace(api.getNamespace());
	grpc.addAllParameters(DeviceModelConverter.asGrpcCommandParameters(api.getParameters()));
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert device status search criteria from API to GRPC.
     * 
     * @param specificationId
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStatusSearchCriteria asApiDeviceStatusSearchCriteria(UUID deviceTypeId, String code)
	    throws SiteWhereException {
	GDeviceStatusSearchCriteria.Builder gcriteria = GDeviceStatusSearchCriteria.newBuilder();
	if (deviceTypeId != null) {
	    gcriteria.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
	}
	if (code != null) {
	    gcriteria.setCode(GOptionalString.newBuilder().setValue(code).build());
	}
	return gcriteria.build();
    }

    /**
     * Convert device status search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static List<IDeviceStatus> asApiDeviceStatusSearchResults(List<GDeviceStatus> response)
	    throws SiteWhereException {
	List<IDeviceStatus> results = new ArrayList<IDeviceStatus>();
	for (GDeviceStatus grpc : response) {
	    results.add(DeviceModelConverter.asApiDeviceStatus(grpc));
	}
	return results;
    }

    /**
     * Convert device status create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStatusCreateRequest asApiDeviceStatusCreateRequest(GDeviceStatusCreateRequest grpc)
	    throws SiteWhereException {
	DeviceStatusCreateRequest api = new DeviceStatusCreateRequest();
	api.setCode(grpc.getCode());
	api.setName(grpc.getName());
	api.setBackgroundColor(grpc.getBackgroundColor());
	api.setForegroundColor(grpc.getForegroundColor());
	api.setBorderColor(grpc.getBorderColor());
	api.setIcon(grpc.getIcon());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert device status create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStatusCreateRequest asGrpcDeviceStatusCreateRequest(IDeviceStatusCreateRequest api)
	    throws SiteWhereException {
	GDeviceStatusCreateRequest.Builder grpc = GDeviceStatusCreateRequest.newBuilder();
	grpc.setCode(api.getCode());
	grpc.setName(api.getName());
	grpc.setBackgroundColor(api.getBackgroundColor());
	grpc.setForegroundColor(api.getForegroundColor());
	grpc.setBorderColor(api.getBorderColor());
	grpc.setIcon(api.getIcon());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device status from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStatus asApiDeviceStatus(GDeviceStatus grpc) throws SiteWhereException {
	DeviceStatus api = new DeviceStatus();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setCode(grpc.getCode());
	api.setName(grpc.getName());
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setBackgroundColor(grpc.getBackgroundColor());
	api.setForegroundColor(grpc.getForegroundColor());
	api.setBorderColor(grpc.getBorderColor());
	api.setIcon(grpc.getIcon());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert device status from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStatus asGrpcDeviceStatus(IDeviceStatus api) throws SiteWhereException {
	GDeviceStatus.Builder grpc = GDeviceStatus.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setCode(api.getCode());
	grpc.setName(api.getName());
	grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	grpc.setBackgroundColor(api.getBackgroundColor());
	grpc.setForegroundColor(api.getForegroundColor());
	grpc.setBorderColor(api.getBorderColor());
	grpc.setIcon(api.getIcon());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device element mapping from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceElementMapping asApiDeviceElementMapping(GDeviceElementMapping grpc) throws SiteWhereException {
	DeviceElementMapping api = new DeviceElementMapping();
	api.setDeviceToken(grpc.getDeviceToken());
	api.setDeviceElementSchemaPath(grpc.getSchemaPath());
	return api;
    }

    /**
     * Convert list of device element mappgings from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceElementMapping> asApiDeviceElementMappings(List<GDeviceElementMapping> grpcs)
	    throws SiteWhereException {
	List<DeviceElementMapping> api = new ArrayList<DeviceElementMapping>();
	for (GDeviceElementMapping grpc : grpcs) {
	    api.add(DeviceModelConverter.asApiDeviceElementMapping(grpc));
	}
	return api;
    }

    /**
     * Convert device element mapping from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceElementMapping asGrpcDeviceElementMapping(IDeviceElementMapping api)
	    throws SiteWhereException {
	GDeviceElementMapping.Builder grpc = GDeviceElementMapping.newBuilder();
	grpc.setDeviceToken(api.getDeviceToken());
	grpc.setSchemaPath(api.getDeviceElementSchemaPath());
	return grpc.build();
    }

    /**
     * Convert list of device element mappings from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceElementMapping> asGrpcDeviceElementMappings(List<IDeviceElementMapping> apis)
	    throws SiteWhereException {
	List<GDeviceElementMapping> grpcs = new ArrayList<GDeviceElementMapping>();
	if (apis != null) {
	    for (IDeviceElementMapping api : apis) {
		grpcs.add(DeviceModelConverter.asGrpcDeviceElementMapping(api));
	    }
	}
	return grpcs;
    }

    /**
     * Convert device search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDevice> asApiDeviceSearchResults(GDeviceSearchResults response)
	    throws SiteWhereException {
	List<IDevice> results = new ArrayList<IDevice>();
	for (GDevice grpc : response.getDevicesList()) {
	    results.add(DeviceModelConverter.asApiDevice(grpc));
	}
	return new SearchResults<IDevice>(results, response.getCount());
    }

    /**
     * Convert device create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCreateRequest asApiDeviceCreateRequest(GDeviceCreateRequest grpc) throws SiteWhereException {
	DeviceCreateRequest api = new DeviceCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setParentDeviceToken(grpc.hasParentDeviceToken() ? grpc.getParentDeviceToken().getValue() : null);
	api.setDeviceTypeToken(grpc.getDeviceTypeToken());
	api.setStatus(grpc.hasStatus() ? grpc.getStatus().getValue() : null);
	api.setComments(grpc.hasComments() ? grpc.getComments().getValue() : null);
	api.setDeviceElementMappings(
		DeviceModelConverter.asApiDeviceElementMappings(grpc.getDeviceElementMappingsList()));
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert device create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCreateRequest asGrpcDeviceCreateRequest(IDeviceCreateRequest api) throws SiteWhereException {
	GDeviceCreateRequest.Builder grpc = GDeviceCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	if (api.getParentDeviceToken() != null) {
	    grpc.setParentDeviceToken(GOptionalString.newBuilder().setValue(api.getParentDeviceToken()));
	}
	grpc.setDeviceTypeToken(api.getDeviceTypeToken());
	if (api.getStatus() != null) {
	    grpc.setStatus(GOptionalString.newBuilder().setValue(api.getStatus()));
	}
	if (api.getComments() != null) {
	    grpc.setComments(GOptionalString.newBuilder().setValue(api.getComments()));
	}
	grpc.addAllDeviceElementMappings(
		DeviceModelConverter.asGrpcDeviceElementMappings(api.getDeviceElementMappings()));
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static Device asApiDevice(GDevice grpc) throws SiteWhereException {
	Device api = new Device();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setToken(grpc.getToken());
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setStatus(grpc.hasStatus() ? grpc.getStatus().getValue() : null);
	api.setDeviceAssignmentId(
		grpc.hasDeviceAssignmentId() ? CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()) : null);
	api.setParentDeviceId(
		grpc.hasParentDeviceId() ? CommonModelConverter.asApiUuid(grpc.getParentDeviceId()) : null);
	api.setComments(grpc.hasComments() ? grpc.getComments().getValue() : null);
	api.setDeviceElementMappings(
		DeviceModelConverter.asApiDeviceElementMappings(grpc.getDeviceElementMappingsList()));
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert device from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDevice asGrpcDevice(IDevice api) throws SiteWhereException {
	GDevice.Builder grpc = GDevice.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setToken(api.getToken());
	if (api.getParentDeviceId() != null) {
	    grpc.setParentDeviceId(CommonModelConverter.asGrpcUuid(api.getParentDeviceId()));
	}
	grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	if (api.getStatus() != null) {
	    grpc.setStatus(GOptionalString.newBuilder().setValue(api.getStatus()).build());
	}
	if (api.getDeviceAssignmentId() != null) {
	    grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	}
	if (api.getComments() != null) {
	    grpc.setComments(GOptionalString.newBuilder().setValue(api.getComments()).build());
	}
	grpc.addAllDeviceElementMappings(
		DeviceModelConverter.asGrpcDeviceElementMappings(api.getDeviceElementMappings()));
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert device search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceSearchCriteria asApiDeviceSearchCriteria(GDeviceSearchCriteria grpc) throws SiteWhereException {
	int pageNumber = grpc.hasPaging() ? grpc.getPaging().getPageNumber() : 1;
	int pageSize = grpc.hasPaging() ? grpc.getPaging().getPageSize() : 0;
	Date createdAfter = grpc.hasCreatedAfter() ? CommonModelConverter.asDate(grpc.getCreatedAfter()) : null;
	Date createdBefore = grpc.hasCreatedBefore() ? CommonModelConverter.asDate(grpc.getCreatedBefore()) : null;
	DeviceSearchCriteria api = new DeviceSearchCriteria(pageNumber, pageSize, createdAfter, createdBefore);
	api.setExcludeAssigned(grpc.hasExcludeAssigned() ? grpc.getExcludeAssigned().getValue() : false);
	api.setDeviceTypeToken(grpc.hasDeviceType() ? grpc.getDeviceType().getToken() : null);
	return api;
    }

    /**
     * Convert device search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceSearchCriteria asGrpcDeviceSearchCriteria(IDeviceSearchCriteria api)
	    throws SiteWhereException {
	GDeviceSearchCriteria.Builder grpc = GDeviceSearchCriteria.newBuilder();
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	if (api.getDeviceTypeToken() != null) {
	    grpc.setDeviceType(GDeviceTypeReference.newBuilder().setToken(api.getDeviceTypeToken()).build());
	}
	if (api.getStartDate() != null) {
	    grpc.setCreatedAfter(CommonModelConverter.asGrpcTimestamp(api.getStartDate()));
	}
	if (api.getEndDate() != null) {
	    grpc.setCreatedBefore(CommonModelConverter.asGrpcTimestamp(api.getEndDate()));
	}
	if (api.isExcludeAssigned()) {
	    grpc.setExcludeAssigned(GOptionalBoolean.newBuilder().setValue(true));
	}
	return grpc.build();
    }

    /**
     * Convert a device group create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceGroupCreateRequest asApiDeviceGroupCreateRequest(GDeviceGroupCreateRequest grpc)
	    throws SiteWhereException {
	DeviceGroupCreateRequest api = new DeviceGroupCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setRoles(grpc.getRolesList());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert a device group group request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceGroupCreateRequest asGrpcDeviceGroupCreateRequest(IDeviceGroupCreateRequest api)
	    throws SiteWhereException {
	GDeviceGroupCreateRequest.Builder grpc = GDeviceGroupCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.addAllRoles(api.getRoles());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device group search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceGroupSearchCriteria asApiDeviceGroupSearchCriteria(ISearchCriteria criteria)
	    throws SiteWhereException {
	GDeviceGroupSearchCriteria.Builder gcriteria = GDeviceGroupSearchCriteria.newBuilder();
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	return gcriteria.build();
    }

    /**
     * Convert device groups with role search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceGroupsWithRoleSearchCriteria asApiDeviceGroupsWithRoleSearchCriteria(String role,
	    boolean includeDeleted, ISearchCriteria criteria) throws SiteWhereException {
	GDeviceGroupsWithRoleSearchCriteria.Builder gcriteria = GDeviceGroupsWithRoleSearchCriteria.newBuilder();
	gcriteria.setRole(role);
	gcriteria.setIncludeDeleted(includeDeleted ? GOptionalBoolean.newBuilder().setValue(true).build() : null);
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	return gcriteria.build();
    }

    /**
     * Convert device group search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceGroup> asApiDeviceGroupSearchResults(GDeviceGroupSearchResults response)
	    throws SiteWhereException {
	List<IDeviceGroup> results = new ArrayList<IDeviceGroup>();
	for (GDeviceGroup grpc : response.getDeviceGroupsList()) {
	    results.add(DeviceModelConverter.asApiDeviceGroup(grpc));
	}
	return new SearchResults<IDeviceGroup>(results, response.getCount());
    }

    /**
     * Convert a device group from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceGroup asApiDeviceGroup(GDeviceGroup grpc) throws SiteWhereException {
	DeviceGroup api = new DeviceGroup();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setToken(grpc.getToken());
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setRoles(grpc.getRolesList());
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert a device group from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceGroup asGrpcDeviceGroup(IDeviceGroup api) throws SiteWhereException {
	GDeviceGroup.Builder grpc = GDeviceGroup.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setToken(api.getToken());
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.addAllRoles(api.getRoles());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert a device group element type from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static GroupElementType asApiDeviceGroupElementType(GDeviceGroupElementType grpc) throws SiteWhereException {
	switch (grpc) {
	case ELEMENT_TYPE_DEVICE:
	    return GroupElementType.Device;
	case ELEMENT_TYPE_GROUP:
	    return GroupElementType.Group;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown device group element type: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert a device group element type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceGroupElementType asGrpcDeviceGroupElementType(GroupElementType api) throws SiteWhereException {
	switch (api) {
	case Device:
	    return GDeviceGroupElementType.ELEMENT_TYPE_DEVICE;
	case Group:
	    return GDeviceGroupElementType.ELEMENT_TYPE_GROUP;
	}
	throw new SiteWhereException("Unknown device group element type: " + api.name());
    }

    /**
     * Convert a device group element create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceGroupElementCreateRequest asApiDeviceGroupElementCreateRequest(
	    GDeviceGroupElementCreateRequest grpc) throws SiteWhereException {
	DeviceGroupElementCreateRequest api = new DeviceGroupElementCreateRequest();
	api.setType(DeviceModelConverter.asApiDeviceGroupElementType(grpc.getType()));
	api.setElementId(grpc.getElementId());
	api.setRoles(grpc.getRolesList());
	return api;
    }

    /**
     * Convert a list of device group element create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<IDeviceGroupElementCreateRequest> asApiDeviceGroupElementCreateRequests(
	    List<GDeviceGroupElementCreateRequest> grpcs) throws SiteWhereException {
	List<IDeviceGroupElementCreateRequest> api = new ArrayList<IDeviceGroupElementCreateRequest>();
	for (GDeviceGroupElementCreateRequest grpc : grpcs) {
	    api.add(DeviceModelConverter.asApiDeviceGroupElementCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert a device group element create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceGroupElementCreateRequest asGrpcDeviceGroupElementCreateRequest(
	    IDeviceGroupElementCreateRequest api) throws SiteWhereException {
	GDeviceGroupElementCreateRequest.Builder grpc = GDeviceGroupElementCreateRequest.newBuilder();
	grpc.setType(DeviceModelConverter.asGrpcDeviceGroupElementType(api.getType()));
	grpc.setElementId(api.getElementId());
	grpc.addAllRoles(api.getRoles());
	return grpc.build();
    }

    /**
     * Convert a list of device group element create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceGroupElementCreateRequest> asGrpcDeviceGroupElementCreateRequests(
	    List<IDeviceGroupElementCreateRequest> apis) throws SiteWhereException {
	List<GDeviceGroupElementCreateRequest> grpcs = new ArrayList<GDeviceGroupElementCreateRequest>();
	for (IDeviceGroupElementCreateRequest api : apis) {
	    grpcs.add(DeviceModelConverter.asGrpcDeviceGroupElementCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device group element search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceGroupElementsSearchCriteria asApiDeviceGroupElementSearchCriteria(ISearchCriteria criteria)
	    throws SiteWhereException {
	GDeviceGroupElementsSearchCriteria.Builder gcriteria = GDeviceGroupElementsSearchCriteria.newBuilder();
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	return gcriteria.build();
    }

    /**
     * Convert device group element search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceGroupElement> asApiDeviceGroupElementsSearchResults(
	    GDeviceGroupElementsSearchResults response) throws SiteWhereException {
	List<IDeviceGroupElement> results = new ArrayList<IDeviceGroupElement>();
	for (GDeviceGroupElement grpc : response.getElementsList()) {
	    results.add(DeviceModelConverter.asApiDeviceGroupElement(grpc));
	}
	return new SearchResults<IDeviceGroupElement>(results, response.getCount());
    }

    /**
     * Convert a device group element from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceGroupElement asApiDeviceGroupElement(GDeviceGroupElement grpc) throws SiteWhereException {
	DeviceGroupElement api = new DeviceGroupElement();
	api.setType(DeviceModelConverter.asApiDeviceGroupElementType(grpc.getType()));
	api.setElementId(CommonModelConverter.asApiUuid(grpc.getElementId()));
	api.setRoles(grpc.getRolesList());
	return api;
    }

    /**
     * Convert a device group element list from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<IDeviceGroupElement> asApiDeviceGroupElements(List<GDeviceGroupElement> grpcs)
	    throws SiteWhereException {
	List<IDeviceGroupElement> api = new ArrayList<IDeviceGroupElement>();
	for (GDeviceGroupElement grpc : grpcs) {
	    api.add(DeviceModelConverter.asApiDeviceGroupElement(grpc));
	}
	return api;
    }

    /**
     * Convert a device group element from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceGroupElement asGrpcDeviceGroupElement(IDeviceGroupElement api) throws SiteWhereException {
	GDeviceGroupElement.Builder grpc = GDeviceGroupElement.newBuilder();
	grpc.setType(DeviceModelConverter.asGrpcDeviceGroupElementType(api.getType()));
	grpc.setElementId(CommonModelConverter.asGrpcUuid(api.getElementId()));
	grpc.addAllRoles(api.getRoles());
	return grpc.build();
    }

    /**
     * Convert device assignment search criteria from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAssignmentSearchCriteria asApiDeviceAssignmentSearchCriteria(IAssignmentSearchCriteria grpc)
	    throws SiteWhereException {
	GDeviceAssignmentSearchCriteria.Builder gcriteria = GDeviceAssignmentSearchCriteria.newBuilder();
	if (grpc.getStatus() != null) {
	    gcriteria.setStatus(DeviceModelConverter.asGrpcDeviceAssignmentStatus(grpc.getStatus()));
	}
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(grpc));
	return gcriteria.build();
    }

    /**
     * Convert device assignments for asset search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetsForAssignmentSearchCriteria asApiDeviceAssignmentSearchCriteria(
	    IAssignmentsForAssetSearchCriteria api) throws SiteWhereException {
	GAssetsForAssignmentSearchCriteria.Builder gcriteria = GAssetsForAssignmentSearchCriteria.newBuilder();
	if (api.getAreaToken() != null) {
	    gcriteria.setArea(GAreaReference.newBuilder().setToken(api.getAreaToken()).build());
	}
	if (api.getStatus() != null) {
	    gcriteria.setStatus(DeviceModelConverter.asGrpcDeviceAssignmentStatus(api.getStatus()));
	}
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(api));
	return gcriteria.build();
    }

    /**
     * Convert device assignment status from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignmentStatus asApiDeviceAssignmentStatus(GDeviceAssignmentStatus grpc)
	    throws SiteWhereException {
	switch (grpc) {
	case ASSN_STATUS_ACTIVE:
	    return DeviceAssignmentStatus.Active;
	case ASSN_STATUS_MISSING:
	    return DeviceAssignmentStatus.Missing;
	case ASSN_STATUS_RELEASED:
	    return DeviceAssignmentStatus.Released;
	case ASSN_STATUS_UNSPECIFIED:
	    return null;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown device assignment status: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert device assignment status from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAssignmentStatus asGrpcDeviceAssignmentStatus(DeviceAssignmentStatus api)
	    throws SiteWhereException {
	if (api == null) {
	    return GDeviceAssignmentStatus.ASSN_STATUS_UNSPECIFIED;
	}
	switch (api) {
	case Active:
	    return GDeviceAssignmentStatus.ASSN_STATUS_ACTIVE;
	case Missing:
	    return GDeviceAssignmentStatus.ASSN_STATUS_MISSING;
	case Released:
	    return GDeviceAssignmentStatus.ASSN_STATUS_RELEASED;
	}
	throw new SiteWhereException("Unknown device assignment status: " + api.name());
    }

    /**
     * Convert device history search criteria from API to GRPC.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAssignmentHistoryCriteria asGrpcDeviceAssignmentHistoryCriteria(ISearchCriteria criteria)
	    throws SiteWhereException {
	GDeviceAssignmentHistoryCriteria.Builder gcriteria = GDeviceAssignmentHistoryCriteria.newBuilder();
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	return gcriteria.build();
    }

    /**
     * Convert device assignment search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceAssignment> asApiDeviceAssignmentSearchResults(
	    GDeviceAssignmentSearchResults response) throws SiteWhereException {
	List<IDeviceAssignment> results = new ArrayList<IDeviceAssignment>();
	for (GDeviceAssignment grpc : response.getAssignmentsList()) {
	    results.add(DeviceModelConverter.asApiDeviceAssignment(grpc));
	}
	return new SearchResults<IDeviceAssignment>(results, response.getCount());
    }

    /**
     * Convert a device assignment create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignmentCreateRequest asApiDeviceAssignmentCreateRequest(GDeviceAssignmentCreateRequest grpc)
	    throws SiteWhereException {
	DeviceAssignmentCreateRequest api = new DeviceAssignmentCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setDeviceToken(grpc.hasDeviceToken() ? grpc.getDeviceToken().getValue() : null);
	api.setAreaToken(grpc.hasAreaToken() ? grpc.getAreaToken().getValue() : null);
	api.setAssetToken(grpc.hasAssetToken() ? grpc.getAssetToken().getValue() : null);
	api.setStatus(DeviceModelConverter.asApiDeviceAssignmentStatus(grpc.getStatus()));
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert a device assignment create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAssignmentCreateRequest asGrpcDeviceAssignmentCreateRequest(IDeviceAssignmentCreateRequest api)
	    throws SiteWhereException {
	GDeviceAssignmentCreateRequest.Builder grpc = GDeviceAssignmentCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	if (api.getDeviceToken() != null) {
	    grpc.setDeviceToken(GOptionalString.newBuilder().setValue(api.getDeviceToken()));
	}
	if (api.getAreaToken() != null) {
	    grpc.setAreaToken(GOptionalString.newBuilder().setValue(api.getAreaToken()));
	}
	if (api.getAssetToken() != null) {
	    grpc.setAssetToken(GOptionalString.newBuilder().setValue(api.getAssetToken()));
	}
	if (api.getStatus() != null) {
	    grpc.setStatus(DeviceModelConverter.asGrpcDeviceAssignmentStatus(api.getStatus()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert a device assignment from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignment asApiDeviceAssignment(GDeviceAssignment grpc) throws SiteWhereException {
	DeviceAssignment api = new DeviceAssignment();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setToken(grpc.getToken());
	api.setStatus(DeviceModelConverter.asApiDeviceAssignmentStatus(grpc.getStatus()));
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setAreaId(CommonModelConverter.asApiUuid(grpc.getAreaId()));
	api.setAssetId(CommonModelConverter.asApiUuid(grpc.getAssetId()));
	if (grpc.hasActiveDate()) {
	    api.setActiveDate(CommonModelConverter.asDate(grpc.getActiveDate()));
	}
	if (grpc.hasReleasedDate()) {
	    api.setReleasedDate(CommonModelConverter.asDate(grpc.getReleasedDate()));
	}
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert a device assignment from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAssignment asGrpcDeviceAssignment(IDeviceAssignment api) throws SiteWhereException {
	GDeviceAssignment.Builder grpc = GDeviceAssignment.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setToken(api.getToken());
	grpc.setStatus(DeviceModelConverter.asGrpcDeviceAssignmentStatus(api.getStatus()));
	grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	if (api.getAreaId() != null) {
	    grpc.setAreaId(CommonModelConverter.asGrpcUuid(api.getAreaId()));
	}
	if (api.getAssetId() != null) {
	    grpc.setAssetId(CommonModelConverter.asGrpcUuid(api.getAssetId()));
	}
	if (api.getActiveDate() != null) {
	    grpc.setActiveDate(CommonModelConverter.asGrpcTimestamp(api.getActiveDate()));
	}
	if (api.getReleasedDate() != null) {
	    grpc.setReleasedDate(CommonModelConverter.asGrpcTimestamp(api.getReleasedDate()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert device assignment search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AssignmentSearchCriteria asApiAssignmentSearchCriteria(GDeviceAssignmentSearchCriteria grpc)
	    throws SiteWhereException {
	int pageNumber = grpc.hasPaging() ? grpc.getPaging().getPageNumber() : 1;
	int pageSize = grpc.hasPaging() ? grpc.getPaging().getPageSize() : 0;
	AssignmentSearchCriteria api = new AssignmentSearchCriteria(pageNumber, pageSize);
	api.setStatus(DeviceModelConverter.asApiDeviceAssignmentStatus(grpc.getStatus()));
	return api;
    }

    /**
     * Convert device assignment search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAssignmentSearchCriteria asGrpcAssignmentSearchCriteria(IAssignmentSearchCriteria api)
	    throws SiteWhereException {
	GDeviceAssignmentSearchCriteria.Builder grpc = GDeviceAssignmentSearchCriteria.newBuilder();
	grpc.setPaging(GPaging.newBuilder().setPageNumber(api.getPageNumber()).setPageSize(api.getPageSize()).build());
	grpc.setStatus(DeviceModelConverter.asGrpcDeviceAssignmentStatus(api.getStatus()));
	return grpc.build();
    }

    /**
     * Convert assignments for asset search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AssignmentsForAssetSearchCriteria asApiAssignmentsForAssetSearchCriteria(
	    GAssetsForAssignmentSearchCriteria grpc) throws SiteWhereException {
	int pageNumber = grpc.hasPaging() ? grpc.getPaging().getPageNumber() : 1;
	int pageSize = grpc.hasPaging() ? grpc.getPaging().getPageSize() : 0;
	AssignmentsForAssetSearchCriteria api = new AssignmentsForAssetSearchCriteria(pageNumber, pageSize);
	api.setAreaToken(grpc.hasArea() ? grpc.getArea().getToken() : null);
	api.setStatus(DeviceModelConverter.asApiDeviceAssignmentStatus(grpc.getStatus()));
	return api;
    }

    /**
     * Convert assignments for asset search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAssetsForAssignmentSearchCriteria asGrpcAssignmentsForAssetSearchCriteria(
	    IAssignmentsForAssetSearchCriteria api) throws SiteWhereException {
	GAssetsForAssignmentSearchCriteria.Builder grpc = GAssetsForAssignmentSearchCriteria.newBuilder();
	grpc.setPaging(GPaging.newBuilder().setPageNumber(api.getPageNumber()).setPageSize(api.getPageSize()).build());
	grpc.setStatus(DeviceModelConverter.asGrpcDeviceAssignmentStatus(api.getStatus()));
	if (api.getAreaToken() != null) {
	    grpc.setArea(GAreaReference.newBuilder().setToken(api.getAreaToken()).build());
	}
	return grpc.build();
    }

    /**
     * Convert a device stream create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStreamCreateRequest asApiDeviceStreamCreateRequest(GDeviceStreamCreateRequest grpc)
	    throws SiteWhereException {
	DeviceStreamCreateRequest api = new DeviceStreamCreateRequest();
	api.setStreamId(grpc.getStreamId());
	api.setContentType(grpc.getContentType());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert a device stream create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStreamCreateRequest asGrpcDeviceStreamCreateRequest(IDeviceStreamCreateRequest api)
	    throws SiteWhereException {
	GDeviceStreamCreateRequest.Builder grpc = GDeviceStreamCreateRequest.newBuilder();
	grpc.setStreamId(api.getStreamId());
	grpc.setContentType(api.getContentType());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device stream search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStreamSearchCriteria asApiDeviceStreamSearchCriteria(ISearchCriteria criteria)
	    throws SiteWhereException {
	GDeviceStreamSearchCriteria.Builder gcriteria = GDeviceStreamSearchCriteria.newBuilder();
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	return gcriteria.build();
    }

    /**
     * Convert device stream search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceStream> asApiDeviceStreamSearchResults(GDeviceStreamSearchResults response)
	    throws SiteWhereException {
	List<IDeviceStream> results = new ArrayList<IDeviceStream>();
	for (GDeviceStream grpc : response.getStreamsList()) {
	    results.add(DeviceModelConverter.asApiDeviceStream(grpc));
	}
	return new SearchResults<IDeviceStream>(results, response.getCount());
    }

    /**
     * Convert a device stream from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStream asApiDeviceStream(GDeviceStream grpc) throws SiteWhereException {
	DeviceStream api = new DeviceStream();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setStreamId(grpc.getStreamId());
	api.setContentType(grpc.getContentType());
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert a device stream from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStream asGrpcDeviceStream(IDeviceStream api) throws SiteWhereException {
	GDeviceStream.Builder grpc = GDeviceStream.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setStreamId(api.getStreamId());
	grpc.setContentType(api.getContentType());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert area type create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AreaTypeCreateRequest asApiAreaTypeCreateRequest(GAreaTypeCreateRequest grpc)
	    throws SiteWhereException {
	AreaTypeCreateRequest api = new AreaTypeCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setIcon(grpc.getIcon());
	api.setContainedAreaTypeTokens(grpc.getContainedAreaTypeTokensList());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert area type create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAreaTypeCreateRequest asGrpcAreaTypeCreateRequest(IAreaTypeCreateRequest api)
	    throws SiteWhereException {
	GAreaTypeCreateRequest.Builder grpc = GAreaTypeCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setIcon(api.getIcon());
	if (api.getContainedAreaTypeTokens() != null) {
	    grpc.addAllContainedAreaTypeTokens(api.getContainedAreaTypeTokens());
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert area type search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GAreaTypeSearchCriteria asApiAreaTypeSearchCriteria(ISearchCriteria criteria)
	    throws SiteWhereException {
	GAreaTypeSearchCriteria.Builder gcriteria = GAreaTypeSearchCriteria.newBuilder();
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	return gcriteria.build();
    }

    /**
     * Convert area type search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IAreaType> asApiAreaTypeSearchResults(GAreaTypeSearchResults response)
	    throws SiteWhereException {
	List<IAreaType> results = new ArrayList<IAreaType>();
	for (GAreaType grpc : response.getAreaTypesList()) {
	    results.add(DeviceModelConverter.asApiAreaType(grpc));
	}
	return new SearchResults<IAreaType>(results, response.getCount());
    }

    /**
     * Convert area type from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AreaType asApiAreaType(GAreaType grpc) throws SiteWhereException {
	AreaType api = new AreaType();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setToken(grpc.getToken());
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setIcon(grpc.getIcon());
	api.setContainedAreaTypeIds(CommonModelConverter.asApiUuids(grpc.getContainedAreaTypeIdsList()));
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert area type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAreaType asGrpcAreaType(IAreaType api) throws SiteWhereException {
	GAreaType.Builder grpc = GAreaType.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setToken(api.getToken());
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setIcon(api.getIcon());
	if (api.getContainedAreaTypeIds() != null) {
	    grpc.addAllContainedAreaTypeIds(CommonModelConverter.asGrpcUuids(api.getContainedAreaTypeIds()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert area map data from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AreaMapData asApiAreaMapData(GAreaMapData grpc) throws SiteWhereException {
	AreaMapData api = new AreaMapData();
	api.setType(grpc.getType());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert area map data from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAreaMapData asGrpcAreaMapData(IAreaMapData api) throws SiteWhereException {
	GAreaMapData.Builder grpc = GAreaMapData.newBuilder();
	if (api.getType() != null) {
	    grpc.setType(api.getType());
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert area create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AreaCreateRequest asApiAreaCreateRequest(GAreaCreateRequest grpc) throws SiteWhereException {
	AreaCreateRequest api = new AreaCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setAreaTypeToken(grpc.getAreaTypeToken());
	api.setParentAreaToken(grpc.hasParentAreaToken() ? grpc.getParentAreaToken().getValue() : null);
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setImageUrl(grpc.getImageUrl());
	api.setMap(grpc.hasMapData() ? DeviceModelConverter.asApiAreaMapData(grpc.getMapData()) : null);
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert area create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAreaCreateRequest asGrpcAreaCreateRequest(IAreaCreateRequest api) throws SiteWhereException {
	GAreaCreateRequest.Builder grpc = GAreaCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setAreaTypeToken(api.getAreaTypeToken());
	if (api.getParentAreaToken() != null) {
	    grpc.setParentAreaToken(GOptionalString.newBuilder().setValue(api.getParentAreaToken()));
	}
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setImageUrl(api.getImageUrl());
	if (api.getMap() != null) {
	    grpc.setMapData(DeviceModelConverter.asGrpcAreaMapData(api.getMap()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert area search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AreaSearchCriteria asApiAreaSearchCriteria(GAreaSearchCriteria grpc) throws SiteWhereException {
	AreaSearchCriteria api = new AreaSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	api.setRootOnly(grpc.hasRootOnly() ? grpc.getRootOnly().getValue() : null);
	api.setParentAreaId(grpc.hasParentAreaId() ? CommonModelConverter.asApiUuid(grpc.getParentAreaId()) : null);
	api.setAreaTypeId(grpc.hasAreaTypeId() ? CommonModelConverter.asApiUuid(grpc.getAreaTypeId()) : null);
	return api;
    }

    /**
     * Convert area search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GAreaSearchCriteria asGrpcAreaSearchCriteria(IAreaSearchCriteria api) throws SiteWhereException {
	GAreaSearchCriteria.Builder grpc = GAreaSearchCriteria.newBuilder();
	if (api.getRootOnly() != null) {
	    grpc.setRootOnly(GOptionalBoolean.newBuilder().setValue(api.getRootOnly()));
	}
	if (api.getParentAreaId() != null) {
	    grpc.setParentAreaId(CommonModelConverter.asGrpcUuid(api.getParentAreaId()));
	}
	if (api.getAreaTypeId() != null) {
	    grpc.setAreaTypeId(CommonModelConverter.asGrpcUuid(api.getAreaTypeId()));
	}
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	return grpc.build();
    }

    /**
     * Convert area search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IArea> asApiAreaSearchResults(GAreaSearchResults response) throws SiteWhereException {
	List<IArea> results = new ArrayList<IArea>();
	for (GArea grpc : response.getAreasList()) {
	    results.add(DeviceModelConverter.asApiArea(grpc));
	}
	return new SearchResults<IArea>(results, response.getCount());
    }

    /**
     * Convert area from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static Area asApiArea(GArea grpc) throws SiteWhereException {
	Area api = new Area();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setToken(grpc.getToken());
	api.setAreaTypeId(CommonModelConverter.asApiUuid(grpc.getAreaTypeId()));
	api.setParentAreaId(grpc.hasParentAreaId() ? CommonModelConverter.asApiUuid(grpc.getParentAreaId()) : null);
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setImageUrl(grpc.getImageUrl());
	api.setMap(DeviceModelConverter.asApiAreaMapData(grpc.getMapData()));
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert list of areas from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<IArea> asApiAreas(Collection<GArea> grpcs) throws SiteWhereException {
	List<IArea> apis = new ArrayList<>();
	for (GArea grpc : grpcs) {
	    apis.add(DeviceModelConverter.asApiArea(grpc));
	}
	return apis;
    }

    /**
     * Convert area from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GArea asGrpcArea(IArea api) throws SiteWhereException {
	GArea.Builder grpc = GArea.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setToken(api.getToken());
	grpc.setAreaTypeId(CommonModelConverter.asGrpcUuid(api.getAreaTypeId()));
	if (api.getParentAreaId() != null) {
	    grpc.setParentAreaId(CommonModelConverter.asGrpcUuid(api.getParentAreaId()));
	}
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setImageUrl(api.getImageUrl());
	grpc.setMapData(DeviceModelConverter.asGrpcAreaMapData(api.getMap()));
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert list of areas from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GArea> asGrpcAreas(List<IArea> apis) throws SiteWhereException {
	List<GArea> grpcs = new ArrayList<>();
	for (IArea api : apis) {
	    grpcs.add(DeviceModelConverter.asGrpcArea(api));
	}
	return grpcs;
    }

    /**
     * Convert zone create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ZoneCreateRequest asApiZoneCreateRequest(GZoneCreateRequest grpc) throws SiteWhereException {
	ZoneCreateRequest api = new ZoneCreateRequest();
	api.setName(grpc.getName());
	api.setCoordinates(CommonModelConverter.asApiLocations(grpc.getCoordinatesList()));
	api.setFillColor(grpc.getFillColor());
	api.setBorderColor(grpc.getBorderColor());
	api.setOpacity(grpc.getOpacity());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert zone create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GZoneCreateRequest asGrpcZoneCreateRequest(IZoneCreateRequest api) throws SiteWhereException {
	GZoneCreateRequest.Builder grpc = GZoneCreateRequest.newBuilder();
	grpc.setName(api.getName());
	grpc.addAllCoordinates(CommonModelConverter.asGrpcLocations(api.getCoordinates()));
	grpc.setFillColor(api.getFillColor());
	grpc.setBorderColor(api.getBorderColor());
	grpc.setOpacity(api.getOpacity());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert zone search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GZoneSearchCriteria asApiZoneSearchCriteria(ISearchCriteria criteria) throws SiteWhereException {
	GZoneSearchCriteria.Builder gcriteria = GZoneSearchCriteria.newBuilder();
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(criteria));
	return gcriteria.build();
    }

    /**
     * Convert zone search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IZone> asApiZoneSearchResults(GZoneSearchResults response) throws SiteWhereException {
	List<IZone> results = new ArrayList<IZone>();
	for (GZone grpc : response.getZonesList()) {
	    results.add(DeviceModelConverter.asApiZone(grpc));
	}
	return new SearchResults<IZone>(results, response.getCount());
    }

    /**
     * Convert zone from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static Zone asApiZone(GZone grpc) throws SiteWhereException {
	Zone api = new Zone();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setToken(grpc.getToken());
	api.setName(grpc.getName());
	api.setCoordinates(CommonModelConverter.asApiLocations(grpc.getCoordinatesList()));
	api.setFillColor(grpc.getFillColor());
	api.setBorderColor(grpc.getBorderColor());
	api.setOpacity(grpc.getOpacity());
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert zone from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GZone asGrpcZone(IZone api) throws SiteWhereException {
	GZone.Builder grpc = GZone.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setToken(api.getToken());
	grpc.setName(api.getName());
	grpc.addAllCoordinates(CommonModelConverter.asGrpcLocations(api.getCoordinates()));
	grpc.setFillColor(api.getFillColor());
	grpc.setBorderColor(api.getBorderColor());
	grpc.setOpacity(api.getOpacity());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }
}