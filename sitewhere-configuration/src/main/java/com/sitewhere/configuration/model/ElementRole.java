/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.configuration.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.configuration.model.ElementRole.Serializer;

/**
 * Used to indicate role of an element.
 * 
 * @author Derek
 */
@JsonSerialize(using = Serializer.class)
public class ElementRole {

    /** Role name */
    private String name;

    /** Indicates if role is optional */
    private boolean optional;

    /** Indicates if multiple elements in role are allowed */
    private boolean multiple;

    /** Indicates if elements in role can be reordered */
    private boolean reorderable;

    /** Indicates if element is permanent */
    private boolean permanent;

    /** Child roles in the order they should appear */
    private IElementRoleProvider[] children;

    /** Subtypes that specialize the given role */
    private IElementRoleProvider[] subtypes;

    public static ElementRole build(String name, boolean optional, boolean multiple, boolean reorderable) {
	return build(name, optional, multiple, reorderable, new IElementRoleProvider[0]);
    }

    public static ElementRole build(String name, boolean optional, boolean multiple, boolean reorderable,
	    IElementRoleProvider[] children) {
	return build(name, optional, multiple, reorderable, children, new IElementRoleProvider[0]);
    }

    public static ElementRole build(String name, boolean optional, boolean multiple, boolean reorderable,
	    IElementRoleProvider[] children, IElementRoleProvider[] subtypes) {
	return build(name, optional, multiple, reorderable, children, subtypes, false);
    }

    public static ElementRole build(String name, boolean optional, boolean multiple, boolean reorderable,
	    IElementRoleProvider[] children, IElementRoleProvider[] subtypes, boolean permanent) {
	ElementRole role = new ElementRole();
	role.name = name;
	role.optional = optional;
	role.multiple = multiple;
	role.reorderable = reorderable;
	role.children = children;
	role.subtypes = subtypes;
	role.permanent = permanent;
	return role;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public boolean isOptional() {
	return optional;
    }

    public void setOptional(boolean optional) {
	this.optional = optional;
    }

    public boolean isMultiple() {
	return multiple;
    }

    public void setMultiple(boolean multiple) {
	this.multiple = multiple;
    }

    public boolean isReorderable() {
	return reorderable;
    }

    public void setReorderable(boolean reorderable) {
	this.reorderable = reorderable;
    }

    public boolean isPermanent() {
	return permanent;
    }

    public void setPermanent(boolean permanent) {
	this.permanent = permanent;
    }

    public IElementRoleProvider[] getChildren() {
	return children;
    }

    public void setChildren(IElementRoleProvider[] children) {
	this.children = children;
    }

    public IElementRoleProvider[] getSubtypes() {
	return subtypes;
    }

    public void setSubtypes(IElementRoleProvider[] subtypes) {
	this.subtypes = subtypes;
    }

    public static class Serializer extends JsonSerializer<ElementRole> {

	public void serialize(ElementRole value, JsonGenerator generator, SerializerProvider provider)
		throws IOException, JsonProcessingException {
	    generator.writeStartObject();
	    generator.writeFieldName("name");
	    generator.writeString(value.getName());
	    generator.writeFieldName("optional");
	    generator.writeBoolean(value.isOptional());
	    generator.writeFieldName("multiple");
	    generator.writeBoolean(value.isMultiple());
	    generator.writeFieldName("reorderable");
	    generator.writeBoolean(value.isReorderable());
	    generator.writeFieldName("permanent");
	    generator.writeBoolean(value.isPermanent());

	    if (value.getChildren() != null) {
		generator.writeArrayFieldStart("children");
		for (IElementRoleProvider child : value.getChildren()) {
		    generator.writeString(child.getName());
		}
		generator.writeEndArray();
	    }

	    if (value.getSubtypes() != null) {
		generator.writeArrayFieldStart("subtypes");
		for (IElementRoleProvider child : value.getSubtypes()) {
		    generator.writeString(child.getName());
		}
		generator.writeEndArray();
	    }

	    generator.writeEndObject();
	}
    }
}