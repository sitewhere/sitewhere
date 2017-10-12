package com.sitewhere.server.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.rest.model.resource.MultiResourceCreateResponse;
import com.sitewhere.rest.model.resource.Resource;
import com.sitewhere.rest.model.resource.ResourceCreateError;
import com.sitewhere.rest.model.resource.request.ResourceCreateRequest;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.configuration.IDefaultResourcePaths;
import com.sitewhere.spi.resource.IMultiResourceCreateResponse;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.IResourceManager;
import com.sitewhere.spi.resource.ResourceCreateFailReason;
import com.sitewhere.spi.resource.ResourceCreateMode;
import com.sitewhere.spi.resource.ResourceType;
import com.sitewhere.spi.resource.request.IResourceCreateRequest;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Implemenatation of {@link IResourceManager} that loads resources from the
 * file system.
 * 
 * @author Derek
 */
public class FileSystemResourceManager extends LifecycleComponent implements IResourceManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Root folder */
    private File rootFolder;

    /** Global resources folder */
    private File globalsFolder;

    /** Tenants folder */
    private File tenantsFolder;

    /** Templates folder */
    private File templatesFolder;

    public FileSystemResourceManager() {
	super(LifecycleComponentType.ResourceManager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (!rootFolder.exists()) {
	    throw new SiteWhereException("Root folder path specified for file system resource manager does not exist.");
	}
	if (!rootFolder.isDirectory()) {
	    throw new SiteWhereException("Root folder for file system resource manager is not a directory.");
	}
	globalsFolder = new File(getRootFolder(), IDefaultResourcePaths.GLOBAL_FOLDER_NAME);
	if (!(globalsFolder.exists() && (globalsFolder.isDirectory()))) {
	    throw new SiteWhereException("Global resources folder does not exist or is not a directory.");
	}
	tenantsFolder = new File(getRootFolder(), IDefaultResourcePaths.TENANTS_FOLDER_NAME);
	if (!(tenantsFolder.exists() && (tenantsFolder.isDirectory()))) {
	    throw new SiteWhereException("Tenant resources folder does not exist or is not a directory.");
	}
	templatesFolder = new File(getRootFolder(), IDefaultResourcePaths.TEMPLATES_FOLDER_NAME);
	if (!(templatesFolder.exists() && (templatesFolder.isDirectory()))) {
	    throw new SiteWhereException("Templates folder does not exist or is not a directory.");
	}
    }

    /**
     * Load a resource relative to a folder.
     * 
     * @param folder
     * @param relativePath
     * @return
     * @throws SiteWhereException
     */
    protected File getResourceFile(File folder, String relativePath) throws SiteWhereException {
	File resourceFile = new File(folder, relativePath);
	if (!resourceFile.exists()) {
	    return null;
	}
	return resourceFile;
    }

    /**
     * Create an {@link IResource} from a file that holds its content.
     * 
     * @param relativePath
     * @param file
     * @return
     * @throws SiteWhereException
     */
    protected IResource createResourceFromContent(String relativePath, File file) throws SiteWhereException {
	Resource resource = new Resource();
	resource.setPath(relativePath);
	resource.setResourceType(findResourceType(file));
	resource.setLastModified(System.currentTimeMillis());
	FileInputStream input = null;
	try {
	    input = new FileInputStream(file);
	    byte[] content = IOUtils.toByteArray(input);
	    resource.setContent(content);
	} catch (FileNotFoundException e) {
	    // Ignore. File deleted.
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to read resource file.", e);
	} finally {
	    IOUtils.closeQuietly(input);
	}
	return resource;
    }

    /**
     * Find resource type based on file characteristics.
     * 
     * @param file
     * @return
     */
    protected ResourceType findResourceType(File file) {
	String filename = file.getName();
	if (filename.equals(IDefaultResourcePaths.GLOBAL_CONFIG_FILE_NAME)) {
	    return ResourceType.ConfigurationFile;
	} else if (filename.endsWith(".groovy")) {
	    return ResourceType.GroovyScript;
	}
	return ResourceType.Unknown;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#createGlobalResources(java.
     * util.List, com.sitewhere.spi.resource.ResourceCreateMode)
     */
    @Override
    public IMultiResourceCreateResponse createGlobalResources(List<IResourceCreateRequest> requests,
	    ResourceCreateMode mode) throws SiteWhereException {
	MultiResourceCreateResponse response = new MultiResourceCreateResponse();
	for (IResourceCreateRequest request : requests) {
	    IResource resource = getGlobalResource(request.getPath());
	    if (resource == null) {
		writeResourceFile(request, null, response);
	    } else {
		handleResourceExists(request, null, response, mode);
	    }
	}
	return response;
    }

    /**
     * Write a file to the filesystem to persist a resource.
     * 
     * @param request
     * @param response
     */
    protected void writeResourceFile(IResourceCreateRequest request, String qualifier,
	    MultiResourceCreateResponse response) {
	String middle = (qualifier != null) ? (File.separator + qualifier + File.separator) : File.separator;
	File rfile = new File(getRootFolder().getAbsolutePath() + middle + request.getPath());
	Path parentPath = rfile.toPath().getParent();
	parentPath.toFile().mkdirs();

	FileOutputStream fileOut = null;
	try {
	    fileOut = new FileOutputStream(rfile);
	    IOUtils.write(request.getContent(), fileOut);
	    Resource created = new Resource();
	    created.setPath(request.getPath());
	    created.setResourceType(request.getResourceType());
	    created.setContent(request.getContent());
	    response.getCreatedResources().add(created);
	} catch (FileNotFoundException e) {
	    ResourceCreateError error = new ResourceCreateError();
	    error.setPath(request.getPath());
	    error.setReason(ResourceCreateFailReason.StorageFailure);
	    response.getErrors().add(error);
	    LOGGER.error("Error creating resource file: " + rfile.getAbsolutePath(), e);
	} catch (IOException e) {
	    ResourceCreateError error = new ResourceCreateError();
	    error.setPath(request.getPath());
	    error.setReason(ResourceCreateFailReason.StorageFailure);
	    response.getErrors().add(error);
	    LOGGER.error("Error writing resource file: " + rfile.getAbsolutePath(), e);
	} finally {
	    IOUtils.closeQuietly(fileOut);
	}
    }

    /**
     * Handle case where resource already exists.
     * 
     * @param request
     * @param qualifier
     * @param response
     * @param mode
     */
    protected void handleResourceExists(IResourceCreateRequest request, String qualifier,
	    MultiResourceCreateResponse response, ResourceCreateMode mode) {
	switch (mode) {
	case FAIL_IF_EXISTS: {
	    ResourceCreateError error = new ResourceCreateError();
	    error.setPath(request.getPath());
	    error.setReason(ResourceCreateFailReason.ResourceExists);
	    response.getErrors().add(error);
	    break;
	}
	case OVERWRITE: {
	    writeResourceFile(request, qualifier, response);
	    break;
	}
	case PUSH_NEW_VERSION: {
	    // TODO: Handle versioning here.
	    writeResourceFile(request, qualifier, response);
	    break;
	}
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#getGlobalResource(java.lang.
     * String)
     */
    @Override
    public IResource getGlobalResource(String path) throws SiteWhereException {
	File resFile = new File(getGlobalsFolder(), path);
	if (!resFile.exists()) {
	    resFile = new File(getRootFolder(), path);
	    if (!resFile.exists()) {
		return null;
	    }
	}
	return createResourceFromContent(path, resFile);
    }

    /**
     * Get all resources in a folder.
     * 
     * @param folder
     * @param recurse
     * @return
     * @throws SiteWhereException
     */
    protected List<IResource> getResourcesInFolder(File folder, boolean recurse) throws SiteWhereException {
	if (!folder.exists()) {
	    throw new SiteWhereException("Resource folder does not exist: " + folder.getAbsolutePath());
	}
	List<IResource> resources = new ArrayList<IResource>();
	Path folderPath = folder.toPath();
	Collection<File> all = FileUtils.listFiles(folder, null, recurse);
	for (File file : all) {
	    String relative = folderPath.relativize(file.toPath()).toString();
	    resources.add(createResourceFromContent(relative, file));
	}
	Collections.sort(resources, new Comparator<IResource>() {

	    @Override
	    public int compare(IResource r1, IResource r2) {
		return r1.getPath().compareTo(r2.getPath());
	    }
	});
	return resources;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.resource.IResourceManager#getGlobalResources()
     */
    @Override
    public List<IResource> getGlobalResources() throws SiteWhereException {
	List<IResource> globals = getResourcesInFolder(getGlobalsFolder(), true);
	List<IResource> roots = getResourcesInFolder(getRootFolder(), false);
	roots.addAll(globals);
	return roots;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.resource.IResourceManager#getTenantTemplateRoots()
     */
    @Override
    public List<String> getTenantTemplateRoots() throws SiteWhereException {
	String[] folders = getTemplatesFolder().list(DirectoryFileFilter.INSTANCE);
	List<String> roots = new ArrayList<String>();
	for (String folder : folders) {
	    roots.add(folder);
	}
	Collections.sort(roots, new Comparator<String>() {

	    @Override
	    public int compare(String s1, String s2) {
		return s1.compareTo(s2);
	    }
	});
	return roots;
    }

    /**
     * Get a template folder based on its id.
     * 
     * @param templateId
     * @return
     */
    protected File getTemplateFolder(String templateId) {
	return new File(getTemplatesFolder(), templateId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#getTenantTemplateResources(
     * java.lang.String)
     */
    @Override
    public List<IResource> getTenantTemplateResources(String templateId) throws SiteWhereException {
	return getResourcesInFolder(getTemplateFolder(templateId), true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#deleteGlobalResource(java.
     * lang.String)
     */
    @Override
    public IResource deleteGlobalResource(String path) throws SiteWhereException {
	File resourceFile = getResourceFile(getGlobalsFolder(), path);
	if (resourceFile != null) {
	    IResource resource = createResourceFromContent(path, resourceFile);
	    FileUtils.deleteQuietly(resourceFile);
	    return resource;
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#copyTemplateResourcesToTenant
     * (java.lang.String, java.lang.String,
     * com.sitewhere.spi.resource.ResourceCreateMode)
     */
    @Override
    public IMultiResourceCreateResponse copyTemplateResourcesToTenant(String templateId, String tenantId,
	    ResourceCreateMode mode) throws SiteWhereException {
	List<IResource> templateResources = getTenantTemplateResources(templateId);
	List<IResourceCreateRequest> requests = new ArrayList<IResourceCreateRequest>();
	for (IResource resource : templateResources) {
	    requests.add(new ResourceCreateRequest.Builder(resource).build());
	}
	return createTenantResources(tenantId, requests, mode);
    }

    /**
     * Get or create folder that contains tenant-specific data.
     * 
     * @param tenantId
     * @return
     * @throws SiteWhereException
     */
    protected File assureTenantFolder(String tenantId) throws SiteWhereException {
	File folder = new File(getTenantsFolder(), tenantId);
	if (!folder.exists()) {
	    folder.mkdirs();
	}
	return folder;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#createTenantResources(java.
     * lang.String, java.util.List,
     * com.sitewhere.spi.resource.ResourceCreateMode)
     */
    @Override
    public IMultiResourceCreateResponse createTenantResources(String tenantId, List<IResourceCreateRequest> requests,
	    ResourceCreateMode mode) throws SiteWhereException {
	assureTenantFolder(tenantId);
	MultiResourceCreateResponse response = new MultiResourceCreateResponse();
	for (IResourceCreateRequest request : requests) {
	    IResource resource = getTenantResource(tenantId, request.getPath());
	    String qualifier = IDefaultResourcePaths.TENANTS_FOLDER_NAME + File.separator + tenantId;
	    if (resource == null) {
		writeResourceFile(request, qualifier, response);
	    } else {
		handleResourceExists(request, qualifier, response, mode);
	    }
	}
	return response;
    }

    /**
     * Get a tenant folder based on its id.
     * 
     * @param templateId
     * @return
     */
    protected File getTenantFolder(String tenantId) {
	return new File(getTenantsFolder(), tenantId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#getTenantResource(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IResource getTenantResource(String tenantId, String path) throws SiteWhereException {
	File tenantFolder = getTenantFolder(tenantId);
	if (!tenantFolder.exists()) {
	    return null;
	}
	File resFile = new File(tenantFolder, path);
	if (!resFile.exists()) {
	    return null;
	}
	return createResourceFromContent(path, resFile);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#getTenantResources(java.lang.
     * String)
     */
    @Override
    public List<IResource> getTenantResources(String tenantId) throws SiteWhereException {
	return getResourcesInFolder(getTenantFolder(tenantId), true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#deleteTenantResource(java.
     * lang.String)
     */
    @Override
    public IResource deleteTenantResource(String tenantId, String path) throws SiteWhereException {
	File resourceFile = getResourceFile(getTenantFolder(tenantId), path);
	if (resourceFile != null) {
	    IResource resource = createResourceFromContent(path, resourceFile);
	    FileUtils.deleteQuietly(resourceFile);
	    return resource;
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.resource.IResourceManager#deleteTenantResources(java.
     * lang.String)
     */
    @Override
    public void deleteTenantResources(String tenantId) throws SiteWhereException {
	File tenant = assureTenantFolder(tenantId);
	if (tenant.exists()) {
	    try {
		// Delete underlying resources folder.
		FileUtils.deleteDirectory(tenant);
	    } catch (IOException e) {
		LOGGER.warn("Some resources for tenant '" + tenantId + "' could not be removed.");
	    }
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

    public File getRootFolder() {
	return rootFolder;
    }

    public void setRootFolder(File rootFolder) {
	this.rootFolder = rootFolder;
    }

    public File getGlobalsFolder() {
	return globalsFolder;
    }

    public void setGlobalsFolder(File globalsFolder) {
	this.globalsFolder = globalsFolder;
    }

    public File getTenantsFolder() {
	return tenantsFolder;
    }

    public void setTenantsFolder(File tenantsFolder) {
	this.tenantsFolder = tenantsFolder;
    }

    public File getTemplatesFolder() {
	return templatesFolder;
    }

    public void setTemplatesFolder(File templatesFolder) {
	this.templatesFolder = templatesFolder;
    }
}