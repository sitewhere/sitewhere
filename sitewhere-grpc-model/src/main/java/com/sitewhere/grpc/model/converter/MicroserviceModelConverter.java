/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.grpc.model.MicroserviceModel.GAttributeNode;
import com.sitewhere.grpc.model.MicroserviceModel.GAttributeType;
import com.sitewhere.grpc.model.MicroserviceModel.GElementNode;
import com.sitewhere.grpc.model.MicroserviceModel.GElementNodeList;
import com.sitewhere.grpc.model.MicroserviceModel.GMicroserviceConfiguration;
import com.sitewhere.grpc.model.MicroserviceModel.GNodeType;
import com.sitewhere.grpc.model.MicroserviceModel.GXmlNode;
import com.sitewhere.rest.model.configuration.AttributeNode;
import com.sitewhere.rest.model.configuration.ConfigurationModel;
import com.sitewhere.rest.model.configuration.ElementNode;
import com.sitewhere.rest.model.configuration.XmlNode;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.configuration.model.AttributeType;
import com.sitewhere.spi.microservice.configuration.model.IAttributeNode;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.configuration.model.IElementNode;
import com.sitewhere.spi.microservice.configuration.model.IXmlNode;
import com.sitewhere.spi.microservice.configuration.model.NodeType;

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
     * Convert attribute type from API to GRPC.
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
	case ATTRIBUTE_TYPE_SITE_REFERENCE:
	    return AttributeType.SiteReference;
	case ATTRIBUTE_TYPE_SPECIFICATION_REFERENCE:
	    return AttributeType.SpecificationReference;
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
	case SiteReference:
	    return GAttributeType.ATTRIBUTE_TYPE_SITE_REFERENCE;
	case SpecificationReference:
	    return GAttributeType.ATTRIBUTE_TYPE_SPECIFICATION_REFERENCE;
	case String:
	    return GAttributeType.ATTRIBUTE_TYPE_STRING;
	}
	throw new SiteWhereException("Unknown node type: " + api.name());
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
	api.setChoices(grpc.getChoicesList());
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
	grpc.addAllChoices(api.getChoices());
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
	updateFromElementNode(api, grpc);
	return api;
    }

    /**
     * Update API element node information from GRPC.
     * 
     * @param api
     * @param grpc
     * @throws SiteWhereException
     */
    public static void updateFromElementNode(ElementNode api, GElementNode grpc) throws SiteWhereException {
	if (grpc.getAttributesList().size() > 0) {
	    api.setAttributes(new ArrayList<IAttributeNode>());
	    for (GAttributeNode attribute : grpc.getAttributesList()) {
		api.getAttributes().add(MicroserviceModelConverter.asApiAttributeNode(attribute));
	    }
	}
	api.setRole(grpc.getRole());
	api.setOnDeleteWarning(grpc.hasOnDeleteWarning() ? grpc.getOnDeleteWarning().getValue() : null);
	api.setSpecializes(grpc.getSpecializesMap());
	api.setAttributeGroups(grpc.getAttributeGroupsMap());
	api.setDeprecated(grpc.getDeprecated());
	updateFromGrpcXmlNode(api, grpc.getNode());
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
	grpc.putAllSpecializes(api.getSpecializes());
	grpc.putAllAttributeGroups(api.getAttributeGroups());
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
     * Convert configuration model from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ConfigurationModel asApiConfigurationModel(GMicroserviceConfiguration grpc)
	    throws SiteWhereException {
	ConfigurationModel api = new ConfigurationModel();
	api.setDefaultXmlNamespace(grpc.getDefaultNamespace());
	Map<String, GElementNodeList> elementsByRole = grpc.getElementsByRoleMap();
	for (String role : elementsByRole.keySet()) {
	    GElementNodeList list = elementsByRole.get(role);
	    api.getElementsByRole().put(role, MicroserviceModelConverter.asApiElementNodeList(list));
	}
	updateFromElementNode(api, grpc.getRoot());
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
	grpc.setDefaultNamespace(api.getDefaultXmlNamespace());
	for (String role : api.getElementsByRole().keySet()) {
	    List<IElementNode> list = api.getElementsByRole().get(role);
	    grpc.putElementsByRole(role, MicroserviceModelConverter.asGrpcElementNodeList(list));
	}
	grpc.setRoot(MicroserviceModelConverter.asGrpcElementNode(api));
	return grpc.build();
    }
}
