package com.sitewhere.rest.model.resource.request;

import com.sitewhere.spi.resource.ResourceType;
import com.sitewhere.spi.resource.request.IResourceCreateRequest;

/**
 * Model implementation for {@link IResourceCreateRequest}.
 * 
 * @author Derek
 */
public class ResourceCreateRequest implements IResourceCreateRequest {

	/** Resource path */
	private String path;

	/** Resource type */
	private ResourceType resourceType;

	/** Resource content */
	private byte[] content;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.resource.request.IResourceCreateRequest#getPath()
	 */
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.resource.request.IResourceCreateRequest#getResourceType
	 * ()
	 */
	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.resource.request.IResourceCreateRequest#getContent()
	 */
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}