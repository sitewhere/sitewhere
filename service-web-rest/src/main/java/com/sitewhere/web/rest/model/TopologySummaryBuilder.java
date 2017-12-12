/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.spi.microservice.state.IInstanceTopologyEntry;
import com.sitewhere.spi.microservice.state.IInstanceTopologySnapshot;
import com.sitewhere.spi.microservice.state.IMicroserviceDetails;

public class TopologySummaryBuilder {

    /**
     * Build summary from topology snapshot.
     * 
     * @param snapshot
     * @return
     */
    public static List<InstanceTopologySummary> build(IInstanceTopologySnapshot snapshot) {
	Map<String, InstanceTopologySummary> summaries = new HashMap<>();
	for (IInstanceTopologyEntry entry : snapshot.getTopologyEntries()) {
	    IMicroserviceDetails microservice = entry.getMicroserviceDetails();
	    InstanceTopologySummary summary = summaries.get(microservice.getIdentifier());
	    if (summary == null) {
		summary = new InstanceTopologySummary();
		summary.setIdentifier(microservice.getIdentifier());
		summary.setName(microservice.getName());
		summary.setIcon(microservice.getIcon());
		summary.setDescription(microservice.getDescription());
		summary.setGlobal(microservice.isGlobal());
		summary.getHostnames().add(microservice.getHostname());
		summaries.put(microservice.getIdentifier(), summary);
	    } else {
		summary.getHostnames().add(microservice.getHostname());
	    }
	}
	List<InstanceTopologySummary> list = new ArrayList<>();
	list.addAll(summaries.values());
	list.sort(new Comparator<InstanceTopologySummary>() {

	    @Override
	    public int compare(InstanceTopologySummary o1, InstanceTopologySummary o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	return list;
    }
}