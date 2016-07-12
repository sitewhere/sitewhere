package com.sitewhere.tenant.marshaling;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.rest.model.tenant.TenantGroupElement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantGroupElement;

/**
 * Configurable helper class that allows {@link TenantGroupElement} model
 * objects to be created from {@link ITenantGroupElement} SPI objects. Supports
 * different detail levels based on configured options.
 * 
 * @author dadams
 */
public class TenantGroupElementMarshalHelper {

	/** Static logger instance */
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(TenantGroupElementMarshalHelper.class);

	/** Indicates full tenant detail should be marshaled */
	private boolean includeTenantDetails = false;

	/**
	 * Convert a tenant group element SPI object to one that can be marshaled.
	 * 
	 * @param element
	 * @return
	 * @throws SiteWhereException
	 */
	public TenantGroupElement convert(ITenantGroupElement element) throws SiteWhereException {
		TenantGroupElement result = new TenantGroupElement();
		result.setTenantGroupId(element.getTenantGroupId());
		if (isIncludeTenantDetails()) {
			ITenant tenant = SiteWhere.getServer().getTenantManagement().getTenantById(element.getTenantId());
			if (tenant == null) {
				throw new SiteWhereException(
						"Unable to find referenced tenant for tenant group: " + element.getTenantId());
			}
			result.setTenant(Tenant.copy(tenant));
		} else {
			result.setTenantId(element.getTenantId());
		}
		return result;
	}

	public boolean isIncludeTenantDetails() {
		return includeTenantDetails;
	}

	public TenantGroupElementMarshalHelper setIncludeTenantDetails(boolean includeTenantDetails) {
		this.includeTenantDetails = includeTenantDetails;
		return this;
	}
}