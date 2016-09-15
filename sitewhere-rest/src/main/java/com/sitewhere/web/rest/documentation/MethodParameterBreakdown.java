/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.web.rest.annotations.Concerns.ConcernType;
import com.sitewhere.web.rest.documentation.ParsedParameter.ParameterType;

/**
 * Breaks down parsed parameters by concern type.
 * 
 * @author Derek
 */
public class MethodParameterBreakdown {

    /** Parameters not associated with a concern */
    private List<ParsedParameter> nonConcernParameters = new ArrayList<ParsedParameter>();

    /** Parameters listed by concern */
    private Map<ConcernType, List<ParsedParameter>> concernParameters = new HashMap<ConcernType, List<ParsedParameter>>();

    public List<ParsedParameter> getNonConcernParameters() {
	return nonConcernParameters;
    }

    public void setNonConcernParameters(List<ParsedParameter> nonConcernParameters) {
	this.nonConcernParameters = nonConcernParameters;
    }

    public Map<ConcernType, List<ParsedParameter>> getConcernParameters() {
	return concernParameters;
    }

    public void setConcernParameters(Map<ConcernType, List<ParsedParameter>> concernParameters) {
	this.concernParameters = concernParameters;
    }

    /**
     * Parse method parameters to break them
     * 
     * @param method
     * @return
     */
    public static MethodParameterBreakdown parse(ParsedMethod method) {
	MethodParameterBreakdown breakdown = new MethodParameterBreakdown();
	for (ParsedParameter param : method.getParameters()) {
	    if (param.getType() == ParameterType.Request) {
		if (param.getConcerns().size() == 0) {
		    breakdown.getNonConcernParameters().add(param);
		} else {
		    for (ConcernType concern : param.getConcerns()) {
			List<ParsedParameter> cparams = breakdown.getConcernParameters().get(concern);
			if (cparams == null) {
			    cparams = new ArrayList<ParsedParameter>();
			    breakdown.getConcernParameters().put(concern, cparams);
			}
			cparams.add(param);
		    }
		}
	    }
	}
	return breakdown;
    }
}