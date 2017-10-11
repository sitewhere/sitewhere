package com.sitewhere.tenant.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IDefaultResourcePaths;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;
import com.sitewhere.tenant.spi.templates.ITenantTemplate;
import com.sitewhere.tenant.spi.templates.ITenantTemplateManager;

/**
 * Manages templates that can be used to create new tenants.
 * 
 * @author Derek
 */
public class TenantTemplateManager extends LifecycleComponent implements ITenantTemplateManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected handle to microservice */
    @Autowired
    private ITenantManagementMicroservice microservice;

    /** List of templates */
    private List<ITenantTemplate> templates = new ArrayList<ITenantTemplate>();

    public TenantTemplateManager() {
	super(LifecycleComponentType.TenantTemplateManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	List<ITenantTemplate> updated = new ArrayList<ITenantTemplate>();

	// Loop through tenant folders and pull templates.
	File root = getMicroservice().getTenantTemplatesRoot();
	File[] folders = root.listFiles(File::isDirectory);
	for (File folder : folders) {
	    File tfile = new File(folder, IDefaultResourcePaths.TEMPLATE_JSON_FILE_NAME);
	    if (tfile.exists()) {
		InputStream input;
		try {
		    input = new FileInputStream(tfile);
		    byte[] content = IOUtils.toByteArray(input);
		    TenantTemplate template = MarshalUtils.unmarshalJson(content, TenantTemplate.class);
		    updated.add(template);
		} catch (IOException e) {
		    LOGGER.warn("Unable to unmarshal template.", e);
		}
	    }
	}
	synchronized (templates) {
	    templates.clear();
	    templates.addAll(updated);

	    // Sort by template name.
	    templates.sort(new Comparator<ITenantTemplate>() {

		@Override
		public int compare(ITenantTemplate o1, ITenantTemplate o2) {
		    return o1.getName().compareTo(o2.getName());
		}
	    });
	}

	LOGGER.info("Template manager found the following templates:");
	for (ITenantTemplate template : templates) {
	    LOGGER.info("[" + template.getId() + "] " + template.getName());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.tenant.ITenantTemplateManager#getTenantTemplates
     * ()
     */
    @Override
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException {
	return templates;
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

    public ITenantManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(ITenantManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}