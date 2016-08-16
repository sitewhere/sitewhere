package com.sitewhere.groovy;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.configuration.ResourceManagerGlobalConfigurationResolver;
import com.sitewhere.server.resource.ResourceStreamHandler;
import com.sitewhere.server.resource.ResourceUrlConnection;
import com.sitewhere.spi.resource.IResource;

import groovy.util.ResourceConnector;
import groovy.util.ResourceException;

/**
 * Implementation of {@link ResourceConnector} that returns
 * {@link URLConnection} objects that can resolve artifacts using SiteWhere
 * {@link IResource} implementations.
 * 
 * @author Derek
 */
public class SiteWhereResourceConnector implements ResourceConnector {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/** Tenant id if using tenant-specific resources */
	private String tenantId;

	/** Handler used in URLs */
	private ResourceStreamHandler handler = new ResourceStreamHandler();

	public SiteWhereResourceConnector() {
		this(null);
	}

	public SiteWhereResourceConnector(String tenantId) {
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
			} else if (getTenantId() != null) {
				result = new ResourceUrlConnection(
						new URL(ResourceUrlConnection.PROTO_SITEWHERE, ResourceUrlConnection.SUBJECT_RESOURCE, -1,
								"/" + ResourceUrlConnection.TYPE_TENANT_RESOURCE + "/" + getTenantId() + "/"
										+ ResourceManagerGlobalConfigurationResolver.SCRIPTS_FOLDER + "/groovy/" + name,
								handler));
			} else {
				result = new ResourceUrlConnection(
						new URL(ResourceUrlConnection.PROTO_SITEWHERE, ResourceUrlConnection.SUBJECT_RESOURCE, -1,
								"/" + ResourceUrlConnection.TYPE_GLOBAL_RESOURCE + "/global/"
										+ ResourceManagerGlobalConfigurationResolver.SCRIPTS_FOLDER + "/groovy/" + name,
								handler));
			}
			try {
				result.getInputStream();
			} catch (IOException e) {
				throw new ResourceException(e);
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