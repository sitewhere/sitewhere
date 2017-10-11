package com.sitewhere.microservice.zookeeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.spi.SiteWhereException;

/**
 * Utility methods for Zookeeper operations.
 * 
 * @author Derek
 */
public class ZkUtils {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Copy a folder recursively into Zk.
     * 
     * @param curator
     * @param zkRoot
     * @param root
     * @param current
     * @throws Exception
     */
    public static void copyFolderRecursivelytoZk(CuratorFramework curator, String zkRoot, File root, File current)
	    throws SiteWhereException {
	String relative = root.toPath().relativize(current.toPath()).toString();
	String folderPath = zkRoot + ((relative.length() > 0) ? "/" + relative : "");
	File[] contents = current.listFiles();
	for (File file : contents) {
	    String subFile = folderPath + "/" + file.getName();
	    if (file.isDirectory()) {
		try {
		    curator.create().forPath(subFile);
		    LOGGER.debug("Created folder for '" + file.getAbsolutePath() + "' in '" + subFile + "'.");
		} catch (Exception e) {
		    throw new SiteWhereException(
			    "Unable to copy folder '" + file.getAbsolutePath() + "' into '" + subFile + "'.", e);
		}
		ZkUtils.copyFolderRecursivelytoZk(curator, zkRoot, root, file);
	    } else if (file.isFile()) {
		FileInputStream input = null;
		try {
		    input = new FileInputStream(file);
		    byte[] data = IOUtils.toByteArray(input);
		    try {
			curator.create().forPath(subFile, data);
			LOGGER.debug("Created file for '" + file.getAbsolutePath() + "' in '" + subFile + "'.");
		    } catch (Exception e) {
			throw new SiteWhereException(
				"Unable to copy file '" + file.getAbsolutePath() + "' into '" + subFile + "'.", e);
		    }
		} catch (IOException e) {
		    IOUtils.closeQuietly(input);
		}
	    }
	}
    }
}
