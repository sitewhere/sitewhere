/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.marshaling;

import org.apache.commons.lang.StringUtils;

import com.sitewhere.rest.model.batch.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.command.ICommandParameter;
import com.sitewhere.spi.device.group.IDeviceGroup;

/**
 * Helper class that creates an HTML version of a command for display in the
 * user interface. TODO: This class is really presentation-only, so it should be
 * moved to sitewhere-web at some point. For now, there is a dependency from the
 * marshal helper that prevents it.
 * 
 * @author Derek
 */
public class CommandHtmlHelper {

    /**
     * Get an HTML version of a command invocation that can be shown in the UI.
     * 
     * @param invocation
     * @return
     * @throws SiteWhereException
     */
    public static String getHtml(DeviceCommandInvocation invocation) throws SiteWhereException {
	DeviceCommand command = invocation.getCommand();
	if (command == null) {
	    throw new SiteWhereException("Command information must be populated to generate HTML.");
	}
	String html = "";
	html += "<span class='sw-spec-command-name'>" + command.getName() + "</span>(";
	int i = 0;
	for (ICommandParameter param : command.getParameters()) {
	    String value = invocation.getParameterValues().get(param.getName());
	    if (param.isRequired()) {
		html += "<span class='sw-spec-command-param-required'>";
	    }
	    if (i++ > 0) {
		html += ", ";
	    }
	    html += " <span class='sw-spec-command-param-name'>" + param.getName() + "</span>";
	    if (value != null) {
		html += ":<span class='sw-spec-command-param-type' title='" + param.getType() + "'>" + value
			+ "</span> ";
	    }
	    if (param.isRequired()) {
		html += "</span>";
	    }
	}
	html += ")";
	return html;
    }

    /**
     * Get HTML that indicates critieria used to determine which devices are
     * included in a batch command invocation request.
     * 
     * @param criteria
     * @param relativePath
     * @return
     * @throws SiteWhereException
     */
    public static String getHtml(BatchCommandForCriteriaRequest criteria, IDeviceManagement devices,
	    String relativePath) throws SiteWhereException {
	if (StringUtils.isEmpty(criteria.getSpecificationToken())) {
	    throw new SiteWhereException("Specification token must be populated to generate HTML.");
	}
	IDeviceSpecification specification = devices.getDeviceSpecificationByToken(criteria.getSpecificationToken());
	if (specification == null) {
	    throw new SiteWhereException("Invalid specification reference: " + criteria.getSpecificationToken());
	}
	String html = "all devices";
	if (!StringUtils.isEmpty(criteria.getSiteToken())) {
	    ISite site = devices.getSiteByToken(criteria.getSiteToken());
	    if (site == null) {
		throw new SiteWhereException("Invalid site reference: " + criteria.getGroupToken());
	    }
	    html += " belonging to site <a href=\"" + relativePath + "/sites/" + site.getToken() + ".html\">"
		    + site.getName() + "</a>";
	}
	html += " with specification <a href=\"" + relativePath + "/specifications/" + specification.getToken()
		+ ".html\">" + specification.getName() + "</a>";
	if (!StringUtils.isEmpty(criteria.getGroupToken())) {
	    IDeviceGroup group = devices.getDeviceGroup(criteria.getGroupToken());
	    if (group == null) {
		throw new SiteWhereException("Invalid group reference: " + criteria.getGroupToken());
	    }
	    html += " and belonging to group <a href=\"" + relativePath + "/groups/" + group.getToken() + ".html\">"
		    + group.getName() + "</a>";
	} else if (!StringUtils.isEmpty(criteria.getGroupsWithRole())) {
	    html += " and belonging to groups with role <strong>" + criteria.getGroupsWithRole() + "</strong>";
	}
	return html;
    }
}