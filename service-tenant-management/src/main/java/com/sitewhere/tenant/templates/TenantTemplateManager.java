package com.sitewhere.tenant.templates;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IDefaultResourcePaths;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.IResourceManager;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
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

    /** Resource manager implementation */
    private IResourceManager resourceManager;

    /** List of templates */
    private List<ITenantTemplate> templates = new ArrayList<ITenantTemplate>();

    public TenantTemplateManager(IResourceManager resourceManager) {
	super(LifecycleComponentType.TenantTemplateManager);
	this.resourceManager = resourceManager;
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

	List<String> roots = getResourceManager().getTenantTemplateRoots();
	for (String root : roots) {
	    List<IResource> all = getResourceManager().getTenantTemplateResources(root);
	    for (IResource resource : all) {
		Path path = Paths.get(resource.getPath());
		if ((path.getNameCount() == 1) && (path.startsWith(IDefaultResourcePaths.TEMPLATE_JSON_FILE_NAME))) {
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

    public IResourceManager getResourceManager() {
	return resourceManager;
    }

    public void setResourceManager(IResourceManager resourceManager) {
	this.resourceManager = resourceManager;
    }
}