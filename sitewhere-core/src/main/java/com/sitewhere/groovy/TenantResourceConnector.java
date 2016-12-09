package com.sitewhere.groovy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.resource.ResourceStreamHandler;
import com.sitewhere.server.resource.ResourceUrlConnection;
import com.sitewhere.spi.configuration.IDefaultResourcePaths;

import groovy.util.ResourceConnector;
import groovy.util.ResourceException;

/**
 * Implementation of {@link ResourceConnector} that loads resources from tenant
 * locations, but falls back to a global resource by the same name if not found.
 * 
 * @author Derek
 */
public class TenantResourceConnector implements ResourceConnector {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant id if using tenant-specific resources */
    private String tenantId;

    /** Handler used in URLs */
    private ResourceStreamHandler handler = new ResourceStreamHandler();

    public TenantResourceConnector(String tenantId) {
	this.tenantId = tenantId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * groovy.util.ResourceConnector#getResourceConnection(java.lang.String)
     */
    @Override
    public URLConnection getResourceConnection(String name) throws ResourceException {
	LOGGER.debug("Calling getResourceConnection() with " + name);
	try {
	    URLConnection result = null;
	    if (name.startsWith(ResourceUrlConnection.PROTO_SITEWHERE)) {
		result = new ResourceUrlConnection(new URL(null, name, handler));
	    } else {
		result = new ResourceUrlConnection(new URL(ResourceUrlConnection.PROTO_SITEWHERE,
			ResourceUrlConnection.SUBJECT_RESOURCE, -1, "/" + ResourceUrlConnection.TYPE_TENANT_RESOURCE
				+ "/" + getTenantId() + "/" + IDefaultResourcePaths.SCRIPTS_FOLDER + "/groovy/" + name,
			handler));
	    }
	    try {
		result.getInputStream();
	    } catch (IOException e) {
		result = GlobalResourceConnector.getGlobalResourceConnection(name, handler);
		try {
		    result.getInputStream();
		    LOGGER.info(
			    "Unable to find tenant resource '" + name + "', but found global resource with same name.");
		} catch (IOException e1) {
		    throw new ResourceException(e1);
		}
	    }
	    return result;
	} catch (MalformedURLException e) {
	    throw new ResourceException("Invalid resource URL.", e);
	}
    }

    public String getTenantId() {
	return tenantId;
    }

    public void setTenantId(String tenantId) {
	this.tenantId = tenantId;
    }
}
