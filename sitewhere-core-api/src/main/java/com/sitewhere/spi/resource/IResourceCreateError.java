package com.sitewhere.spi.resource;

/**
 * Indicates error creating resource.
 * 
 * @author Derek
 */
public interface IResourceCreateError {

	/**
	 * Get path being created.
	 * 
	 * @return
	 */
	public String getPath();

	/**
	 * Get reason for failure.
	 * 
	 * @return
	 */
	public ResourceCreateFailReason getReason();
}