/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Used by REST service to marshal the hierarchy of granted authorities.
 */
public class GrantedAuthorityHierarchyNode implements Comparable<GrantedAuthorityHierarchyNode> {

    /** Authority id */
    private String id;

    /** Text description */
    private String text;

    /** Flag for group */
    private boolean group;

    /** List of contained authorities */
    private List<GrantedAuthorityHierarchyNode> items = new ArrayList<GrantedAuthorityHierarchyNode>();

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getText() {
	return text;
    }

    public void setText(String text) {
	this.text = text;
    }

    public boolean isGroup() {
	return group;
    }

    public void setGroup(boolean group) {
	this.group = group;
    }

    public List<GrantedAuthorityHierarchyNode> getItems() {
	return items;
    }

    public void setItems(List<GrantedAuthorityHierarchyNode> items) {
	this.items = items;
    }

    @Override
    public int compareTo(GrantedAuthorityHierarchyNode o) {
	return getText().compareTo(o.getText());
    }
}