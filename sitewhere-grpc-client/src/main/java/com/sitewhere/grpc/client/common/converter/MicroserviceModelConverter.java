/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.common.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.grpc.model.MicroserviceModel.GAttributeChoice;
import com.sitewhere.grpc.model.MicroserviceModel.GAttributeNode;
import com.sitewhere.grpc.model.MicroserviceModel.GAttributeType;
import com.sitewhere.grpc.model.MicroserviceModel.GElementNode;
import com.sitewhere.grpc.model.MicroserviceModel.GElementNodeList;
import com.sitewhere.grpc.model.MicroserviceModel.GElementRole;
import com.sitewhere.grpc.model.MicroserviceModel.GMicroserviceConfiguration;
import com.sitewhere.grpc.model.MicroserviceModel.GMicroserviceDetails;
import com.sitewhere.grpc.model.MicroserviceModel.GMicroserviceIdentifier;
import com.sitewhere.grpc.model.MicroserviceModel.GNodeType;
import com.sitewhere.grpc.model.MicroserviceModel.GXmlNode;
import com.sitewhere.rest.model.configuration.AttributeChoice;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ConfigurationModel;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.rest.model.configuration.ElementRole;
import com.sitewhere.rest.model.configuration.XmlNode;
import com.sitewhere.rest.model.microservice.state.MicroserviceDetails;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IAttributeChoice;
import com.sitewhere.spi.microservice.configuration.model.IAttributeNode;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.configuration.model.IElementNode;
import com.sitewhere.spi.microservice.configuration.model.IElementRole;
import com.sitewhere.spi.microservice.configuration.model.IXmlNode;
import com.sitewhere.spi.microservice.configuration.model.NodeType;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;

/**
 * Convert model objects used for interacting with microservices.
 * 
 * @author Derek
 */
public class MicroserviceModelConverter {

    /**
     * Convert node type from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static NodeType asApiNodeType(GNodeType grpc) throws SiteWhereException {
	switch (grpc) {
	case NODE_TYPE_ATTRIBUTE:
	    return NodeType.Attribute;
	case NODE_TYPE_ELEMENT:
	    return NodeType.Element;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown node type: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert node type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GNodeType asGrpcNodeType(NodeType api) throws SiteWhereException {
	switch (api) {
	case Attribute:
	    return GNodeType.NODE_TYPE_ATTRIBUTE;
	case Element:
	    return GNodeType.NODE_TYPE_ELEMENT;
	}
	throw new SiteWhereException("Unknown node type: " + api.name());
    }

    /**
     * Update API XML node from GRPC.
     * 
     * @param api
     * @param grpc
     * @throws SiteWhereException
     */
    public static void updateFromGrpcXmlNode(XmlNode api, GXmlNode grpc) throws SiteWhereException {
	api.setName(grpc.getName());
	api.setIcon(grpc.getIcon());
	api.setDescription(grpc.getDescription());
	api.setNodeType(MicroserviceModelConverter.asApiNodeType(grpc.getType()));
	api.setLocalName(grpc.getLocalName());
	api.setNamespace(grpc.hasNamespace() ? grpc.getNamespace().getValue() : null);
    }

