package com.sitewhere.microservice.groovy;

import java.io.File;

import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.microservice.spi.groovy.IScriptSynchronizer;

/**
 * Implementation of {@link IScriptSynchronizer} that copies instance-level
 * scripts from Zookeeper to the local filesystem of a microservice.
 * 
 * @author Derek
 */
public class InstanceScriptSynchronizer extends ScriptSynchronizer {

    /** File system root */
    private File fileSystemRoot;

    /** Zookeeper root path */
    private String zkScriptRootPath;

    public InstanceScriptSynchronizer(IMicroservice microservice) {
	super(microservice);
	setFileSystemRoot(new File(getMicrosevice().getInstanceSettings().getFileSystemStorageRoot()));
	setZkScriptRootPath(getMicrosevice().getInstanceZkPath());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#
     * getFileSystemRoot()
     */
    @Override
    public File getFileSystemRoot() {
	return fileSystemRoot;
    }

    public void setFileSystemRoot(File fileSystemRoot) {
	this.fileSystemRoot = fileSystemRoot;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.groovy.IScriptSynchronizer#
     * getZkScriptRootPath()
     */
    @Override
    public String getZkScriptRootPath() {
	return zkScriptRootPath;
    }

    public void setZkScriptRootPath(String zkScriptRootPath) {
	this.zkScriptRootPath = zkScriptRootPath;
    }
}