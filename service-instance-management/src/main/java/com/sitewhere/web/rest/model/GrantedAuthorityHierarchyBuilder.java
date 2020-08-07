/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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