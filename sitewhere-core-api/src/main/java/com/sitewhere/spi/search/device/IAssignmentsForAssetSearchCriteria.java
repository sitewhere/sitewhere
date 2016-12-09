package com.sitewhere.spi.search.device;

import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Search criteria for locating assignments for a given asset type.
 * 
 * @author Derek
 */
public interface IAssignmentsForAssetSearchCriteria extends ISearchCriteria {

    /**
     * Limits search to a given site.
     * 
     * @return
     */
    public String getSiteToken();

    /**
     * Only return assignments with the given status.
     * 
     * @return
     */
    public DeviceAssignmentStatus getStatus();
}