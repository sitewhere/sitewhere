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
package com.sitewhere.device.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.rest.model.search.TreeNode;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.common.IBrandedTreeEntity;

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
    public static List<TreeNode> buildTree(List<? extends IBrandedTreeEntity> flat) throws SiteWhereException {
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
    protected static List<TreeNode> buildTree(Map<UUID, List<IBrandedTreeEntity>> childrenById, UUID id) {
	List<TreeNode> nodes = new ArrayList<>();
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
