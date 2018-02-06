/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.zookeeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
			if (curator.checkExists().forPath(subFile) == null) {
			    curator.create().creatingParentsIfNeeded().forPath(subFile, data);
			} else {
			    curator.setData().forPath(subFile, data);
			}
			LOGGER.debug("Created file for '" + file.getAbsolutePath() + "' in '" + subFile + "'.");
		    } catch (Exception e) {
			throw new SiteWhereException(
				"Unable to copy file '" + file.getAbsolutePath() + "' into '" + subFile + "'.", e);
		    }
		} catch (IOException e) {
		    LOGGER.error("Error copying tenant template file to Zk: " + file.getAbsolutePath(), e);
		    IOUtils.closeQuietly(input);
		}
	    }
	}
    }

    /**
     * Copies content tree recursively from Zk to local filesystem.
     * 
     * @param curator
     * @param zkRoot
     * @param root
     * @param current
     * @throws SiteWhereException
     */
    public static void copyFolderRecursivelyFromZk(CuratorFramework curator, String zkRoot, File root, String current)
	    throws SiteWhereException {
	String[] zkSegments = current.split("/");
	String lastZkSegment = (zkSegments.length > 0) ? zkSegments[zkSegments.length - 1] : null;
	if (lastZkSegment != null) {
	    // Find relative path and corresponding file.
	    String relative = current.substring(zkRoot.length());
	    File local = new File(root, relative);

	    // Make sure parent folders exist.
	    if (!local.getParentFile().exists()) {
		local.getParentFile().mkdirs();
	    }

	    // Assume path is a file.
	    if (lastZkSegment.indexOf('.') > -1) {
		FileOutputStream output = null;
		try {
		    output = new FileOutputStream(local);
		    byte[] content = curator.getData().forPath(current);
		    IOUtils.write(content, output);
		    LOGGER.debug("Wrote file for '" + local.getAbsolutePath() + "' from '" + current + "'.");
		} catch (Exception e) {
		    throw new SiteWhereException(
			    "Unable to copy file '" + local.getAbsolutePath() + "' from '" + current + "'.", e);
		} finally {
		    IOUtils.closeQuietly(output);
		}
	    }

	    // Assume path is a folder.
	    else {
		try {
		    List<String> children = curator.getChildren().forPath(current);
		    for (String child : children) {
			ZkUtils.copyFolderRecursivelyFromZk(curator, zkRoot, root, current + "/" + child);
		    }
		} catch (Exception e) {
		    throw new SiteWhereException("Unable to get children for folder '" + local.getAbsolutePath()
			    + "' from '" + current + "'.", e);
		}
	    }
	}
    }
}
