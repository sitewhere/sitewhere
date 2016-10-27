package com.sitewhere.server.tenant;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.configuration.ResourceManagerGlobalConfigurationResolver;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.IResourceManager;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.server.tenant.ITenantTemplate;
import com.sitewhere.spi.server.tenant.ITenantTemplateManager;

/**
 * Manages templates that can be used to create new tenants.
 * 
 * @author Derek
 */
public class TenantTemplateManager extends LifecycleComponent implements ITenantTemplateManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

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
	templates.clear();

	IResourceManager resources = SiteWhere.getServer().getRuntimeResourceManager();
	List<IResource> all = resources.getGlobalResources();
	for (IResource resource : all) {
	    Path path = Paths.get(resource.getPath());
	    if (path.startsWith(ResourceManagerGlobalConfigurationResolver.TEMPLATES_FOLDER_NAME)) {
		if ((path.getNameCount() == 3)
			&& (path.endsWith(ResourceManagerGlobalConfigurationResolver.TEMPLATE_JSON_FILE_NAME))) {
		    TenantTemplate template = MarshalUtils.unmarshalJson(resource.getContent(), TenantTemplate.class);
		    templates.add(template);
		}
	    }
	}
	// Sort by template name.
	templates.sort(new Comparator<ITenantTemplate>() {

	    @Override
	    public int compare(ITenantTemplate o1, ITenantTemplate o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
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
}