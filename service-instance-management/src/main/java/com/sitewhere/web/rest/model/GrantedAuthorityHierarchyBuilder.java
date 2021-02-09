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
import java.util.Collections;
import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;

/**
 * Converts a list of granted authorities into a hierarchical structure.
 */
public class GrantedAuthorityHierarchyBuilder {

    /**
     * Build hierarchy from a list of authorities.
     * 
     * @param auths
     * @return
     * @throws SiteWhereException
     */
    public static List<GrantedAuthorityHierarchyNode> build(List<IGrantedAuthority> auths) throws SiteWhereException {
	List<GrantedAuthorityHierarchyNode> results = new ArrayList<GrantedAuthorityHierarchyNode>();
	List<IGrantedAuthority> roots = new ArrayList<IGrantedAuthority>();
	for (IGrantedAuthority auth : auths) {
	    if (auth.getParent() == null) {
		roots.add(auth);
	    }
	}
	if (roots.size() == 0) {
	    throw new SiteWhereException("No root authorities found.");
	}
	for (IGrantedAuthority root : roots) {
	    auths.remove(root);
	    GrantedAuthorityHierarchyNode node = create(root);
	    addChildren(node, auths);
	    results.add(node);
	}
	Collections.sort(results);
	return results;
    }

    /**
     * Add children for the given node.
     * 
     * @param node
     * @param auths
     */
    protected static void addChildren(GrantedAuthorityHierarchyNode node, List<IGrantedAuthority> auths) {
	List<IGrantedAuthority> matches = new ArrayList<IGrantedAuthority>();
	for (IGrantedAuthority auth : auths) {
	    if (node.getId().equals(auth.getParent())) {
		matches.add(auth);
	    }
	}
	for (IGrantedAuthority match : matches) {
	    auths.remove(match);
	}
	for (IGrantedAuthority match : matches) {
	    GrantedAuthorityHierarchyNode child = create(match);
	    addChildren(child, auths);
	    node.getItems().add(child);
	}
	Collections.sort(node.getItems());
    }

    /**
     * Create a node from an authority.
     * 
     * @param auth
     * @return
     */
    protected static GrantedAuthorityHierarchyNode create(IGrantedAuthority auth) {
	GrantedAuthorityHierarchyNode node = new GrantedAuthorityHierarchyNode();
	node.setId(auth.getAuthority());
	node.setText(auth.getDescription());
	node.setGroup(auth.isGroup());
	return node;
    }
}