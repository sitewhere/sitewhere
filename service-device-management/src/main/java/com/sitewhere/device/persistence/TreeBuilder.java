/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.rest.model.search.TreeNode;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IBrandedTreeEntity;
import com.sitewhere.spi.search.ITreeNode;

public class TreeBuilder {

    /** Nil marker for root nodes */
    public static final UUID ROOT = new UUID(0L, 0L);

    /**
     * Build a tree based on a list of records which contain parent references.
     * 
     * @param flat
     * @return
     * @throws SiteWhereException
     */
    public static List<ITreeNode> buildTree(List<? extends IBrandedTreeEntity> flat) throws SiteWhereException {
	Map<UUID, List<IBrandedTreeEntity>> byParentId = getChildrenByParentId(flat);
	return buildTree(byParentId, ROOT);
    }

    /**
     * Build tree of nodes which have the given parent id.
     * 
     * @param childrenById
     * @param id
     * @return
     */
    protected static List<ITreeNode> buildTree(Map<UUID, List<IBrandedTreeEntity>> childrenById, UUID id) {
	List<ITreeNode> nodes = new ArrayList<>();
	List<? extends IBrandedTreeEntity> entities = childrenById.get(id);
	if (entities != null) {
	    for (IBrandedTreeEntity entity : entities) {
		TreeNode node = new TreeNode();
		node.setToken(entity.getToken());
		node.setName(entity.getName());
		node.setIcon(entity.getIcon());
		node.setMetadata(
			entity.getMetadata() != null && entity.getMetadata().size() > 0 ? entity.getMetadata() : null);
		node.setChildren(buildTree(childrenById, entity.getId()));
		nodes.add(node);
	    }
	}
	return nodes;
    }

    /**
     * Build a map of children by parent UUID.
     * 
     * @param flat
     * @return
     */
    protected static Map<UUID, List<IBrandedTreeEntity>> getChildrenByParentId(
	    List<? extends IBrandedTreeEntity> flat) {
	Map<UUID, List<IBrandedTreeEntity>> byParentId = new HashMap<>();
	for (IBrandedTreeEntity entity : flat) {
	    UUID id = entity.getParentId() != null ? entity.getParentId() : ROOT;
	    List<IBrandedTreeEntity> children = byParentId.get(id);
	    if (children == null) {
		children = new ArrayList<IBrandedTreeEntity>();
		byParentId.put(id, children);
	    }
	    children.add(entity);
	}
	return byParentId;
    }
}
