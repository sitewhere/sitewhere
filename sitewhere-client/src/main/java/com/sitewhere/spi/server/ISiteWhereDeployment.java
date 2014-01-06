/*
* $Id$
* --------------------------------------------------------------------------------------
* Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
*
* The software in this package is published under the terms of the CPAL v1.0
* license, a copy of which has been included with this distribution in the
* LICENSE.txt file.
*/

package com.sitewhere.spi.server;

import java.io.File;
import java.util.Calendar;

import javax.xml.bind.JAXBContext;

import com.sitewhere.spi.SiteWhereException;

/**
 * Information about an Atlas deployment.
 * 
 * @author Derek
 */
public interface ISiteWhereDeployment {

	/**
	 * Get the configuration UUID.
	 * 
	 * @return
	 */
	public String getConfigurationUuid();

	/**
	 * Get the configuration name.
	 * 
	 * @return
	 */
	public String getConfigurationName();

	/**
	 * Get a description of the current deployment.
	 * 
	 * @return
	 */
	public String getConfigurationDescription();

	/**
	 * Get the root folder for the deployment.
	 * 
	 * @return
	 */
	public File getDeploymentRoot();

	/**
	 * Get the date/time on which the deployment was started.
	 * 
	 * @return
	 */
	public Calendar getDeployedDate();

	/**
	 * Indicates the time of last redeploy.
	 * 
	 * @return
	 */
	public Long getLastRedeployTime();

	/**
	 * Get JAXB context for the given class.
	 * 
	 * @param clazz
	 * @return
	 * @throws SiteWhereException
	 */
	public JAXBContext getJaxbContext(Class<?> clazz) throws SiteWhereException;

	/**
	 * Locates a configuration bean of the given type.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 * @throws SiteWhereException
	 */
	public <T> T getConfigurationBean(Class<T> type) throws SiteWhereException;
}