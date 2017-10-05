package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sitewhere.grpc.model.CommonModel.GDeviceContainerPolicy;
import com.sitewhere.grpc.model.CommonModel.GDeviceGroupElementType;
import com.sitewhere.grpc.model.CommonModel.GOptionalBoolean;
import com.sitewhere.grpc.model.CommonModel.GParameterType;
import com.sitewhere.grpc.model.CommonModel.GSiteReference;
import com.sitewhere.grpc.model.DeviceModel.GCommandParameter;
import com.sitewhere.grpc.model.DeviceModel.GDevice;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommand;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommandCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceElementMapping;
import com.sitewhere.grpc.model.DeviceModel.GDeviceElementSchema;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroup;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupElement;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupElementCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSearchCriteria;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSlot;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSpecification;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSpecificationCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSpecificationReference;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatus;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatusCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceUnit;
import com.sitewhere.rest.model.device.Device;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.DeviceStatus;
import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.rest.model.device.group.DeviceGroup;
import com.sitewhere.rest.model.device.group.DeviceGroupElement;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceGroupElementCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceStatusCreateRequest;
import com.sitewhere.rest.model.search.device.DeviceSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.ParameterType;
import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.element.IDeviceSlot;
import com.sitewhere.spi.device.element.IDeviceUnit;
import com.sitewhere.spi.device.group.GroupElementType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
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
    public static GDeviceElementSchema asGrpcDeviceElementScheme(IDeviceElementSchema api) throws SiteWhereException {
	GDeviceElementSchema.Builder grpc = GDeviceElementSchema.newBuilder();
	grpc.addAllSlots(DeviceModelConverter.asGrpcDeviceSlots(api.getDeviceSlots()));
	grpc.addAllUnits(DeviceModelConverter.asGrpcDeviceUnits(api.getDeviceUnits()));
	return grpc.build();
    }

    /**
     * Convert device specification create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceSpecificationCreateRequest asApiDeviceSpecificationCreateRequest(
	    GDeviceSpecificationCreateRequest grpc) throws SiteWhereException {
	DeviceSpecificationCreateRequest api = new DeviceSpecificationCreateRequest();
	api.setToken(grpc.getToken());
	api.setName(grpc.getName());
	api.setAssetModuleId(grpc.getAssetModuleId());
	api.setAssetId(grpc.getAssetId());
	api.setContainerPolicy(DeviceModelConverter.asApiDeviceContainerPolicy(grpc.getContainerPolicy()));
	api.setDeviceElementSchema(DeviceModelConverter.asApiDeviceElementSchema(grpc.getDeviceElementSchema()));
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert device specification create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceSpecificationCreateRequest asGrpcDeviceSpecificationCreateRequest(
	    IDeviceSpecificationCreateRequest api) throws SiteWhereException {
	GDeviceSpecificationCreateRequest.Builder grpc = GDeviceSpecificationCreateRequest.newBuilder();
	grpc.setToken(api.getToken());
	grpc.setName(api.getName());
	grpc.setAssetModuleId(api.getAssetModuleId());
	grpc.setAssetId(api.getAssetId());
	grpc.setContainerPolicy(DeviceModelConverter.asGrpcDeviceContainerPolicy(api.getContainerPolicy()));
	grpc.setDeviceElementSchema(DeviceModelConverter.asGrpcDeviceElementScheme(api.getDeviceElementSchema()));
	grpc.putAllMetadata(api.getMetadata());
	return grpc.build();
    }

    /**
     * Convert device specification from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceSpecification asApiDeviceSpecification(GDeviceSpecification grpc) throws SiteWhereException {
	DeviceSpecification api = new DeviceSpecification();
	api.setToken(grpc.getToken());
	api.setName(grpc.getName());
	api.setAssetModuleId(grpc.getAssetModuleId());
	api.setAssetId(grpc.getAssetId());
	api.setContainerPolicy(DeviceModelConverter.asApiDeviceContainerPolicy(grpc.getContainerPolicy()));
	api.setDeviceElementSchema(DeviceModelConverter.asApiDeviceElementSchema(grpc.getDeviceElementSchema()));
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert device specification from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceSpecification asGrpcDeviceSpecification(IDeviceSpecification api) throws SiteWhereException {
	GDeviceSpecification.Builder grpc = GDeviceSpecification.newBuilder();
	grpc.setToken(api.getToken());
	grpc.setName(api.getName());
	grpc.setAssetModuleId(api.getAssetModuleId());
	grpc.setAssetId(api.getAssetId());
	grpc.setContainerPolicy(DeviceModelConverter.asGrpcDeviceContainerPolicy(api.getContainerPolicy()));
	if (api.getDeviceElementSchema() != null) {
	    grpc.setDeviceElementSchema(DeviceModelConverter.asGrpcDeviceElementScheme(api.getDeviceElementSchema()));
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
     * Convert device command create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandCreateRequest asApiDeviceCommandCreateRequest(GDeviceCommandCreateRequest grpc)
	    throws SiteWhereException {
	DeviceCommandCreateRequest api = new DeviceCommandCreateRequest();
	api.setToken(grpc.getToken());
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
	grpc.setToken(api.getToken());
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
	api.setToken(grpc.getToken());
	api.setSpecificationToken(grpc.getSpecificationToken());
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
	grpc.setToken(api.getToken());
	grpc.setSpecificationToken(api.getSpecificationToken());
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
	api.setCode(grpc.getCode());
	api.setName(grpc.getName());
	api.setSpecificationToken(grpc.getSpecificationToken());
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
	grpc.setCode(api.getCode());
	grpc.setName(api.getName());
	grpc.setSpecificationToken(api.getSpecificationToken());
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
	api.setHardwareId(grpc.getHardwareId());
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
	grpc.setHardwareId(api.getHardwareId());
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
	for (IDeviceElementMapping api : apis) {
	    grpcs.add(DeviceModelConverter.asGrpcDeviceElementMapping(api));
	}
	return grpcs;
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
	api.setHardwareId(grpc.getHardwareId());
	api.setParentHardwareId(grpc.getParentHardwareId());
	api.setSpecificationToken(grpc.getSpecificationToken());
	api.setSiteToken(grpc.getSiteToken());
	api.setStatus(grpc.getStatus());
	api.setComments(grpc.getComments());
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
	grpc.setHardwareId(api.getHardwareId());
	grpc.setParentHardwareId(api.getParentHardwareId());
	grpc.setSpecificationToken(api.getSpecificationToken());
	grpc.setSiteToken(api.getSiteToken());
	grpc.setStatus(api.getStatus());
	grpc.setComments(api.getComments());
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
	api.setHardwareId(grpc.getHardwareId());
	api.setSpecificationToken(grpc.getSpecificationToken());
	api.setSiteToken(grpc.getSiteToken());
	api.setStatus(grpc.getStatus());
	api.setComments(grpc.getComments());
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
	grpc.setHardwareId(api.getHardwareId());
	grpc.setParentHardwareId(api.getParentHardwareId());
	grpc.setSpecificationToken(api.getSpecificationToken());
	grpc.setSiteToken(api.getSiteToken());
	grpc.setStatus(api.getStatus());
	grpc.setComments(api.getComments());
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
	api.setSpecificationToken(grpc.hasSpecification() ? grpc.getSpecification().getToken() : null);
	api.setSiteToken(grpc.hasSite() ? grpc.getSite().getToken() : null);
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
	grpc.setCreatedAfter(CommonModelConverter.asGrpcTimestamp(api.getStartDate()));
	grpc.setCreatedBefore(CommonModelConverter.asGrpcTimestamp(api.getEndDate()));
	if (api.getSpecificationToken() != null) {
	    grpc.setSpecification(
		    GDeviceSpecificationReference.newBuilder().setToken(api.getSpecificationToken()).build());
	}
	if (api.getSiteToken() != null) {
	    grpc.setSite(GSiteReference.newBuilder().setToken(api.getSiteToken()).build());
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
	api.setToken(grpc.getToken());
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
	grpc.setToken(api.getToken());
	grpc.setName(api.getName());
	grpc.setDescription(api.getDescription());
	grpc.addAllRoles(api.getRoles());
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
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
     * Convert a device group element from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceGroupElement asApiDeviceGroupElement(GDeviceGroupElement grpc) throws SiteWhereException {
	DeviceGroupElement api = new DeviceGroupElement();
	api.setType(DeviceModelConverter.asApiDeviceGroupElementType(grpc.getType()));
	api.setElementId(grpc.getElementId());
	api.setRoles(grpc.getRolesList());
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
	grpc.setElementId(api.getElementId());
	grpc.addAllRoles(api.getRoles());
	return grpc.build();
    }
}