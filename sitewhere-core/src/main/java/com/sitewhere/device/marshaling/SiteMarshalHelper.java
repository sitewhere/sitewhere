package com.sitewhere.device.marshaling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.device.DeviceAssignment;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.device.AssignmentSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Configurable helper class that allows {@link Site} model objects to be
 * created from {@link ISite} SPI objects.
 * 
 * @author Derek
 */
public class SiteMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Asset module manager */
    private IAssetModuleManager assetModuleManager;

    /** Indicates whether assignments for site should be included */
    private boolean includeAssignements = false;

    /** Indicates whether zones are to be included */
    private boolean includeZones = false;

    /** Device assignment marshal helper */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    public SiteMarshalHelper(IDeviceManagement deviceManagement, IAssetModuleManager assetModuleManager) {
	this.deviceManagement = deviceManagement;
	this.assetModuleManager = assetModuleManager;

	this.assignmentHelper = new DeviceAssignmentMarshalHelper(deviceManagement);
	assignmentHelper.setIncludeDevice(true);
	assignmentHelper.setIncludeAsset(true);
	assignmentHelper.setIncludeSite(false);
    }

    /**
     * Convert the SPI into a model object based on marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public Site convert(ISite source) throws SiteWhereException {
	Site site = Site.copy(source);
	if (isIncludeAssignements()) {
	    AssignmentSearchCriteria criteria = new AssignmentSearchCriteria(1, 0);
	    criteria.setStatus(DeviceAssignmentStatus.Active);
	    ISearchResults<IDeviceAssignment> matches = getDeviceManagement()
		    .getDeviceAssignmentsForSite(site.getToken(), criteria);
	    List<DeviceAssignment> assignments = new ArrayList<DeviceAssignment>();
	    for (IDeviceAssignment match : matches.getResults()) {
		assignments.add(assignmentHelper.convert(match, getAssetModuleManager()));
	    }
	    site.setDeviceAssignments(assignments);
	}
	if (isIncludeZones()) {
	    ISearchResults<IZone> matches = getDeviceManagement().listZones(source.getToken(), SearchCriteria.ALL);
	    List<Zone> zones = new ArrayList<Zone>();
	    List<IZone> reordered = matches.getResults();
	    Collections.sort(reordered, new Comparator<IZone>() {

		@Override
		public int compare(IZone z0, IZone z1) {
		    return z0.getName().compareTo(z1.getName());
		}
	    });
	    for (IZone match : matches.getResults()) {
		zones.add(Zone.copy(match));
	    }
	    site.setZones(zones);
	}
	return site;
    }

    /**
     * Get device management implementation.
     * 
     * @param tenant
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceManagement getDeviceManagement(ITenant tenant) throws SiteWhereException {
	return SiteWhere.getServer().getDeviceManagement(tenant);
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IAssetModuleManager getAssetModuleManager() {
	return assetModuleManager;
    }

    public void setAssetModuleManager(IAssetModuleManager assetModuleManager) {
	this.assetModuleManager = assetModuleManager;
    }

    public boolean isIncludeAssignements() {
	return includeAssignements;
    }

    public void setIncludeAssignements(boolean includeAssignements) {
	this.includeAssignements = includeAssignements;
    }

    public boolean isIncludeZones() {
	return includeZones;
    }

    public void setIncludeZones(boolean includeZones) {
	this.includeZones = includeZones;
    }
}