    /**
     * Convert XML node from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GXmlNode asGrpcXmlNode(IXmlNode api) throws SiteWhereException {
	GXmlNode.Builder grpc = GXmlNode.newBuilder();
	grpc.setName(api.getName());
	grpc.setIcon(api.getIcon());
	grpc.setDescription(api.getDescription());
	grpc.setType(MicroserviceModelConverter.asGrpcNodeType(api.getNodeType()));
	grpc.setLocalName(api.getLocalName());
	if (api.getNamespace() != null) {
	    grpc.setNamespace(GOptionalString.newBuilder().setValue(api.getNamespace()).build());
	}
	return grpc.build();
    }

    /**
     * Convert attribute type from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AttributeType asApiAttributeType(GAttributeType grpc) throws SiteWhereException {
	switch (grpc) {
	case ATTRIBUTE_TYPE_BOOLEAN:
	    return AttributeType.Boolean;
	case ATTRIBUTE_TYPE_DECIMAL:
	    return AttributeType.Decimal;
	case ATTRIBUTE_TYPE_INTEGER:
	    return AttributeType.Integer;
	case ATTRIBUTE_TYPE_SCRIPT:
	    return AttributeType.Script;
	case ATTRIBUTE_TYPE_DEVICE_TYPE_REFERENCE:
	    return AttributeType.DeviceTypeReference;
	case ATTRIBUTE_TYPE_CUSTOMER_REFERENCE:
	    return AttributeType.CustomerReference;
	case ATTRIBUTE_TYPE_AREA_REFERENCE:
	    return AttributeType.AreaReference;
	case ATTRIBUTE_TYPE_ASSET_REFERENCE:
	    return AttributeType.AssetReference;
	case ATTRIBUTE_TYPE_STRING:
	    return AttributeType.String;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown attribute type: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert attribute type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAttributeType asGrpcAttributeType(AttributeType api) throws SiteWhereException {
	switch (api) {
	case Boolean:
	    return GAttributeType.ATTRIBUTE_TYPE_BOOLEAN;
	case Decimal:
	    return GAttributeType.ATTRIBUTE_TYPE_DECIMAL;
	case Integer:
	    return GAttributeType.ATTRIBUTE_TYPE_INTEGER;
	case Script:
	    return GAttributeType.ATTRIBUTE_TYPE_SCRIPT;
	case DeviceTypeReference:
	    return GAttributeType.ATTRIBUTE_TYPE_DEVICE_TYPE_REFERENCE;
	case CustomerReference:
	    return GAttributeType.ATTRIBUTE_TYPE_CUSTOMER_REFERENCE;
	case AreaReference:
	    return GAttributeType.ATTRIBUTE_TYPE_AREA_REFERENCE;
	case AssetReference:
	    return GAttributeType.ATTRIBUTE_TYPE_ASSET_REFERENCE;
	case String:
	    return GAttributeType.ATTRIBUTE_TYPE_STRING;
	}
	throw new SiteWhereException("Unknown node type: " + api.name());
    }

    /**
     * Convert attribute choice from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AttributeChoice asApiAttributeChoice(GAttributeChoice grpc) throws SiteWhereException {
	AttributeChoice api = new AttributeChoice();
	api.setName(grpc.getName());
	api.setValue(grpc.getValue());
	return api;
    }

    /**
     * Convert attribute choice list from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static List<IAttributeChoice> asApiAttributeChoiceList(List<GAttributeChoice> grpc)
	    throws SiteWhereException {
	List<IAttributeChoice> api = new ArrayList<IAttributeChoice>();
	for (GAttributeChoice choice : grpc) {
	    api.add(MicroserviceModelConverter.asApiAttributeChoice(choice));
	}
	return api;
    }

    /**
     * Convert attribute choice from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAttributeChoice asGrpcAttributeChoice(IAttributeChoice api) throws SiteWhereException {
	GAttributeChoice.Builder grpc = GAttributeChoice.newBuilder();
	grpc.setName(api.getName());
	grpc.setValue(api.getValue());
	return grpc.build();
    }

    /**
     * Convert attribute choice list from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static List<GAttributeChoice> asGrpcAttributeChoiceList(List<IAttributeChoice> api)
	    throws SiteWhereException {
	List<GAttributeChoice> grpc = new ArrayList<GAttributeChoice>();
	for (IAttributeChoice element : api) {
	    grpc.add(MicroserviceModelConverter.asGrpcAttributeChoice(element));
	}
	return grpc;
    }

    /**
     * Convert attribute node from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AttributeNode asApiAttributeNode(GAttributeNode grpc) throws SiteWhereException {
	AttributeNode api = new AttributeNode();
	api.setType(MicroserviceModelConverter.asApiAttributeType(grpc.getType()));
	api.setDefaultValue(grpc.hasDefaultValue() ? grpc.getDefaultValue().getValue() : null);
	api.setIndex(grpc.getIndex());
	api.setChoices((grpc.getChoicesList().size() > 0)
		? MicroserviceModelConverter.asApiAttributeChoiceList(grpc.getChoicesList())
		: null);
	api.setRequired(grpc.getRequired());
	api.setGroup(grpc.hasGroup() ? grpc.getGroup().getValue() : null);
	updateFromGrpcXmlNode(api, grpc.getNode());
	return api;
    }

    /**
     * Convert attribute node from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAttributeNode asGrpcAttributeNode(IAttributeNode api) throws SiteWhereException {
	GAttributeNode.Builder grpc = GAttributeNode.newBuilder();
	grpc.setType(MicroserviceModelConverter.asGrpcAttributeType(api.getType()));
	if (api.getDefaultValue() != null) {
	    grpc.setDefaultValue(GOptionalString.newBuilder().setValue(api.getDefaultValue()));
	}
	grpc.setIndex(api.isIndex());
	if (api.getChoices() != null) {
	    grpc.addAllChoices(MicroserviceModelConverter.asGrpcAttributeChoiceList(api.getChoices()));
	}
	grpc.setRequired(api.isRequired());
	if (api.getGroup() != null) {
	    grpc.setGroup(GOptionalString.newBuilder().setValue(api.getGroup()));
	}
	grpc.setNode(MicroserviceModelConverter.asGrpcXmlNode(api));
	return grpc.build();
    }

    /**
     * Convert element node from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ElementNode asApiElementNode(GElementNode grpc) throws SiteWhereException {
	ElementNode api = new ElementNode();
	if (grpc.getAttributesList().size() > 0) {
	    api.setAttributes(new ArrayList<IAttributeNode>());
	    for (GAttributeNode attribute : grpc.getAttributesList()) {
		api.getAttributes().add(MicroserviceModelConverter.asApiAttributeNode(attribute));
	    }
	}
	api.setRole(grpc.getRole());
	api.setOnDeleteWarning(grpc.hasOnDeleteWarning() ? grpc.getOnDeleteWarning().getValue() : null);
	api.setSpecializes((grpc.getSpecializesMap().size() > 0) ? grpc.getSpecializesMap() : null);
	api.setAttributeGroups((grpc.getAttributeGroupsMap().size() > 0) ? grpc.getAttributeGroupsMap() : null);
	api.setDeprecated(grpc.getDeprecated());
	updateFromGrpcXmlNode(api, grpc.getNode());
	return api;
    }

    /**
     * Convert element node from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GElementNode asGrpcElementNode(IElementNode api) throws SiteWhereException {
	GElementNode.Builder grpc = GElementNode.newBuilder();
	if (api.getAttributes() != null) {
	    for (IAttributeNode attribute : api.getAttributes()) {
		grpc.addAttributes(MicroserviceModelConverter.asGrpcAttributeNode(attribute));
	    }
	}
	grpc.setRole(api.getRole());
	if (api.getOnDeleteWarning() != null) {
	    grpc.setOnDeleteWarning(GOptionalString.newBuilder().setValue(api.getOnDeleteWarning()).build());
	}
	if (api.getSpecializes() != null) {
	    grpc.putAllSpecializes(api.getSpecializes());
	}
	if (api.getAttributeGroups() != null) {
	    grpc.putAllAttributeGroups(api.getAttributeGroups());
	}
	grpc.setDeprecated(api.isDeprecated());
	grpc.setNode(MicroserviceModelConverter.asGrpcXmlNode(api));
	return grpc.build();
    }

    /**
     * Convert element node list from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static List<IElementNode> asApiElementNodeList(GElementNodeList grpc) throws SiteWhereException {
	List<IElementNode> api = new ArrayList<IElementNode>();
	for (GElementNode element : grpc.getElementsList()) {
	    api.add(MicroserviceModelConverter.asApiElementNode(element));
	}
	return api;
    }

    /**
     * Convert element node list from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GElementNodeList asGrpcElementNodeList(List<IElementNode> api) throws SiteWhereException {
	GElementNodeList.Builder grpc = GElementNodeList.newBuilder();
	for (IElementNode element : api) {
	    grpc.addElements(MicroserviceModelConverter.asGrpcElementNode(element));
	}
	return grpc.build();
    }

    /**
     * Convert element role from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ElementRole asApiElementRole(GElementRole grpc) throws SiteWhereException {
	ElementRole api = new ElementRole();
	api.setName(grpc.getName());
	api.setOptional(grpc.getOptional());
	api.setMultiple(grpc.getMultiple());
	api.setReorderable(grpc.getReorderable());
	api.setPermanent(grpc.getPermanent());
	if (grpc.getChildRolesList().size() > 0) {
	    api.setChildRoles(new ArrayList<String>());
	    api.getChildRoles().addAll(grpc.getChildRolesList());
	}
	if (grpc.getSubtypeRolesList().size() > 0) {
	    api.setSubtypeRoles(new ArrayList<String>());
	    api.getSubtypeRoles().addAll(grpc.getSubtypeRolesList());
	}
	return api;
    }

    /**
     * Convert element role from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GElementRole asGrpcElementRole(IElementRole api) throws SiteWhereException {
	GElementRole.Builder grpc = GElementRole.newBuilder();
	grpc.setName(api.getName());
	grpc.setOptional(api.isOptional());
	grpc.setMultiple(api.isMultiple());
	grpc.setReorderable(api.isReorderable());
	grpc.setPermanent(api.isPermanent());
	if (api.getChildRoles() != null) {
	    grpc.addAllChildRoles(api.getChildRoles());
	}
	if (api.getSubtypeRoles() != null) {
	    grpc.addAllSubtypeRoles(api.getSubtypeRoles());
	}
	return grpc.build();
    }

    /**
     * Convert microservice identifier from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static MicroserviceIdentifier asApiMicroserviceIdentifier(GMicroserviceIdentifier grpc)
	    throws SiteWhereException {
	switch (grpc) {
	case MSID_ASSET_MANAGEMENT:
	    return MicroserviceIdentifier.AssetManagement;
	case MSID_BATCH_OPERATIONS:
	    return MicroserviceIdentifier.BatchOperations;
	case MSID_COMMAND_DELIVERY:
	    return MicroserviceIdentifier.CommandDelivery;
	case MSID_DEVICE_MANAGEMENT:
	    return MicroserviceIdentifier.DeviceManagement;
	case MSID_DEVICE_REGISTRATION:
	    return MicroserviceIdentifier.DeviceRegistration;
	case MSID_DEVICE_STATE:
	    return MicroserviceIdentifier.DeviceState;
	case MSID_EVENT_MANAGEMENT:
	    return MicroserviceIdentifier.EventManagement;
	case MSID_EVENT_SEARCH:
	    return MicroserviceIdentifier.EventSearch;
	case MSID_EVENT_SOURCES:
	    return MicroserviceIdentifier.EventSources;
	case MSID_INBOUND_PROCESSING:
	    return MicroserviceIdentifier.InboundProcessing;
	case MSID_INSTANCE_MANAGEMENT:
	    return MicroserviceIdentifier.InstanceManagement;
	case MSID_LABEL_GENERATION:
	    return MicroserviceIdentifier.LabelGeneration;
	case MSID_OUTBOUND_CONNECTORS:
	    return MicroserviceIdentifier.OutboundConnectors;
	case MSID_RULE_PROCESSING:
	    return MicroserviceIdentifier.RuleProcessing;
	case MSID_SCHEDULE_MANAGEMENT:
	    return MicroserviceIdentifier.ScheduleManagement;
	case MSID_STREAMING_MEDIA:
	    return MicroserviceIdentifier.StreamingMedia;
	case MSID_TENANT_MANAGEMENT:
	    return MicroserviceIdentifier.InstanceManagement;
	case MSID_USER_MANAGEMENT:
	    return MicroserviceIdentifier.InstanceManagement;
	case MSID_WEB_REST:
	    return MicroserviceIdentifier.WebRest;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown microservice identifier: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert microservice identifier from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GMicroserviceIdentifier asGrpcMicroserviceIdentifier(MicroserviceIdentifier api)
	    throws SiteWhereException {
	switch (api) {
	case AssetManagement:
	    return GMicroserviceIdentifier.MSID_ASSET_MANAGEMENT;
	case BatchOperations:
	    return GMicroserviceIdentifier.MSID_BATCH_OPERATIONS;
	case CommandDelivery:
	    return GMicroserviceIdentifier.MSID_COMMAND_DELIVERY;
	case DeviceManagement:
	    return GMicroserviceIdentifier.MSID_DEVICE_MANAGEMENT;
	case DeviceRegistration:
	    return GMicroserviceIdentifier.MSID_DEVICE_REGISTRATION;
	case DeviceState:
	    return GMicroserviceIdentifier.MSID_DEVICE_STATE;
	case EventManagement:
	    return GMicroserviceIdentifier.MSID_EVENT_MANAGEMENT;
	case EventSearch:
	    return GMicroserviceIdentifier.MSID_EVENT_SEARCH;
	case EventSources:
	    return GMicroserviceIdentifier.MSID_EVENT_SOURCES;
	case InboundProcessing:
	    return GMicroserviceIdentifier.MSID_INBOUND_PROCESSING;
	case InstanceManagement:
	    return GMicroserviceIdentifier.MSID_INSTANCE_MANAGEMENT;
	case LabelGeneration:
	    return GMicroserviceIdentifier.MSID_LABEL_GENERATION;
	case OutboundConnectors:
	    return GMicroserviceIdentifier.MSID_OUTBOUND_CONNECTORS;
	case RuleProcessing:
	    return GMicroserviceIdentifier.MSID_RULE_PROCESSING;
	case ScheduleManagement:
	    return GMicroserviceIdentifier.MSID_SCHEDULE_MANAGEMENT;
	case StreamingMedia:
	    return GMicroserviceIdentifier.MSID_STREAMING_MEDIA;
	case WebRest:
	    return GMicroserviceIdentifier.MSID_WEB_REST;
	}
	throw new SiteWhereException("Unknown microservice identifier: " + api.name());
    }

    /**
     * Convert microservice details from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static MicroserviceDetails asApiMicroserviceDetails(GMicroserviceDetails grpc) throws SiteWhereException {
	MicroserviceDetails api = new MicroserviceDetails();
	api.setIdentifier(grpc.getIdentifier());
	api.setHostname(grpc.getHostname());
	api.setName(grpc.getName());
	api.setIcon(grpc.getIcon());
	api.setDescription(grpc.getDescription());
	api.setGlobal(grpc.getGlobal());
	return api;
    }

    /**
     * Convert microservice details from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GMicroserviceDetails asGrpcMicroserviceDetails(IMicroserviceDetails api) throws SiteWhereException {
	GMicroserviceDetails.Builder grpc = GMicroserviceDetails.newBuilder();
	grpc.setIdentifier(api.getIdentifier());
	grpc.setHostname(api.getHostname());
	grpc.setName(api.getName());
	grpc.setIcon(api.getIcon());
	grpc.setDescription(api.getDescription());
	grpc.setGlobal(api.isGlobal());
	return grpc.build();
    }

    /**
     * Convert configuration model from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ConfigurationModel asApiConfigurationModel(GMicroserviceConfiguration grpc)
	    throws SiteWhereException {
	ConfigurationModel api = new ConfigurationModel();
	api.setMicroserviceDetails(MicroserviceModelConverter.asApiMicroserviceDetails(grpc.getMicroservice()));
	api.setDefaultXmlNamespace(grpc.getDefaultNamespace());
	api.setRootRoleId(grpc.getRootRoleId());
	Map<String, GElementNodeList> elementsByRole = grpc.getElementsByRoleMap();
	for (String role : elementsByRole.keySet()) {
	    GElementNodeList list = elementsByRole.get(role);
	    api.getElementsByRole().put(role, MicroserviceModelConverter.asApiElementNodeList(list));
	}
	Map<String, GElementRole> rolesById = grpc.getRolesByIdMap();
	for (String roleId : rolesById.keySet()) {
	    GElementRole role = rolesById.get(roleId);
	    api.getRolesById().put(roleId, MicroserviceModelConverter.asApiElementRole(role));
	}
	return api;
    }

    /**
     * Convert configuration model from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GMicroserviceConfiguration asGrpcConfigurationModel(IConfigurationModel api)
	    throws SiteWhereException {
	GMicroserviceConfiguration.Builder grpc = GMicroserviceConfiguration.newBuilder();
	grpc.setMicroservice(MicroserviceModelConverter.asGrpcMicroserviceDetails(api.getMicroserviceDetails()));
	grpc.setDefaultNamespace(api.getDefaultXmlNamespace());
	grpc.setRootRoleId(api.getRootRoleId());
	for (String role : api.getElementsByRole().keySet()) {
	    List<IElementNode> list = api.getElementsByRole().get(role);
	    grpc.putElementsByRole(role, MicroserviceModelConverter.asGrpcElementNodeList(list));
	}
	for (String roleId : api.getRolesById().keySet()) {
	    IElementRole role = api.getRolesById().get(roleId);
	    grpc.putRolesById(roleId, MicroserviceModelConverter.asGrpcElementRole(role));
	}
	return grpc.build();
    }
}
