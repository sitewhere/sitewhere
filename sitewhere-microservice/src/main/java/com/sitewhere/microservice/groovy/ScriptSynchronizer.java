package com.sitewhere.microservice.groovy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.microservice.spi.groovy.IScriptSynchronizer;
import com.sitewhere.spi.SiteWhereException;

/**
 * Base class for script synchronizers.
 * 
 * @author Derek
 */
public abstract class ScriptSynchronizer implements IScriptSynchronizer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice reference */
    private IMicroservice microsevice;

    public ScriptSynchronizer(IMicroservice microservice) {
	this.microsevice = microservice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#add(java.lang.
     * String)
     */
    @Override
    public void add(String zkPath) throws SiteWhereException {
	copy(zkPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#update(java.
     * lang.String)
     */
    @Override
    public void update(String zkPath) throws SiteWhereException {
	copy(zkPath);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#delete(java.
     * lang.String)
     */
    @Override
    public void delete(String zkPath) throws SiteWhereException {
	File existing = getFileFor(zkPath);
	if (existing.exists()) {
	    try {
		Files.delete(existing.toPath());
		LOGGER.info("Deleted script at path '" + zkPath + "'.");
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to delete script from filesystem.", e);
	    }
	}
    }

    /**
     * Copy Zookeeper content to filesystem.
     * 
     * @param zkPath
     * @throws SiteWhereException
     */
    protected void copy(String zkPath) throws SiteWhereException {
	byte[] content = getZkContent(zkPath);
	FileOutputStream output = null;
	try {
	    File out = getFileFor(zkPath);
	    if (!out.getParentFile().exists()) {
		out.getParentFile().mkdirs();
	    }
	    output = new FileOutputStream(out);
	    ByteArrayInputStream input = new ByteArrayInputStream(content);
	    IOUtils.copy(input, output);
	    LOGGER.info("Copied script from '" + zkPath + "' to '" + out.getAbsolutePath() + "'.");
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to copy script from Zookeeper to filesystem.", e);
	} finally {
	    IOUtils.closeQuietly(output);
	}
    }

    /**
     * Get Zookeeper content from the given path.
     * 
     * @param zkPath
     * @return
     * @throws SiteWhereException
     */
    protected byte[] getZkContent(String zkPath) throws SiteWhereException {
	try {
	    return getMicrosevice().getZookeeperManager().getCurator().getData().forPath(zkPath);
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to get Zookeeper content for path '" + zkPath + "'.");
	}
    }

    /**
     * Get the file (relative to filesystem root) that corresponds to the
     * Zookeeper path.
     * 
     * @param zkPath
     * @return
     * @throws SiteWhereException
     */
    protected File getFileFor(String zkPath) throws SiteWhereException {
	try {
	    Path root = Paths.get(getZkScriptRootPath());
	    Path relative = root.relativize(Paths.get(zkPath));
	    Path fsRoot = getFileSystemRoot().toPath();
	    return fsRoot.resolve(relative).toFile();
	} catch (Throwable e) {
	    throw new SiteWhereException("Unable to get Zookeeper script content.", e);
	}
    }

    public IMicroservice getMicrosevice() {
	return microsevice;
    }

    public void setMicrosevice(IMicroservice microsevice) {
	this.microsevice = microsevice;
    }
}