package com.sitewhere.device.marshaling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.device.Site;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
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

	/** Tenant */
	private ITenant tenant;

	/** Indicates whether zones are to be included */
	private boolean includeZones = false;

	public SiteMarshalHelper(ITenant tenant) {
		this.tenant = tenant;
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
		if (isIncludeZones()) {
			ISearchResults<IZone> matches = getDeviceManagement(getTenant()).listZones(source.getToken(),
					SearchCriteria.ALL);
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

	public ITenant getTenant() {
		return tenant;
	}

	public void setTenant(ITenant tenant) {
		this.tenant = tenant;
	}

	public boolean isIncludeZones() {
		return includeZones;
	}

	public void setIncludeZones(boolean includeZones) {
		this.includeZones = includeZones;
	}
}