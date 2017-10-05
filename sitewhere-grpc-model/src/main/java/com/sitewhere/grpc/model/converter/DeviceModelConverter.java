package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.grpc.model.CommonModel.GDeviceContainerPolicy;
import com.sitewhere.grpc.model.CommonModel.GParameterType;
import com.sitewhere.grpc.model.DeviceModel.GCommandParameter;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommand;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommandCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceElementSchema;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSlot;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSpecification;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSpecificationCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceUnit;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.command.CommandParameter;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.rest.model.device.request.DeviceCommandCreateRequest;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.command.ParameterType;
import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.element.IDeviceSlot;
import com.sitewhere.spi.device.element.IDeviceUnit;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;

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
}