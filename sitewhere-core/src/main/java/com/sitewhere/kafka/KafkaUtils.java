package com.sitewhere.kafka;

import java.io.File;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Utility functions used by Kafka components.
 * 
 * @author Derek
 */
public class KafkaUtils {

    /**
     * Get folder relative to SiteWhere home.
     * 
     * @param relative
     * @return
     * @throws SiteWhereException
     */
    public static File createRelativeFolder(String relative) throws SiteWhereException {
	File file = new File(SiteWhereServer.getSiteWhereHomeFolder(), relative);
	if (file.exists()) {
	    return file;
	}
	if (!file.mkdirs()) {
	    throw new RuntimeException("Unable to create folder: " + file.getAbsolutePath());
	}
	return file;
    }
}
