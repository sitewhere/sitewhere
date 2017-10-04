package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.grpc.model.CommonModel.GDeviceContainerPolicy;
import com.sitewhere.grpc.model.DeviceModel.GDeviceElementSchema;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSlot;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSpecification;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSpecificationCreateRequest;
import com.sitewhere.grpc.model.DeviceModel.GDeviceUnit;
import com.sitewhere.rest.model.device.DeviceSpecification;
import com.sitewhere.rest.model.device.element.DeviceElementSchema;
import com.sitewhere.rest.model.device.element.DeviceSlot;
import com.sitewhere.rest.model.device.element.DeviceUnit;
import com.sitewhere.rest.model.device.request.DeviceSpecificationCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceContainerPolicy;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.element.IDeviceElementSchema;
import com.sitewhere.spi.device.element.IDeviceSlot;
import com.sitewhere.spi.device.element.IDeviceUnit;
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
}