/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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