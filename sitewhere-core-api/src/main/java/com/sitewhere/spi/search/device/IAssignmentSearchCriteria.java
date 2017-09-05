package com.sitewhere.spi.search.device;

import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.search.ISearchCriteria;

/**
 * Criteria available for filtering assignment search results.
 * 
 * @author Derek
 */
public interface IAssignmentSearchCriteria extends ISearchCriteria {

    /**
     * Only return assignments with the given status.
     * 
     * @return
     */
    public DeviceAssignmentStatus getStatus();
}