package com.sitewhere.server.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.resource.IResource;

/**
 * Parse URLs that resolve to resources in SiteWhere. URLs can be in the form:
 * 
 * <pre>
 * sitewhere://resource/global/path/to/resource
 * </pre>
 * 
 * or
 * 
 * <pre>
 * sitewhere://resource/tenant/tenantid/path/to/resource
 * </pre>
 * 
 * depending on whether the reference is to a global or tenant-specific
 * resource.
 * 
 * @author Derek
 */
public class ResourceUrlConnection extends URLConnection {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	/** Protocol that specifies a SiteWhere URL */
	public static final String PROTO_SITEWHERE = "sitewhere";

	/** Subject */
	public static final String SUBJECT_RESOURCE = "resource";

	/** Marker that specifies a tenant-relative resource path */
	public static final String TYPE_GLOBAL_RESOURCE = "global";

	/** Marker that specifies a tenant-relative resource path */
	public static final String TYPE_TENANT_RESOURCE = "tenant";

	/** Resource referenced in connection */
	private IResource resource;

	public ResourceUrlConnection(URL url) {
		super(url);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#connect()
	 */
	@Override
	public void connect() throws IOException {
		locateResourceByUrl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#getContent()
	 */
	@Override
	public Object getContent() throws IOException {
		LOGGER.debug("Calling getContent() on URLConnection for " + getURL().toExternalForm());
		return assureResource().getContent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#getContentLength()
	 */
	@Override
	public int getContentLength() {
		try {
			LOGGER.debug("Calling getContentLength() on URLConnection for " + getURL().toExternalForm());
			return assureResource().getContent().length;
		} catch (IOException e) {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		LOGGER.debug("Calling getinputStream() on URLConnection for " + getURL().toExternalForm());
		return new ByteArrayInputStream(assureResource().getContent());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.net.URLConnection#getLastModified()
	 */
	@Override
	public long getLastModified() {
		try {
			LOGGER.debug("Calling getLastModified() on URLConnection for " + getURL().toExternalForm());
			return assureResource().getLastModified();
		} catch (IOException e) {
			return 0;
		}
	}

	/**
	 * Assures that a resource is avialable for other operations.
	 * 
	 * @return
	 * @throws RuntimeException
	 */
	protected IResource assureResource() throws IOException {
		if (!connected) {
			connect();
		}
		if (this.resource == null) {
			throw new IOException("Resource not found: " + getURL().toExternalForm());
		}
		return resource;
	}

	/**
	 * Locate the {@link IResource} specified by the URL.
	 * 
	 * @throws IOException
	 */
	protected void locateResourceByUrl() throws IOException {
		String protocol = getURL().getProtocol();
		if (!PROTO_SITEWHERE.equals(protocol)) {
			throw new IOException("Unhandled protocol (expected 'sitewhere'): " + protocol);
		}

		String host = getURL().getHost();
		if (!SUBJECT_RESOURCE.equals(host)) {
			throw new IOException("Unhandled host (expected 'resource'): " + host);
		}

		String path = getURL().getPath().substring(1);
		String[] parts = path.split("/");
		if (parts.length < 1) {
			throw new IOException("No resource path information included: " + getURL().toExternalForm());
		}
		if (TYPE_GLOBAL_RESOURCE.equals(parts[0])) {
			handleGlobalResource(parts);
		} else if (TYPE_TENANT_RESOURCE.equals(parts[0])) {
			handleGlobalResource(parts);
		}
	}

	/**
	 * Handle locating a global resource based on URL.
	 * 
	 * @param parts
	 * @throws IOException
	 */
	protected void handleGlobalResource(String[] parts) throws IOException {
		String path = buildPathFromParts(parts, 1);
		this.resource = SiteWhere.getServer().getResourceManager().getGlobalResource(path);
		if (resource == null) {
			throw new IOException("Unable to find global resource for path: " + path);
		}
	}

	/**
	 * Handle locating a tenant resource based on URL.
	 * 
	 * @param parts
	 * @throws IOException
	 */
	protected void handleTenantResource(String[] parts) throws IOException {
		String tenantId = parts[1];
		String path = buildPathFromParts(parts, 2);
		this.resource = SiteWhere.getServer().getResourceManager().getTenantResource(tenantId, path);
		if (resource == null) {
			throw new IOException("Unable to find tenant resource for path: " + path);
		}
	}

	/**
	 * Build a path by skipping some number of sections, then keeping the rest.
	 * 
	 * @param parts
	 * @param skip
	 * @return
	 */
	protected String buildPathFromParts(String[] parts, int skip) {
		String path = "";
		for (int i = skip; i < parts.length; i++) {
			path += parts[i] + File.separator;
		}
		return path.substring(0, path.length() - 1);
	}
}