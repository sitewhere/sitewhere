/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.device;

import java.util.Date;

import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.spi.search.device.DeviceSearchType;
import com.sitewhere.spi.search.device.IDeviceBySpecificationParameters;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Adds options specific to device searches.
 * 
 * @author Derek
 */
public class DeviceSearchCriteria extends DateRangeSearchCriteria implements IDeviceSearchCriteria {

	/** Indicates if assigned devices should be returned */
	private boolean includeAssigned = true;

	/** Type of search to execute */
	private DeviceSearchType searchType;

	/** Parameters for search for devices using a given specification */
	private IDeviceBySpecificationParameters deviceBySpecificationParameters;

	public DeviceSearchCriteria(int pageNumber, int pageSize, Date startDate, Date endDate,
			boolean includeAssigned) {
		super(pageNumber, pageSize, startDate, endDate);
		this.includeAssigned = includeAssigned;
	}

	/**
	 * Create search criteria for matching all devices.
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param startDate
	 * @param endDate
	 * @param includeAssigned
	 * @return
	 */
	public static IDeviceSearchCriteria createDefaultSearch(int pageNumber, int pageSize, Date startDate,
			Date endDate, boolean includeAssigned) {
		DeviceSearchCriteria criteria =
				new DeviceSearchCriteria(pageNumber, pageSize, startDate, endDate, includeAssigned);
		criteria.setSearchType(DeviceSearchType.All);
		return criteria;
	}

	/**
	 * Create search criteria for all devices that use a given specification.
	 * 
	 * @param specificationToken
	 * @param pageNumber
	 * @param pageSize
	 * @param startDate
	 * @param endDate
	 * @param includeAssigned
	 * @return
	 */
	public static IDeviceSearchCriteria createDeviceBySpecificationSearch(String specificationToken,
			int pageNumber, int pageSize, Date startDate, Date endDate, boolean includeAssigned) {
		DeviceSearchCriteria criteria =
				new DeviceSearchCriteria(pageNumber, pageSize, startDate, endDate, includeAssigned);
		criteria.setSearchType(DeviceSearchType.UsesSpecification);
		DeviceBySpecificationParameters params = new DeviceBySpecificationParameters();
		params.setSpecificationToken(specificationToken);
		criteria.setDeviceBySpecificationParameters(params);
		return criteria;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.search.device.IDeviceSearchCriteria#isIncludeAssigned()
	 */
	public boolean isIncludeAssigned() {
		return includeAssigned;
	}

	public void setIncludeAssigned(boolean includeAssigned) {
		this.includeAssigned = includeAssigned;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.search.device.IDeviceSearchCriteria#getSearchType()
	 */
	public DeviceSearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(DeviceSearchType searchType) {
		this.searchType = searchType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.search.device.IDeviceSearchCriteria#
	 * getDeviceBySpecificationParameters()
	 */
	public IDeviceBySpecificationParameters getDeviceBySpecificationParameters() {
		return deviceBySpecificationParameters;
	}

	public void setDeviceBySpecificationParameters(
			IDeviceBySpecificationParameters deviceBySpecificationParameters) {
		this.deviceBySpecificationParameters = deviceBySpecificationParameters;
	}
}