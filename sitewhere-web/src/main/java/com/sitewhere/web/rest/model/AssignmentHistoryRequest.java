/*
* $Id$
* --------------------------------------------------------------------------------------
* Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
*
* The software in this package is published under the terms of the CPAL v1.0
* license, a copy of which has been included with this distribution in the
* LICENSE.txt file.
*/
package com.sitewhere.web.rest.model;

import java.util.Date;
import java.util.List;

/**
 * Request for history on one or more assignments for a period of time.
 * 
 * @author Derek Adams
 */
public class AssignmentHistoryRequest {

	/** Assignment tokens to load */
	private List<String> assignmentTokens;
	
	/** Start date for search */
	private Date startDate;
	
	/** End date for search */
	private Date endDate;

	public List<String> getAssignmentTokens() {
		return assignmentTokens;
	}

	public void setAssignmentTokens(List<String> assignmentTokens) {
		this.assignmentTokens = assignmentTokens;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}