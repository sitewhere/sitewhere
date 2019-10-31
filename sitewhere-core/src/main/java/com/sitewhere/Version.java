/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.sitewhere.spi.system.IVersion;

/**
 * Gets information about system version from properties generated into the
 * classpath.
 */
public class Version implements IVersion {

    /** Serial version UID */
    private static final long serialVersionUID = 6248127738815445097L;

    /** Loaded from classpath to get version information */
    private static Properties properties = new Properties();

    static {
	try (final InputStream stream = Version.class.getClassLoader()
		.getResourceAsStream("META-INF/application-build.properties")) {
	    properties.load(stream);
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.system.IVersion#getEdition()
     */
    public String getEdition() {
	return "Community Edition";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.system.IVersion#getEditionIdentifier()
     */
    public String getEditionIdentifier() {
	return "CE";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.system.IVersion#getVersionIdentifier()
     */
    public String getVersionIdentifier() {
	return properties.getProperty("sitewhere.version");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.system.IVersion#getBuildTimestamp()
     */
    public String getBuildTimestamp() {
	return properties.getProperty("build.timestamp");
    }

    /*
     * @see com.sitewhere.spi.system.IVersion#getGitRevision()
     */
    @Override
    public String getGitRevision() {
	return properties.getProperty("git.revision");
    }

    /*
     * @see com.sitewhere.spi.system.IVersion#getGitRevisionAbbrev()
     */
    @Override
    public String getGitRevisionAbbrev() {
	return properties.getProperty("git.revision.abbrev");
    }
}