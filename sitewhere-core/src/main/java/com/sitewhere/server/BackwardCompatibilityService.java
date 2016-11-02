package com.sitewhere.server;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.server.resource.SiteWhereHomeResourceManager;
import com.sitewhere.server.tenant.TenantTemplate;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IDefaultResourcePaths;
import com.sitewhere.spi.server.IBackwardCompatibilityService;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Helps with migration of previous server versions to provide compatibility for
 * users that drop the new WAR into an existing server instance.
 * 
 * @author Derek
 */
public class BackwardCompatibilityService implements IBackwardCompatibilityService {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.IBackwardCompatibilityService#
     * beforeServerInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void beforeServerInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	migrateOldTemplateToNew(monitor);
    }

    /**
     * Migrate the old template format to the new one. Note that no data
     * initializers will be fired.
     * 
     * @param monitor
     * @throws SiteWhereException
     */
    protected void migrateOldTemplateToNew(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	File home = SiteWhereHomeResourceManager.calculateConfigurationPath();
	File oldTemplate = new File(home, "tenant-template");
	if (oldTemplate.exists()) {
	    File templates = new File(home, IDefaultResourcePaths.TEMPLATES_FOLDER_NAME);
	    if (!templates.exists()) {
		if (!templates.mkdirs()) {
		    throw new SiteWhereException("Unable to create new templates folder to transition old data.");
		}
	    }
	    File compatibility = new File(templates, "compatibility");
	    if (!oldTemplate.renameTo(compatibility)) {
		throw new SiteWhereException("Unable to rename old templates folder to new format.");
	    }
	    TenantTemplate template = new TenantTemplate();
	    template.setId("compatibility");
	    template.setName("Pre-1.9.0 SiteWhere Template");
	    String content = MarshalUtils.marshalJsonAsPrettyString(template);
	    File templateFile = new File(compatibility, IDefaultResourcePaths.TEMPLATE_JSON_FILE_NAME);
	    try {
		FileUtils.writeStringToFile(templateFile, content, false);
	    } catch (IOException e) {
		throw new SiteWhereException("Unable to write template file.");
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.IBackwardCompatibilityService#beforeServerStart(
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void beforeServerStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.IBackwardCompatibilityService#afterServerStart(
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void afterServerStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }
}