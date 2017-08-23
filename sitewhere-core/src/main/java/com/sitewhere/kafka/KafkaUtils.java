package com.sitewhere.kafka;

import java.io.File;
import java.util.Random;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Utility functions used by Kafka components.
 * 
 * @author Derek
 */
public class KafkaUtils {

    /** Used for generating temp file names */
    private static final Random RANDOM = new Random();

    /**
     * Get folder relative to SiteWhere home.
     * 
     * @param dirPrefix
     * @return
     * @throws SiteWhereException
     */
    public static File createRelativeFolder(String dirPrefix) throws SiteWhereException {
	File file = new File(SiteWhereServer.getSiteWhereHomeFolder(), dirPrefix + RANDOM.nextInt(10000000));
	if (!file.mkdirs()) {
	    throw new RuntimeException("Unable to create folder: " + file.getAbsolutePath());
	}
	file.deleteOnExit();
	return file;
    }
}
