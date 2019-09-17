/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.model.CommonModel.GDeviceAlarmState;
import com.sitewhere.grpc.model.CommonModel.GDeviceContainerPolicy;
import com.sitewhere.grpc.model.CommonModel.GOptionalBoolean;
import com.sitewhere.grpc.model.CommonModel.GOptionalDouble;
import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.grpc.model.CommonModel.GParameterType;
import com.sitewhere.grpc.model.DeviceModel.GArea;
import com.sitewhere.grpc.model.DeviceModel.GAreaCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GAreaSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GAreaSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GAreaType;
import com.sitewhere.grpc.model.DeviceModel.GAreaTypeCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GAreaTypeSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GAreaTypeSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GCommandParameter;
import com.sitewhere.grpc.model.DeviceModel.GCustomer;
import com.sitewhere.grpc.model.DeviceModel.GCustomerCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GCustomerSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GCustomerSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GCustomerType;
import com.sitewhere.grpc.model.DeviceModel.GCustomerTypeCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GCustomerTypeSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GCustomerTypeSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDevice;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAlarm;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAlarmCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAlarmSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAlarmSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignment;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommand;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommandCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommandSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommandSearchResults;
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
import com.sitewhere.grpc.model.DeviceModel.GDeviceRegistationPayload;
import com.sitewhere.grpc.model.DeviceModel.GDeviceRegistrationRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSlot;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatus;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatusCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatusSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatusSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceType;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeReference;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceUnit;
import com.sitewhere.grpc.model.DeviceModel.GTreeNode;
import com.sitewhere.grpc.model.DeviceModel.GZone;
import com.sitewhere.grpc.model.DeviceModel.GZoneCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GZoneSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GZoneSearchResults;
import com.sitewhere.rest.model.area.Area;
import com.sitewhere.rest.model.area.AreaType;
import com.sitewhere.rest.model.area.Zone;
import com.sitewhere.rest.model.area.request.AreaCreateRequest;
import com.sitewhere.rest.model.area.request.AreaTypeCreateRequest;
import com.sitewhere.rest.model.area.request.ZoneCreateRequest;
import com.sitewhere.rest.model.customer.Customer;
import com.sitewhere.rest.model.customer.CustomerType;
import com.sitewhere.rest.model.customer.request.CustomerCreateRequest;
import com.sitewhere.rest.model.customer.request.CustomerTypeCreateRequest;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceAlarm;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.DeviceStatus;
import com.sitewhere.rest.model.device.DeviceType;
import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.rest.model.device.event.kafka.DeviceRegistrationPayload;
import com.sitewhere.rest.model.device.event.request.DeviceRegistrationRequest;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.request.DeviceAlarmCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceAssignmentCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStatusCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.TreeNode;
import com.sitewhere.rest.model.search.area.AreaSearchCriteria;
import com.sitewhere.rest.model.search.customer.CustomerSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceAlarmSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceAssignmentSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceCommandSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.rest.model.search.device.DeviceStatusSearchCriteria;
import com.sitewhere.rest.model.search.device.ZoneSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.area.request.IAreaCreateRequest;
import com.sitewhere.spi.area.request.IAreaTypeCreateRequest;
import com.sitewhere.spi.area.request.IZoneCreateRequest;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.customer.request.ICustomerCreateRequest;
import com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest;
import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAlarm;
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
import com.sitewhere.spi.device.event.kafka.IDeviceRegistrationPayload;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.ITreeNode;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;
import com.sitewhere.spi.search.customer.ICustomerSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceCommandSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceStatusSearchCriteria;
import com.sitewhere.spi.search.device.IZoneSearchCriteria;

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
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceTypeSearchCriteria asApiDeviceTypeSearchCriteria(ISearchCriteria criteria)
	    throws SiteWhereException {
	GDeviceTypeSearchCriteria.Builder gcriteria = GDeviceTypeSearchCriteria.newBuilder();
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
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setDescription(grpc.hasDescription() ? grpc.getDescription().getValue() : null);
	api.setContainerPolicy(DeviceModelConverter.asApiDeviceContainerPolicy(grpc.getContainerPolicy()));
	api.setDeviceElementSchema(DeviceModelConverter.asApiDeviceElementSchema(grpc.getDeviceElementSchema()));
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
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
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	if (api.getDescription() != null) {
	    grpc.setDescription(GOptionalString.newBuilder().setValue(api.getDescription()));
	}
	grpc.setContainerPolicy(DeviceModelConverter.asGrpcDeviceContainerPolicy(api.getContainerPolicy()));
	if (api.getDeviceElementSchema() != null) {
	    grpc.setDeviceElementSchema(DeviceModelConverter.asGrpcDeviceElementSchema(api.getDeviceElementSchema()));
	}
	grpc.putAllMetadata(api.getMetadata());
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
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
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setContainerPolicy(DeviceModelConverter.asApiDeviceContainerPolicy(grpc.getContainerPolicy()));
	api.setDeviceElementSchema(DeviceModelConverter.asApiDeviceElementSchema(grpc.getDeviceElementSchema()));
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
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
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setContainerPolicy(DeviceModelConverter.asGrpcDeviceContainerPolicy(api.getContainerPolicy()));
	if (api.getDeviceElementSchema() != null) {
	    grpc.setDeviceElementSchema(DeviceModelConverter.asGrpcDeviceElementSchema(api.getDeviceElementSchema()));
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
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
     * Convert device command search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandSearchCriteria asApiDeviceCommandSearchCriteria(GDeviceCommandSearchCriteria grpc)
	    throws SiteWhereException {
	DeviceCommandSearchCriteria api = new DeviceCommandSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	api.setDeviceTypeToken(grpc.hasDeviceTypeToken() ? grpc.getDeviceTypeToken().getValue() : null);
	return api;
    }

    /**
     * Convert device command search criteria from API to GRPC.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandSearchCriteria asGrpcDeviceCommandSearchCriteria(IDeviceCommandSearchCriteria api)
	    throws SiteWhereException {
	GDeviceCommandSearchCriteria.Builder gcriteria = GDeviceCommandSearchCriteria.newBuilder();
	if (api.getDeviceTypeToken() != null) {
	    gcriteria.setDeviceTypeToken(GOptionalString.newBuilder().setValue(api.getDeviceTypeToken()));
	}
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(api));
	return gcriteria.build();
    }

    /**
     * Convert device command search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceCommand> asApiDeviceCommandSearchResults(GDeviceCommandSearchResults response)
	    throws SiteWhereException {
	List<IDeviceCommand> results = new ArrayList<IDeviceCommand>();
	for (GDeviceCommand grpc : response.getDeviceCommandsList()) {
	    results.add(DeviceModelConverter.asApiDeviceCommand(grpc));
	}
	return new SearchResults<IDeviceCommand>(results, response.getCount());
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
	api.setDeviceTypeToken(grpc.hasDeviceTypeToken() ? grpc.getDeviceTypeToken().getValue() : null);
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setDescription(grpc.hasDescription() ? grpc.getDescription().getValue() : null);
	api.setNamespace(grpc.hasNamespace() ? grpc.getNamespace().getValue() : null);
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
	if (api.getDeviceTypeToken() != null) {
	    grpc.setDeviceTypeToken(GOptionalString.newBuilder().setValue(api.getDeviceTypeToken()));
	}
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	if (api.getDescription() != null) {
	    grpc.setDescription(GOptionalString.newBuilder().setValue(api.getDescription()));
	}
	if (api.getNamespace() != null) {
	    grpc.setNamespace(GOptionalString.newBuilder().setValue(api.getNamespace()));
	}
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
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setNamespace(grpc.getNamespace());
	api.setParameters(DeviceModelConverter.asApiCommandParameters(grpc.getParametersList()));
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
	grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setNamespace(api.getNamespace());
	grpc.addAllParameters(DeviceModelConverter.asGrpcCommandParameters(api.getParameters()));
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert device status search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStatusSearchCriteria asApiDeviceStatusSearchCriteria(GDeviceStatusSearchCriteria grpc)
	    throws SiteWhereException {
	DeviceStatusSearchCriteria api = new DeviceStatusSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	api.setDeviceTypeToken(grpc.hasDeviceTypeToken() ? grpc.getDeviceTypeToken().getValue() : null);
	api.setCode(grpc.hasCode() ? grpc.getCode().getValue() : null);
	return api;
    }

    /**
     * Convert device status search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStatusSearchCriteria asGrpcDeviceStatusSearchCriteria(IDeviceStatusSearchCriteria api)
	    throws SiteWhereException {
	GDeviceStatusSearchCriteria.Builder gcriteria = GDeviceStatusSearchCriteria.newBuilder();
	if (api.getDeviceTypeToken() != null) {
	    gcriteria.setDeviceTypeToken(GOptionalString.newBuilder().setValue(api.getDeviceTypeToken()));
	}
	if (api.getCode() != null) {
	    gcriteria.setCode(GOptionalString.newBuilder().setValue(api.getCode()).build());
	}
	gcriteria.setPaging(CommonModelConverter.asGrpcPaging(api));
	return gcriteria.build();
    }

    /**
     * Convert device status search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceStatus> asApiDeviceStatusSearchResults(GDeviceStatusSearchResults response)
	    throws SiteWhereException {
	List<IDeviceStatus> results = new ArrayList<IDeviceStatus>();
	for (GDeviceStatus grpc : response.getDeviceStatusesList()) {
	    results.add(DeviceModelConverter.asApiDeviceStatus(grpc));
	}
	return new SearchResults<IDeviceStatus>(results, response.getCount());
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
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setDeviceTypeToken(grpc.hasDeviceTypeToken() ? grpc.getDeviceTypeToken().getValue() : null);
	api.setCode(grpc.hasCode() ? grpc.getCode().getValue() : null);
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setBackgroundColor(grpc.hasBackgroundColor() ? grpc.getBackgroundColor().getValue() : null);
	api.setForegroundColor(grpc.hasForegroundColor() ? grpc.getForegroundColor().getValue() : null);
	api.setBorderColor(grpc.hasBorderColor() ? grpc.getBorderColor().getValue() : null);
	api.setIcon(grpc.hasIcon() ? grpc.getIcon().getValue() : null);
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
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	if (api.getDeviceTypeToken() != null) {
	    grpc.setDeviceTypeToken(GOptionalString.newBuilder().setValue(api.getDeviceTypeToken()));
	}
	if (api.getCode() != null) {
	    grpc.setCode(GOptionalString.newBuilder().setValue(api.getCode()));
	}
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	if (api.getBackgroundColor() != null) {
	    grpc.setBackgroundColor(GOptionalString.newBuilder().setValue(api.getBackgroundColor()));
	}
	if (api.getForegroundColor() != null) {
	    grpc.setForegroundColor(GOptionalString.newBuilder().setValue(api.getForegroundColor()));
	}
	if (api.getBorderColor() != null) {
	    grpc.setBorderColor(GOptionalString.newBuilder().setValue(api.getBorderColor()));
	}
	if (api.getIcon() != null) {
	    grpc.setIcon(GOptionalString.newBuilder().setValue(api.getIcon()));
	}
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
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setCode(grpc.getCode());
	api.setName(grpc.getName());
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setBackgroundColor(grpc.getBackgroundColor());
	api.setForegroundColor(grpc.getForegroundColor());
	api.setBorderColor(grpc.getBorderColor());
	api.setIcon(grpc.getIcon());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
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
	grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	grpc.setCode(api.getCode());
	grpc.setName(api.getName());
	grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	grpc.setBackgroundColor(api.getBackgroundColor());
	grpc.setForegroundColor(api.getForegroundColor());
	grpc.setBorderColor(api.getBorderColor());
	grpc.setIcon(api.getIcon());
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
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
    public static List<GDeviceElementMapping> asGrpcDeviceElementMappings(List<? extends IDeviceElementMapping> apis)
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
     * Convert device registration request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceRegistrationRequest asApiDeviceRegistrationRequest(GDeviceRegistrationRequest grpc)
	    throws SiteWhereException {
	DeviceRegistrationRequest api = new DeviceRegistrationRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setParentDeviceToken(grpc.hasParentDeviceToken() ? grpc.getParentDeviceToken().getValue() : null);
	api.setDeviceTypeToken(grpc.hasDeviceTypeToken() ? grpc.getDeviceTypeToken().getValue() : null);
	api.setStatus(grpc.hasStatus() ? grpc.getStatus().getValue() : null);
	api.setComments(grpc.hasComments() ? grpc.getComments().getValue() : null);
	api.setDeviceElementMappings(
		DeviceModelConverter.asApiDeviceElementMappings(grpc.getDeviceElementMappingsList()));
	api.setMetadata(grpc.getMetadataMap());
	api.setCustomerToken(grpc.hasCustomerToken() ? grpc.getCustomerToken().getValue() : null);
	api.setAreaToken(grpc.hasAreaToken() ? grpc.getAreaToken().getValue() : null);
	return api;
    }

    /**
     * Convert device registration request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceRegistrationRequest asGrpcDeviceRegistrationRequest(IDeviceRegistrationRequest api)
	    throws SiteWhereException {
	GDeviceRegistrationRequest.Builder grpc = GDeviceRegistrationRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	if (api.getParentDeviceToken() != null) {
	    grpc.setParentDeviceToken(GOptionalString.newBuilder().setValue(api.getParentDeviceToken()));
	}
	if (api.getDeviceTypeToken() != null) {
	    grpc.setDeviceTypeToken(GOptionalString.newBuilder().setValue(api.getDeviceTypeToken()));
	}
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
	if (api.getCustomerToken() != null) {
	    grpc.setCustomerToken(GOptionalString.newBuilder().setValue(api.getCustomerToken()));
	}
	if (api.getAreaToken() != null) {
	    grpc.setAreaToken(GOptionalString.newBuilder().setValue(api.getAreaToken()));
	}
	return grpc.build();
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
	api.setDeviceTypeToken(grpc.hasDeviceTypeToken() ? grpc.getDeviceTypeToken().getValue() : null);
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
	if (api.getDeviceTypeToken() != null) {
	    grpc.setDeviceTypeToken(GOptionalString.newBuilder().setValue(api.getDeviceTypeToken()));
	}
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
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setStatus(grpc.hasStatus() ? grpc.getStatus().getValue() : null);
	api.setActiveDeviceAssignmentIds(CommonModelConverter.asApiUuids(grpc.getActiveAssignmentIdList()));
	api.setParentDeviceId(
		grpc.hasParentDeviceId() ? CommonModelConverter.asApiUuid(grpc.getParentDeviceId()) : null);
	api.setComments(grpc.hasComments() ? grpc.getComments().getValue() : null);
	api.setDeviceElementMappings(
		DeviceModelConverter.asApiDeviceElementMappings(grpc.getDeviceElementMappingsList()));
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
	if (api.getParentDeviceId() != null) {
	    grpc.setParentDeviceId(CommonModelConverter.asGrpcUuid(api.getParentDeviceId()));
	}
	grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	if (api.getStatus() != null) {
	    grpc.setStatus(GOptionalString.newBuilder().setValue(api.getStatus()).build());
	}
	if (api.getActiveDeviceAssignmentIds() != null) {
	    grpc.addAllActiveAssignmentId(CommonModelConverter.asGrpcUuids(api.getActiveDeviceAssignmentIds()));
	}
	if (api.getComments() != null) {
	    grpc.setComments(GOptionalString.newBuilder().setValue(api.getComments()).build());
	}
	grpc.addAllDeviceElementMappings(
		DeviceModelConverter.asGrpcDeviceElementMappings(api.getDeviceElementMappings()));
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert devices from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDevice> asGrpcDevices(List<IDevice> apis) throws SiteWhereException {
	List<GDevice> grpcs = new ArrayList<GDevice>();
	for (IDevice api : apis) {
	    grpcs.add(asGrpcDevice(api));
	}
	return grpcs;
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
	Date createdAfter = CommonModelConverter.asApiDate(grpc.getCreatedAfter());
	Date createdBefore = CommonModelConverter.asApiDate(grpc.getCreatedBefore());
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
	grpc.setCreatedAfter(CommonModelConverter.asGrpcDate(api.getStartDate()));
	grpc.setCreatedBefore(CommonModelConverter.asGrpcDate(api.getEndDate()));
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
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setDescription(grpc.hasDescription() ? grpc.getDescription().getValue() : null);
	api.setRoles(grpc.getRolesList());
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
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
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	if (api.getDescription() != null) {
	    grpc.setDescription(GOptionalString.newBuilder().setValue(api.getDescription()));
	}
	grpc.addAllRoles(api.getRoles());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
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
     * @param role
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceGroupsWithRoleSearchCriteria asApiDeviceGroupsWithRoleSearchCriteria(String role,
	    ISearchCriteria criteria) throws SiteWhereException {
	GDeviceGroupsWithRoleSearchCriteria.Builder gcriteria = GDeviceGroupsWithRoleSearchCriteria.newBuilder();
	gcriteria.setRole(role);
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
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setRoles(grpc.getRolesList());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
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
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.addAllRoles(api.getRoles());
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
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
	api.setDeviceToken(grpc.hasDeviceToken() ? grpc.getDeviceToken().getValue() : null);
	api.setNestedGroupToken(grpc.hasNestedGroupToken() ? grpc.getNestedGroupToken().getValue() : null);
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
	if (api.getDeviceToken() != null) {
	    grpc.setDeviceToken(GOptionalString.newBuilder().setValue(api.getDeviceToken()));
	}
	if (api.getNestedGroupToken() != null) {
	    grpc.setNestedGroupToken(GOptionalString.newBuilder().setValue(api.getNestedGroupToken()));
	}
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
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setGroupId(CommonModelConverter.asApiUuid(grpc.getGroupId()));
	api.setDeviceId(grpc.hasDeviceId() ? CommonModelConverter.asApiUuid(grpc.getDeviceId()) : null);
	api.setNestedGroupId(grpc.hasNestedGroupId() ? CommonModelConverter.asApiUuid(grpc.getNestedGroupId()) : null);
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
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	grpc.setGroupId(CommonModelConverter.asGrpcUuid(api.getGroupId()));
	if (api.getDeviceId() != null) {
	    grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	}
	if (api.getNestedGroupId() != null) {
	    grpc.setNestedGroupId(CommonModelConverter.asGrpcUuid(api.getNestedGroupId()));
	}
	grpc.addAllRoles(api.getRoles());
	return grpc.build();
    }

    /**
     * Convert device assignment search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignmentSearchCriteria asApiDeviceAssignmentSearchCriteria(
	    GDeviceAssignmentSearchCriteria grpc) throws SiteWhereException {
	DeviceAssignmentSearchCriteria api = new DeviceAssignmentSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	api.setAssignmentStatuses(CommonModelConverter.asApiDeviceAssignmentStatuses(grpc.getStatusList()));
	api.setDeviceTokens(grpc.getDeviceTokensList());
	api.setDeviceTypeTokens(grpc.getDeviceTypeTokensList());
	api.setCustomerTokens(grpc.getCustomerTokensList());
	api.setAreaTokens(grpc.getAreaTokensList());
	api.setAssetTokens(grpc.getAssetTokensList());
	return api;
    }

    /**
     * Convert device assignment search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAssignmentSearchCriteria asGrpcDeviceAssignmentSearchCriteria(
	    IDeviceAssignmentSearchCriteria api) throws SiteWhereException {
	GDeviceAssignmentSearchCriteria.Builder grpc = GDeviceAssignmentSearchCriteria.newBuilder();
	if (api.getAssignmentStatuses() != null) {
	    grpc.addAllStatus(CommonModelConverter.asGrpcDeviceAssignmentStatuses(api.getAssignmentStatuses()));
	}
	if (api.getDeviceTokens() != null) {
	    grpc.addAllDeviceTokens(api.getDeviceTokens());
	}
	if (api.getDeviceTypeTokens() != null) {
	    grpc.addAllDeviceTypeTokens(api.getDeviceTypeTokens());
	}
	if (api.getCustomerTokens() != null) {
	    grpc.addAllCustomerTokens(api.getCustomerTokens());
	}
	if (api.getAreaTokens() != null) {
	    grpc.addAllAreaTokens(api.getAreaTokens());
	}
	if (api.getAssetTokens() != null) {
	    grpc.addAllAssetTokens(api.getAssetTokens());
	}
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	return grpc.build();
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
	api.setCustomerToken(grpc.hasCustomerToken() ? grpc.getCustomerToken().getValue() : null);
	api.setAreaToken(grpc.hasAreaToken() ? grpc.getAreaToken().getValue() : null);
	api.setAssetToken(grpc.hasAssetToken() ? grpc.getAssetToken().getValue() : null);
	api.setStatus(CommonModelConverter.asApiDeviceAssignmentStatus(grpc.getStatus()));
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
	if (api.getCustomerToken() != null) {
	    grpc.setCustomerToken(GOptionalString.newBuilder().setValue(api.getCustomerToken()));
	}
	if (api.getAreaToken() != null) {
	    grpc.setAreaToken(GOptionalString.newBuilder().setValue(api.getAreaToken()));
	}
	if (api.getAssetToken() != null) {
	    grpc.setAssetToken(GOptionalString.newBuilder().setValue(api.getAssetToken()));
	}
	if (api.getStatus() != null) {
	    grpc.setStatus(CommonModelConverter.asGrpcDeviceAssignmentStatus(api.getStatus()));
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
	api.setStatus(CommonModelConverter.asApiDeviceAssignmentStatus(grpc.getStatus()));
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setCustomerId(grpc.hasCustomerId() ? CommonModelConverter.asApiUuid(grpc.getCustomerId()) : null);
	api.setAreaId(grpc.hasAreaId() ? CommonModelConverter.asApiUuid(grpc.getAreaId()) : null);
	api.setAssetId(grpc.hasAssetId() ? CommonModelConverter.asApiUuid(grpc.getAssetId()) : null);
	api.setActiveDate(CommonModelConverter.asApiDate(grpc.getActiveDate()));
	api.setReleasedDate(CommonModelConverter.asApiDate(grpc.getReleasedDate()));
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert list of device assignments from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<IDeviceAssignment> asApiDeviceAssignments(Collection<GDeviceAssignment> grpcs)
	    throws SiteWhereException {
	List<IDeviceAssignment> apis = new ArrayList<>();
	for (GDeviceAssignment grpc : grpcs) {
	    apis.add(DeviceModelConverter.asApiDeviceAssignment(grpc));
	}
	return apis;
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
	grpc.setStatus(CommonModelConverter.asGrpcDeviceAssignmentStatus(api.getStatus()));
	grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	if (api.getAreaId() != null) {
	    grpc.setAreaId(CommonModelConverter.asGrpcUuid(api.getAreaId()));
	}
	if (api.getAssetId() != null) {
	    grpc.setAssetId(CommonModelConverter.asGrpcUuid(api.getAssetId()));
	}
	grpc.setActiveDate(CommonModelConverter.asGrpcDate(api.getActiveDate()));
	grpc.setReleasedDate(CommonModelConverter.asGrpcDate(api.getReleasedDate()));
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert list of device assignments from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceAssignment> asGrpcDeviceAssignments(List<IDeviceAssignment> apis)
	    throws SiteWhereException {
	List<GDeviceAssignment> grpcs = new ArrayList<>();
	for (IDeviceAssignment api : apis) {
	    grpcs.add(DeviceModelConverter.asGrpcDeviceAssignment(api));
	}
	return grpcs;
    }

    /**
     * Convert device alarm state from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlarmState asApiDeviceAlarmState(GDeviceAlarmState grpc) throws SiteWhereException {
	if (grpc == null) {
	    return null;
	}
	switch (grpc) {
	case ALARM_STATE_TRIGGERED:
	    return DeviceAlarmState.Triggered;
	case ALARM_STATE_ACKNOWLEDGED:
	    return DeviceAlarmState.Acknowledged;
	case ALARM_STATE_RESOLVED:
	    return DeviceAlarmState.Resolved;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown device alarm state: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert device alarm state from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAlarmState asGrpcDeviceAlarmState(DeviceAlarmState api) throws SiteWhereException {
	if (api == null) {
	    return null;
	}
	switch (api) {
	case Triggered:
	    return GDeviceAlarmState.ALARM_STATE_TRIGGERED;
	case Acknowledged:
	    return GDeviceAlarmState.ALARM_STATE_ACKNOWLEDGED;
	case Resolved:
	    return GDeviceAlarmState.ALARM_STATE_RESOLVED;
	}
	throw new SiteWhereException("Unknown device alarm state: " + api.name());
    }

    /**
     * Convert device alarm create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlarmCreateRequest asApiDeviceAlarmCreateRequest(GDeviceAlarmCreateRequest grpc)
	    throws SiteWhereException {
	DeviceAlarmCreateRequest api = new DeviceAlarmCreateRequest();
	api.setDeviceAssignmentToken(
		grpc.hasDeviceAssignmentToken() ? grpc.getDeviceAssignmentToken().getValue() : null);
	api.setAlarmMessage(grpc.hasAlarmMessage() ? grpc.getAlarmMessage().getValue() : null);
	api.setTriggeringEventId(CommonModelConverter.asApiUuid(grpc.getTriggeringEventId()));
	api.setState(DeviceModelConverter.asApiDeviceAlarmState(grpc.getState()));
	api.setTriggeredDate(CommonModelConverter.asApiDate(grpc.getTriggeredDate()));
	api.setAcknowledgedDate(CommonModelConverter.asApiDate(grpc.getAcknowledgedDate()));
	api.setResolvedDate(CommonModelConverter.asApiDate(grpc.getResolvedDate()));
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert device alarm create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAlarmCreateRequest asGrpcDeviceAlarmCreateRequest(IDeviceAlarmCreateRequest api)
	    throws SiteWhereException {
	GDeviceAlarmCreateRequest.Builder grpc = GDeviceAlarmCreateRequest.newBuilder();
	if (api.getDeviceAssignmentToken() != null) {
	    grpc.setDeviceAssignmentToken(GOptionalString.newBuilder().setValue(api.getDeviceAssignmentToken()));
	}
	if (api.getAlarmMessage() != null) {
	    grpc.setAlarmMessage(GOptionalString.newBuilder().setValue(api.getAlarmMessage()));
	}
	if (api.getTriggeringEventId() != null) {
	    grpc.setTriggeringEventId(CommonModelConverter.asGrpcUuid(api.getTriggeringEventId()));
	}
	if (api.getState() != null) {
	    grpc.setState(DeviceModelConverter.asGrpcDeviceAlarmState(api.getState()));
	}
	if (api.getTriggeredDate() != null) {
	    grpc.setTriggeredDate(CommonModelConverter.asGrpcDate(api.getTriggeredDate()));
	}
	if (api.getAcknowledgedDate() != null) {
	    grpc.setAcknowledgedDate(CommonModelConverter.asGrpcDate(api.getAcknowledgedDate()));
	}
	if (api.getResolvedDate() != null) {
	    grpc.setResolvedDate(CommonModelConverter.asGrpcDate(api.getResolvedDate()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device alarm search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlarmSearchCriteria asApiDeviceAlarmSearchCriteria(GDeviceAlarmSearchCriteria grpc)
	    throws SiteWhereException {
	DeviceAlarmSearchCriteria api = new DeviceAlarmSearchCriteria();
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setDeviceAssignmentId(CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()));
	api.setCustomerId(CommonModelConverter.asApiUuid(grpc.getCustomerId()));
	api.setAreaId(CommonModelConverter.asApiUuid(grpc.getAreaId()));
	api.setAssetId(CommonModelConverter.asApiUuid(grpc.getAssetId()));
	api.setTriggeringEventId(CommonModelConverter.asApiUuid(grpc.getTriggeringEventId()));
	api.setState(DeviceModelConverter.asApiDeviceAlarmState(grpc.getState()));
	api.setPageNumber(grpc.getPageNumber());
	api.setPageSize(grpc.getPageSize());
	return api;
    }

    /**
     * Convert device alarm search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAlarmSearchCriteria asGrpcDeviceAlarmSearchCriteria(IDeviceAlarmSearchCriteria api)
	    throws SiteWhereException {
	GDeviceAlarmSearchCriteria.Builder grpc = GDeviceAlarmSearchCriteria.newBuilder();
	if (api.getDeviceId() != null) {
	    grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	}
	if (api.getDeviceAssignmentId() != null) {
	    grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	}
	if (api.getCustomerId() != null) {
	    grpc.setCustomerId(CommonModelConverter.asGrpcUuid(api.getCustomerId()));
	}
	if (api.getAreaId() != null) {
	    grpc.setAreaId(CommonModelConverter.asGrpcUuid(api.getAreaId()));
	}
	if (api.getAssetId() != null) {
	    grpc.setAssetId(CommonModelConverter.asGrpcUuid(api.getAssetId()));
	}
	if (api.getTriggeringEventId() != null) {
	    grpc.setTriggeringEventId(CommonModelConverter.asGrpcUuid(api.getTriggeringEventId()));
	}
	if (api.getState() != null) {
	    grpc.setState(DeviceModelConverter.asGrpcDeviceAlarmState(api.getState()));
	}
	grpc.setPageNumber(api.getPageNumber());
	grpc.setPageSize(api.getPageSize());
	return grpc.build();
    }

    /**
     * Convert device alarm search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceAlarm> asApiDeviceAlarmSearchResults(GDeviceAlarmSearchResults response)
	    throws SiteWhereException {
	List<IDeviceAlarm> results = new ArrayList<IDeviceAlarm>();
	for (GDeviceAlarm grpc : response.getAlarmsList()) {
	    results.add(DeviceModelConverter.asApiDeviceAlarm(grpc));
	}
	return new SearchResults<IDeviceAlarm>(results, response.getCount());
    }

    /**
     * Convert device alarm from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlarm asApiDeviceAlarm(GDeviceAlarm grpc) throws SiteWhereException {
	DeviceAlarm api = new DeviceAlarm();
	api.setDeviceId(grpc.hasDeviceId() ? CommonModelConverter.asApiUuid(grpc.getDeviceId()) : null);
	api.setDeviceAssignmentId(
		grpc.hasDeviceAssignmentId() ? CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()) : null);
	api.setCustomerId(grpc.hasCustomerId() ? CommonModelConverter.asApiUuid(grpc.getCustomerId()) : null);
	api.setAreaId(grpc.hasAreaId() ? CommonModelConverter.asApiUuid(grpc.getAreaId()) : null);
	api.setAssetId(grpc.hasAssetId() ? CommonModelConverter.asApiUuid(grpc.getAssetId()) : null);
	api.setAlarmMessage(grpc.hasAlarmMessage() ? grpc.getAlarmMessage().getValue() : null);
	api.setTriggeringEventId(CommonModelConverter.asApiUuid(grpc.getTriggeringEventId()));
	api.setState(DeviceModelConverter.asApiDeviceAlarmState(grpc.getState()));
	api.setTriggeredDate(CommonModelConverter.asApiDate(grpc.getTriggeredDate()));
	api.setAcknowledgedDate(CommonModelConverter.asApiDate(grpc.getAcknowledgedDate()));
	api.setResolvedDate(CommonModelConverter.asApiDate(grpc.getResolvedDate()));
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert device alarm from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAlarm asGrpcDeviceAlarm(IDeviceAlarm api) throws SiteWhereException {
	GDeviceAlarm.Builder grpc = GDeviceAlarm.newBuilder();
	if (api.getDeviceId() != null) {
	    grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	}
	if (api.getDeviceAssignmentId() != null) {
	    grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	}
	if (api.getCustomerId() != null) {
	    grpc.setCustomerId(CommonModelConverter.asGrpcUuid(api.getCustomerId()));
	}
	if (api.getAreaId() != null) {
	    grpc.setAreaId(CommonModelConverter.asGrpcUuid(api.getAreaId()));
	}
	if (api.getAssetId() != null) {
	    grpc.setAssetId(CommonModelConverter.asGrpcUuid(api.getAssetId()));
	}
	if (api.getAlarmMessage() != null) {
	    grpc.setAlarmMessage(GOptionalString.newBuilder().setValue(api.getAlarmMessage()));
	}
	if (api.getTriggeringEventId() != null) {
	    grpc.setTriggeringEventId(CommonModelConverter.asGrpcUuid(api.getTriggeringEventId()));
	}
	if (api.getState() != null) {
	    grpc.setState(DeviceModelConverter.asGrpcDeviceAlarmState(api.getState()));
	}
	if (api.getTriggeredDate() != null) {
	    grpc.setTriggeredDate(CommonModelConverter.asGrpcDate(api.getTriggeredDate()));
	}
	if (api.getAcknowledgedDate() != null) {
	    grpc.setAcknowledgedDate(CommonModelConverter.asGrpcDate(api.getAcknowledgedDate()));
	}
	if (api.getResolvedDate() != null) {
	    grpc.setResolvedDate(CommonModelConverter.asGrpcDate(api.getResolvedDate()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert customer type create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static CustomerTypeCreateRequest asApiCustomerTypeCreateRequest(GCustomerTypeCreateRequest grpc)
	    throws SiteWhereException {
	CustomerTypeCreateRequest api = new CustomerTypeCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setDescription(grpc.hasDescription() ? grpc.getDescription().getValue() : null);
	api.setContainedCustomerTypeTokens(grpc.getContainedCustomerTypeTokensList());
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert customer type create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GCustomerTypeCreateRequest asGrpcCustomerTypeCreateRequest(ICustomerTypeCreateRequest api)
	    throws SiteWhereException {
	GCustomerTypeCreateRequest.Builder grpc = GCustomerTypeCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	if (api.getDescription() != null) {
	    grpc.setDescription(GOptionalString.newBuilder().setValue(api.getDescription()));
	}
	if (api.getContainedCustomerTypeTokens() != null) {
	    grpc.addAllContainedCustomerTypeTokens(api.getContainedCustomerTypeTokens());
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
    }

    /**
     * Convert customer type search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GCustomerTypeSearchCriteria asApiCustomerTypeSearchCriteria(ISearchCriteria criteria)
	    throws SiteWhereException {
	GCustomerTypeSearchCriteria.Builder gcriteria = GCustomerTypeSearchCriteria.newBuilder();
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
    public static ISearchResults<ICustomerType> asApiCustomerTypeSearchResults(GCustomerTypeSearchResults response)
	    throws SiteWhereException {
	List<ICustomerType> results = new ArrayList<ICustomerType>();
	for (GCustomerType grpc : response.getCustomerTypesList()) {
	    results.add(DeviceModelConverter.asApiCustomerType(grpc));
	}
	return new SearchResults<ICustomerType>(results, response.getCount());
    }

    /**
     * Convert customer type from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static CustomerType asApiCustomerType(GCustomerType grpc) throws SiteWhereException {
	CustomerType api = new CustomerType();
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setContainedCustomerTypeIds(CommonModelConverter.asApiUuids(grpc.getContainedCustomerTypeIdsList()));
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert customer type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GCustomerType asGrpcCustomerType(ICustomerType api) throws SiteWhereException {
	GCustomerType.Builder grpc = GCustomerType.newBuilder();
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	if (api.getContainedCustomerTypeIds() != null) {
	    grpc.addAllContainedCustomerTypeIds(CommonModelConverter.asGrpcUuids(api.getContainedCustomerTypeIds()));
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
    }

    /**
     * Convert customer create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static CustomerCreateRequest asApiCustomerCreateRequest(GCustomerCreateRequest grpc)
	    throws SiteWhereException {
	CustomerCreateRequest api = new CustomerCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setCustomerTypeToken(grpc.hasCustomerTypeToken() ? grpc.getCustomerTypeToken().getValue() : null);
	api.setParentToken(grpc.hasParentCustomerToken() ? grpc.getParentCustomerToken().getValue() : null);
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setDescription(grpc.hasDescription() ? grpc.getDescription().getValue() : null);
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert customer create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GCustomerCreateRequest asGrpcCustomerCreateRequest(ICustomerCreateRequest api)
	    throws SiteWhereException {
	GCustomerCreateRequest.Builder grpc = GCustomerCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	if (api.getCustomerTypeToken() != null) {
	    grpc.setCustomerTypeToken(GOptionalString.newBuilder().setValue(api.getCustomerTypeToken()));
	}
	if (api.getParentToken() != null) {
	    grpc.setParentCustomerToken(GOptionalString.newBuilder().setValue(api.getParentToken()));
	}
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	if (api.getDescription() != null) {
	    grpc.setDescription(GOptionalString.newBuilder().setValue(api.getDescription()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
    }

    /**
     * Convert customer search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static CustomerSearchCriteria asApiCustomerSearchCriteria(GCustomerSearchCriteria grpc)
	    throws SiteWhereException {
	CustomerSearchCriteria api = new CustomerSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	api.setRootOnly(grpc.hasRootOnly() ? grpc.getRootOnly().getValue() : null);
	api.setParentCustomerToken(grpc.hasParentCustomerToken() ? grpc.getParentCustomerToken().getValue() : null);
	api.setCustomerTypeToken(grpc.hasCustomerTypeToken() ? grpc.getCustomerTypeToken().getValue() : null);
	return api;
    }

    /**
     * Convert customer search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GCustomerSearchCriteria asGrpcCustomerSearchCriteria(ICustomerSearchCriteria api)
	    throws SiteWhereException {
	GCustomerSearchCriteria.Builder grpc = GCustomerSearchCriteria.newBuilder();
	if (api.getRootOnly() != null) {
	    grpc.setRootOnly(GOptionalBoolean.newBuilder().setValue(api.getRootOnly()));
	}
	if (api.getParentCustomerToken() != null) {
	    grpc.setParentCustomerToken(GOptionalString.newBuilder().setValue(api.getParentCustomerToken()));
	}
	if (api.getCustomerTypeToken() != null) {
	    grpc.setCustomerTypeToken(GOptionalString.newBuilder().setValue(api.getCustomerTypeToken()));
	}
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	return grpc.build();
    }

    /**
     * Convert customer search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<ICustomer> asApiCustomerSearchResults(GCustomerSearchResults response)
	    throws SiteWhereException {
	List<ICustomer> results = new ArrayList<ICustomer>();
	for (GCustomer grpc : response.getCustomersList()) {
	    results.add(DeviceModelConverter.asApiCustomer(grpc));
	}
	return new SearchResults<ICustomer>(results, response.getCount());
    }

    /**
     * Convert tree node from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GTreeNode asGrpcTreeNode(ITreeNode api) throws SiteWhereException {
	GTreeNode.Builder grpc = GTreeNode.newBuilder();
	grpc.setToken(api.getToken());
	grpc.setName(api.getName());
	if (api.getIcon() != null) {
	    grpc.setIcon(GOptionalString.newBuilder().setValue(api.getIcon()));
	}
	if (api.getChildren() != null) {
	    for (ITreeNode child : api.getChildren()) {
		grpc.addChildren(DeviceModelConverter.asGrpcTreeNode(child));
	    }
	}
	return grpc.build();
    }

    /**
     * Convert tree node from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static TreeNode asApiTreeNode(GTreeNode grpc) throws SiteWhereException {
	TreeNode api = new TreeNode();
	api.setToken(grpc.getToken());
	api.setName(grpc.getName());
	api.setIcon(grpc.hasIcon() ? grpc.getIcon().getValue() : null);
	if (grpc.getChildrenCount() > 0) {
	    api.setChildren(new ArrayList<TreeNode>());
	    for (GTreeNode node : grpc.getChildrenList()) {
		api.getChildren().add(DeviceModelConverter.asApiTreeNode(node));
	    }
	}
	return api;
    }

    /**
     * Convert list of tree nodes from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<ITreeNode> asApiTreeNodes(Collection<GTreeNode> grpcs) throws SiteWhereException {
	List<ITreeNode> apis = new ArrayList<>();
	for (GTreeNode grpc : grpcs) {
	    apis.add(DeviceModelConverter.asApiTreeNode(grpc));
	}
	return apis;
    }

    /**
     * Convert customer from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static Customer asApiCustomer(GCustomer grpc) throws SiteWhereException {
	Customer api = new Customer();
	api.setCustomerTypeId(CommonModelConverter.asApiUuid(grpc.getCustomerTypeId()));
	api.setParentId(grpc.hasParentCustomerId() ? CommonModelConverter.asApiUuid(grpc.getParentCustomerId()) : null);
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
	return api;
    }

    /**
     * Convert list of customers from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<ICustomer> asApiCustomers(Collection<GCustomer> grpcs) throws SiteWhereException {
	List<ICustomer> apis = new ArrayList<>();
	for (GCustomer grpc : grpcs) {
	    apis.add(DeviceModelConverter.asApiCustomer(grpc));
	}
	return apis;
    }

    /**
     * Convert customer from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GCustomer asGrpcCustomer(ICustomer api) throws SiteWhereException {
	GCustomer.Builder grpc = GCustomer.newBuilder();
	grpc.setCustomerTypeId(CommonModelConverter.asGrpcUuid(api.getCustomerTypeId()));
	if (api.getParentId() != null) {
	    grpc.setParentCustomerId(CommonModelConverter.asGrpcUuid(api.getParentId()));
	}
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
	return grpc.build();
    }

    /**
     * Convert list of customers from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GCustomer> asGrpcCustomers(List<ICustomer> apis) throws SiteWhereException {
	List<GCustomer> grpcs = new ArrayList<>();
	for (ICustomer api : apis) {
	    grpcs.add(DeviceModelConverter.asGrpcCustomer(api));
	}
	return grpcs;
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
	api.setContainedAreaTypeTokens(grpc.getContainedAreaTypeTokensList());
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
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
	if (api.getContainedAreaTypeTokens() != null) {
	    grpc.addAllContainedAreaTypeTokens(api.getContainedAreaTypeTokens());
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
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
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setContainedAreaTypeIds(CommonModelConverter.asApiUuids(grpc.getContainedAreaTypeIdsList()));
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
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
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	if (api.getContainedAreaTypeIds() != null) {
	    grpc.addAllContainedAreaTypeIds(CommonModelConverter.asGrpcUuids(api.getContainedAreaTypeIds()));
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
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
	api.setAreaTypeToken(grpc.hasAreaTypeToken() ? grpc.getAreaTypeToken().getValue() : null);
	api.setParentToken(grpc.hasParentAreaToken() ? grpc.getParentAreaToken().getValue() : null);
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setDescription(grpc.hasDescription() ? grpc.getDescription().getValue() : null);
	api.setBounds(CommonModelConverter.asApiLocations(grpc.getBoundsList()));
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
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
	if (api.getAreaTypeToken() != null) {
	    grpc.setAreaTypeToken(GOptionalString.newBuilder().setValue(api.getAreaTypeToken()));
	}
	if (api.getParentToken() != null) {
	    grpc.setParentAreaToken(GOptionalString.newBuilder().setValue(api.getParentToken()));
	}
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	if (api.getDescription() != null) {
	    grpc.setDescription(GOptionalString.newBuilder().setValue(api.getDescription()));
	}
	if (api.getBounds() != null) {
	    grpc.addAllBounds(CommonModelConverter.asGrpcLocations(api.getBounds()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
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
	api.setParentAreaToken(grpc.hasParentAreaToken() ? grpc.getParentAreaToken().getValue() : null);
	api.setAreaTypeToken(grpc.hasAreaTypeToken() ? grpc.getAreaTypeToken().getValue() : null);
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
	if (api.getParentAreaToken() != null) {
	    grpc.setParentAreaToken(GOptionalString.newBuilder().setValue(api.getParentAreaToken()));
	}
	if (api.getAreaTypeToken() != null) {
	    grpc.setAreaTypeToken(GOptionalString.newBuilder().setValue(api.getAreaTypeToken()));
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
	api.setAreaTypeId(CommonModelConverter.asApiUuid(grpc.getAreaTypeId()));
	api.setParentId(grpc.hasParentAreaId() ? CommonModelConverter.asApiUuid(grpc.getParentAreaId()) : null);
	api.setName(grpc.getName());
	api.setDescription(grpc.getDescription());
	api.setBounds(CommonModelConverter.asApiLocations(grpc.getBoundsList()));
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	CommonModelConverter.setBrandingInformation(api, grpc.getBranding());
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
	grpc.setAreaTypeId(CommonModelConverter.asGrpcUuid(api.getAreaTypeId()));
	if (api.getParentId() != null) {
	    grpc.setParentAreaId(CommonModelConverter.asGrpcUuid(api.getParentId()));
	}
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.addAllBounds(CommonModelConverter.asGrpcLocations(api.getBounds()));
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	grpc.setBranding(CommonModelConverter.asGrpcBrandingInformation(api));
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
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setAreaToken(grpc.hasAreaToken() ? grpc.getAreaToken().getValue() : null);
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setBounds(CommonModelConverter.asApiLocations(grpc.getBoundsList()));
	api.setBorderColor(grpc.hasBorderColor() ? grpc.getBorderColor().getValue() : null);
	api.setBorderOpacity(grpc.hasBorderOpacity() ? grpc.getBorderOpacity().getValue() : null);
	api.setFillColor(grpc.hasFillColor() ? grpc.getFillColor().getValue() : null);
	api.setFillOpacity(grpc.hasFillOpacity() ? grpc.getFillOpacity().getValue() : null);
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
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	if (api.getAreaToken() != null) {
	    grpc.setAreaToken(GOptionalString.newBuilder().setValue(api.getAreaToken()));
	}
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	grpc.addAllBounds(CommonModelConverter.asGrpcLocations(api.getBounds()));
	if (api.getBorderColor() != null) {
	    grpc.setBorderColor(GOptionalString.newBuilder().setValue(api.getBorderColor()));
	}
	if (api.getBorderOpacity() != null) {
	    grpc.setBorderOpacity(GOptionalDouble.newBuilder().setValue(api.getBorderOpacity()));
	}
	if (api.getFillColor() != null) {
	    grpc.setFillColor(GOptionalString.newBuilder().setValue(api.getFillColor()));
	}
	if (api.getFillOpacity() != null) {
	    grpc.setFillOpacity(GOptionalDouble.newBuilder().setValue(api.getFillOpacity()));
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert zone search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ZoneSearchCriteria asApiZoneSearchCriteria(GZoneSearchCriteria grpc) throws SiteWhereException {
	ZoneSearchCriteria api = new ZoneSearchCriteria(grpc.getPaging().getPageNumber(),
		grpc.getPaging().getPageSize());
	api.setAreaToken(grpc.hasAreaToken() ? grpc.getAreaToken().getValue() : null);
	return api;
    }

    /**
     * Convert zone search criteria from API to GRPC.
     * 
     * @param code
     * @return
     * @throws SiteWhereException
     */
    public static GZoneSearchCriteria asGrpcZoneSearchCriteria(IZoneSearchCriteria api) throws SiteWhereException {
	GZoneSearchCriteria.Builder grpc = GZoneSearchCriteria.newBuilder();
	if (api.getAreaToken() != null) {
	    grpc.setAreaToken(GOptionalString.newBuilder().setValue(api.getAreaToken()));
	}
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	return grpc.build();
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
	api.setName(grpc.getName());
	api.setBounds(CommonModelConverter.asApiLocations(grpc.getBoundsList()));
	api.setBorderColor(grpc.getBorderColor());
	api.setBorderOpacity(grpc.getBorderOpacity());
	api.setFillColor(grpc.getFillColor());
	api.setFillOpacity(grpc.getFillOpacity());
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
	if (api.getName() != null) {
	    grpc.setName(api.getName());
	}
	if (api.getBounds() != null) {
	    grpc.addAllBounds(CommonModelConverter.asGrpcLocations(api.getBounds()));
	}
	if (api.getBorderColor() != null) {
	    grpc.setBorderColor(api.getBorderColor());
	}
	if (api.getBorderOpacity() != null) {
	    grpc.setBorderOpacity(api.getBorderOpacity());
	}
	if (api.getFillColor() != null) {
	    grpc.setFillColor(api.getFillColor());
	}
	if (api.getFillOpacity() != null) {
	    grpc.setFillOpacity(api.getFillOpacity());
	}
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert device registration payload from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceRegistrationPayload asApiDeviceRegistrationPayload(GDeviceRegistationPayload grpc)
	    throws SiteWhereException {
	DeviceRegistrationPayload api = new DeviceRegistrationPayload();
	api.setSourceId(grpc.getSourceId());
	api.setDeviceToken(grpc.getDeviceToken());
	api.setOriginator(grpc.hasOriginator() ? grpc.getOriginator().getValue() : null);
	api.setDeviceRegistrationRequest(DeviceModelConverter.asApiDeviceRegistrationRequest(grpc.getRegistration()));
	return api;
    }

    /**
     * Convert device registration payload from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceRegistationPayload asGrpcDeviceRegistrationPayload(IDeviceRegistrationPayload api)
	    throws SiteWhereException {
	GDeviceRegistationPayload.Builder grpc = GDeviceRegistationPayload.newBuilder();
	grpc.setSourceId(api.getSourceId());
	grpc.setDeviceToken(api.getDeviceToken());
	if (api.getOriginator() != null) {
	    grpc.setOriginator(GOptionalString.newBuilder().setValue(api.getOriginator()));
	}
	grpc.setRegistration(DeviceModelConverter.asGrpcDeviceRegistrationRequest(api.getDeviceRegistrationRequest()));
	return grpc.build();
    }
}