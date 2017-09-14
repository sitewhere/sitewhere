package com.sitewhere.instance.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.instance.spi.templates.IInstanceTemplate;
import com.sitewhere.instance.spi.templates.IInstanceTemplateManager;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Manages a list of available templates used to bootstrap a SiteWhere instance.
 * 
 * @author Derek
 */
public class InstanceTemplateManager extends LifecycleComponent implements IInstanceTemplateManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Root folder for instance templates */
    private static final String TEMPLATES_ROOT = "/opt/templates";

    /** File name for instance template metadata */
    private static final String TEMPLATE_FILE = "template.json";

    /** List of templates */
    private Map<String, IInstanceTemplate> templates = new HashMap<String, IInstanceTemplate>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	refreshTemplates(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.instance.spi.templates.IInstanceTemplateManager#
     * getInstanceTemplates()
     */
    @Override
    public Map<String, IInstanceTemplate> getInstanceTemplates() throws SiteWhereException {
	return templates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.instance.spi.templates.IInstanceTemplateManager#
     * copyTemplateConfigurationToZk(java.lang.String,
     * org.apache.curator.framework.CuratorFramework, java.lang.String)
     */
    @Override
    public void copyTemplateConfigurationToZk(String templateId, CuratorFramework curator, String confPath)
	    throws SiteWhereException {
	IInstanceTemplate template = getInstanceTemplates().get(templateId);
	if (template == null) {
	    throw new SiteWhereException("Instance template not found: " + templateId);
	}
	File root = getTemplatesRoot();
	File templateFolder = new File(root, templateId);
	if (!templateFolder.exists()) {
	    throw new SiteWhereException("Template folder not found at '" + templateFolder.getAbsolutePath() + "'.");
	}
	File confFolder = new File(templateFolder, "conf");
	if (!confFolder.exists()) {
	    throw new SiteWhereException(
		    "Template configuration folder not found at '" + confFolder.getAbsolutePath() + "'.");
	}
	File[] contents = confFolder.listFiles();
	for (File file : contents) {
	    if (!file.isDirectory()) {
		copyTemplateFileToZk(curator, file, confPath);
	    }
	}
    }

    /**
     * Copy a single template file to Zookeeper.
     * 
     * @param curator
     * @param templateFile
     * @param confPath
     * @throws SiteWhereException
     */
    protected void copyTemplateFileToZk(CuratorFramework curator, File templateFile, String confPath)
	    throws SiteWhereException {
	String zkFile = confPath + "/" + templateFile.getName();
	FileInputStream input = null;
	try {
	    input = new FileInputStream(templateFile);
	    byte[] data = IOUtils.toByteArray(input);
	    curator.create().forPath(zkFile, data);
	} catch (Exception e) {
	    LOGGER.error("Unable to copy template file to Zk.", e);
	    IOUtils.closeQuietly(input);
	}
    }

    /**
     * Get file handle for instance templates root.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected File getTemplatesRoot() throws SiteWhereException {
	File root = new File(TEMPLATES_ROOT);
	if (!root.exists()) {
	    throw new SiteWhereException("Root folder for instance templates not found!");
	}
	return root;
    }

    /**
     * Refresh the list of templates.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void refreshTemplates(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	Map<String, IInstanceTemplate> updated = new HashMap<String, IInstanceTemplate>();
	File root = getTemplatesRoot();
	File[] folders = root.listFiles(File::isDirectory);
	for (File folder : folders) {
	    IInstanceTemplate template = createTemplate(folder);
	    if (template != null) {
		updated.put(template.getId(), template);
	    }
	}
	synchronized (templates) {
	    templates.clear();
	    templates.putAll(updated);
	}
	LOGGER.info("Loaded " + templates.size() + " instance templates:");
	for (String key : templates.keySet()) {
	    IInstanceTemplate template = templates.get(key);
	    LOGGER.info("- " + template.getName() + " (" + template.getId() + ")");
	}
    }

    /**
     * Creates an instance template based on folder contents.
     * 
     * @param folder
     * @return
     * @throws SiteWhereException
     */
    protected IInstanceTemplate createTemplate(File folder) throws SiteWhereException {
	File templateFile = new File(folder, TEMPLATE_FILE);
	if (!templateFile.exists()) {
	    LOGGER.warn("Template file not found for folder '" + folder.getAbsolutePath() + "'. Skipping.");
	    return null;
	}
	FileInputStream input = null;
	try {
	    input = new FileInputStream(templateFile);
	    byte[] content = IOUtils.toByteArray(input);
	    return MarshalUtils.unmarshalJson(content, InstanceTemplate.class);
	} catch (IOException e) {
	    LOGGER.error("Unable to read instance template file.", e);
	    IOUtils.closeQuietly(input);
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}