package com.sitewhere.server.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.configuration.ResourceManagerGlobalConfigurationResolver;
import com.sitewhere.configuration.ResourceManagerTenantConfigurationResolver;
import com.sitewhere.rest.model.resource.MultiResourceCreateResponse;
import com.sitewhere.rest.model.resource.Resource;
import com.sitewhere.rest.model.resource.ResourceCreateError;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.resource.IMultiResourceCreateResponse;
import com.sitewhere.spi.resource.IResource;
import com.sitewhere.spi.resource.IResourceManager;
import com.sitewhere.spi.resource.ResourceCreateFailReason;
import com.sitewhere.spi.resource.ResourceCreateMode;
import com.sitewhere.spi.resource.ResourceType;
import com.sitewhere.spi.resource.request.IResourceCreateRequest;
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

	/** Folder name for global resources */
	private static final String GLOBAL_FOLDER_NAME = "global";

	/** Folder containing tenant resources */
	private static final String TENANTS_FOLDER_NAME = "tenants";

	/** Filename for active tenant configuration file */
	private static final String TENANT_CONFIG_FILENAME = ResourceManagerTenantConfigurationResolver.DEFAULT_TENANT_CONFIGURATION_FILE
			+ "." + ResourceManagerTenantConfigurationResolver.TENANT_SUFFIX_ACTIVE;

	/** Filename for staged tenant configuration file */
	private static final String TENANT_STAGED_FILENAME = ResourceManagerTenantConfigurationResolver.DEFAULT_TENANT_CONFIGURATION_FILE
			+ "." + ResourceManagerTenantConfigurationResolver.TENANT_SUFFIX_STAGED;

	/** Root folder */
	private File rootFolder;

	/** Resources being managed */
	private ResourceMap globalResourceMap = new ResourceMap();

	/** Tenant resource maps by tenant id */
	private Map<String, ResourceMap> tenantResourceMaps = new HashMap<String, ResourceMap>();

	/** Executor for file watcher */
	private ExecutorService executor;

	public FileSystemResourceManager() {
		super(LifecycleComponentType.ResourceManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#start()
	 */
	@Override
	public void start() throws SiteWhereException {
		getGlobalResourceMap().clear();
		getTenantResourceMaps().clear();

		if (!rootFolder.exists()) {
			throw new SiteWhereException("Root folder path specified for file system resource manager does not exist.");
		}
		if (!rootFolder.isDirectory()) {
			throw new SiteWhereException("Root folder for file system resource manager is not a directory.");
		}
		cacheConfigurationResources();

		// Start watching for filesystem changes in background.
		executor = Executors.newSingleThreadExecutor();
		executor.execute(new FileSystemWatcher());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
		if (executor != null) {
			executor.shutdownNow();
		}
	}

	/**
	 * Cache top-level configuration resources as well as all resources in the
	 * 'global' folder and each tenant folder.
	 * 
	 * @throws SiteWhereException
	 */
	protected void cacheConfigurationResources() throws SiteWhereException {
		if (!(getRootFolder().exists() && (getRootFolder().isDirectory()))) {
			throw new SiteWhereException("Configuration folder does not exist.");
		}
		cacheFileResources(getRootFolder());

		// Cache contents of "global" folder.
		File globals = new File(getRootFolder(), GLOBAL_FOLDER_NAME);
		if (!(globals.exists() && (globals.isDirectory()))) {
			throw new SiteWhereException("Globals resources folder does not exist.");
		}
		cacheFolderResources(globals);

		// Cache contents of "tenants" folder.
		File tenants = new File(getRootFolder(), TENANTS_FOLDER_NAME);
		if (!(tenants.exists() && (tenants.isDirectory()))) {
			throw new SiteWhereException("Tenant resources folder does not exist.");
		}
		File[] tenantFolders = tenants.listFiles();
		for (File tenant : tenantFolders) {
			if (tenant.isDirectory()) {
				cacheFolderResources(tenant);
			}
		}
	}

	/**
	 * Cache only file resources in a folder.
	 * 
	 * @param folder
	 */
	protected void cacheFileResources(File folder) {
		File[] files = folder.listFiles();
		for (File file : files) {
			if (!file.isDirectory()) {
				try {
					cacheFile(file);
				} catch (SiteWhereException e) {
					LOGGER.warn("Unable to cache file resource.", e);
				}
			}
		}
	}

	/**
	 * Cache all files in a folder as resources.
	 * 
	 * @param folder
	 */
	protected void cacheFolderResources(File folder) {
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				cacheFolderResources(file);
			} else {
				try {
					cacheFile(file);
				} catch (SiteWhereException e) {
					LOGGER.warn("Unable to cache file resource.", e);
				}
			}
		}
	}

	/**
	 * Cache a single file as a resource.
	 * 
	 * @param file
	 * @throws SiteWhereException
	 */
	protected void cacheFile(File file) throws SiteWhereException {
		Path path = file.toPath();
		Path relative = getRootFolder().toPath().relativize(path);
		if (relative.startsWith(TENANTS_FOLDER_NAME)) {
			Path tenantsRelative = relative.subpath(1, relative.getNameCount());
			if (tenantsRelative.getNameCount() > 0) {
				String tenantId = tenantsRelative.getName(0).toString();
				Path tenantRelative = tenantsRelative.subpath(1, tenantsRelative.getNameCount());
				if (tenantRelative.getNameCount() > 0) {
					cacheTenantFile(tenantId, tenantRelative.toString(), file);
				}
			}
		} else {
			cacheGlobalFile(relative.toString(), file);
		}
	}

	/**
	 * Cache a global file resource.
	 * 
	 * @param relativePath
	 * @param file
	 * @throws SiteWhereException
	 */
	protected void cacheGlobalFile(String relativePath, File file) throws SiteWhereException {
		IResource resource = createResourceFromFile(relativePath, file);
		getGlobalResourceMap().put(relativePath, resource);
		LOGGER.info("Cached resource: " + resource.getPath() + " (" + resource.getResourceType().name() + ") "
				+ resource.getContent().length + " bytes");
	}

	/**
	 * Cache a tenant file resource.
	 * 
	 * @param tenantId
	 * @param relativePath
	 * @param file
	 * @throws SiteWhereException
	 */
	protected void cacheTenantFile(String tenantId, String relativePath, File file) throws SiteWhereException {
		ResourceMap tenant = getTenantResourceMaps().get(tenantId);
		if (tenant == null) {
			tenant = new ResourceMap();
			getTenantResourceMaps().put(tenantId, tenant);
		}
		IResource resource = createResourceFromFile(relativePath, file);
		tenant.put(relativePath, resource);
		LOGGER.info("Cached resource: " + resource.getPath() + " (" + resource.getResourceType().name() + ") "
				+ resource.getContent().length + " bytes");
	}

	/**
	 * Create an {@link IResource} from a file.
	 * 
	 * @param relativePath
	 * @param file
	 * @return
	 * @throws SiteWhereException
	 */
	protected IResource createResourceFromFile(String relativePath, File file) throws SiteWhereException {
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
			throw new SiteWhereException("Resource file not found.", e);
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
		if (filename.equals(ResourceManagerGlobalConfigurationResolver.GLOBAL_CONFIG_FILE_NAME)) {
			return ResourceType.ConfigurationFile;
		} else if (filename.equals(TENANT_CONFIG_FILENAME)) {
			return ResourceType.ConfigurationFile;
		} else if (filename.equals(TENANT_STAGED_FILENAME)) {
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
		rfile.toPath().getParent().toFile().mkdirs();
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
		return getGlobalResourceMap().get(path);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.resource.IResourceManager#getGlobalResources()
	 */
	@Override
	public List<IResource> getGlobalResources() throws SiteWhereException {
		List<IResource> results = new ArrayList<IResource>();
		for (String path : getGlobalResourceMap().keySet()) {
			results.add(getGlobalResourceMap().get(path));
		}
		Collections.sort(results, new Comparator<IResource>() {

			@Override
			public int compare(IResource r1, IResource r2) {
				return r1.getPath().compareTo(r2.getPath());
			}
		});
		return results;
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
		IResource deleted = deleteResource(null, getGlobalResource(path));
		globalResourceMap.remove(path);
		return deleted;
	}

	/**
	 * Delete the given resource.
	 * 
	 * @param qualifier
	 * @param resource
	 * @return
	 * @throws SiteWhereException
	 */
	protected IResource deleteResource(String qualifier, IResource resource) throws SiteWhereException {
		if (resource == null) {
			return null;
		}
		String middle = (qualifier != null) ? (File.separator + qualifier + File.separator) : "";
		File rfile = new File(getRootFolder().getAbsolutePath() + middle + resource.getPath());
		if (!rfile.exists()) {
			return null;
		}
		if (!rfile.delete()) {
			throw new SiteWhereException("Unable to delete resource.");
		}
		uncacheFile(rfile);
		LOGGER.info("Deleted and uncached resource: " + rfile.getAbsolutePath());
		return resource;
	}

	/**
	 * Remove a managed resource based on file information.
	 * 
	 * @param file
	 * @throws SiteWhereException
	 */
	protected void uncacheFile(File file) throws SiteWhereException {
		Path path = file.toPath();
		Path relative = getRootFolder().toPath().relativize(path);
		if (relative.startsWith(TENANTS_FOLDER_NAME)) {
			Path tenantsRelative = relative.subpath(1, relative.getNameCount());
			if (tenantsRelative.getNameCount() > 0) {
				String tenantId = tenantsRelative.getName(0).toString();
				Path tenantRelative = tenantsRelative.subpath(1, tenantsRelative.getNameCount());
				if (tenantRelative.getNameCount() > 0) {
					ResourceMap map = tenantResourceMaps.get(tenantId);
					if (map != null) {
						map.remove(tenantRelative.toString());
					}
				}
			}
		} else {
			globalResourceMap.remove(relative.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.resource.IResourceManager#copyGlobalResourcesToTenant(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void copyGlobalResourcesToTenant(String prefix, String tenantId) throws SiteWhereException {
		File source = new File(getRootFolder().getAbsolutePath() + File.separator + prefix);
		if (!source.exists()) {
			throw new SiteWhereException("No source folder found for: " + source.getAbsolutePath());
		}
		File target = assureTenantFolder(tenantId);
		try {
			FileUtils.copyDirectory(source, target);
		} catch (IOException e) {
			throw new SiteWhereException("Unable to copy global resources to tenant.");
		}
	}

	/**
	 * Get or create folder that contains tenant-specific data.
	 * 
	 * @param tenantId
	 * @return
	 * @throws SiteWhereException
	 */
	protected File assureTenantFolder(String tenantId) throws SiteWhereException {
		File folder = new File(
				getRootFolder().getAbsolutePath() + File.separator + TENANTS_FOLDER_NAME + File.separator + tenantId);
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
		MultiResourceCreateResponse response = new MultiResourceCreateResponse();
		for (IResourceCreateRequest request : requests) {
			IResource resource = getTenantResource(tenantId, request.getPath());
			String qualifier = TENANTS_FOLDER_NAME + File.separator + tenantId;
			if (resource == null) {
				writeResourceFile(request, qualifier, response);
			} else {
				handleResourceExists(request, qualifier, response, mode);
			}
		}
		return response;
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
		ResourceMap map = getTenantResourceMaps().get(tenantId);
		if (map != null) {
			return map.get(path);
		}
		return null;
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
		ResourceMap tenant = getTenantResourceMaps().get(tenantId);
		if (tenant != null) {
			List<IResource> results = new ArrayList<IResource>();
			for (String path : tenant.keySet()) {
				results.add(tenant.get(path));
			}
			Collections.sort(results, new Comparator<IResource>() {

				@Override
				public int compare(IResource r1, IResource r2) {
					return r1.getPath().compareTo(r2.getPath());
				}
			});
			return results;
		}
		return new ArrayList<IResource>();
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
		return deleteResource(TENANTS_FOLDER_NAME + File.separator + tenantId, getTenantResource(tenantId, path));
	}

	/**
	 * Watches for file system changes in another thread.
	 * 
	 * @author Derek
	 */
	private class FileSystemWatcher implements Runnable {

		/** Watches for changes on filesystem */
		private WatchService watcher;

		/** Used to determine which path is generating events */
		private Map<WatchKey, Path> keysToPaths = new HashMap<WatchKey, Path>();

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try {
				this.watcher = FileSystems.getDefault().newWatchService();
				registerRecursively();

				while (true) {
					WatchKey key;
					try {
						key = watcher.take();
					} catch (InterruptedException e) {
						watcher.close();
						return;
					}

					Path basePath = keysToPaths.get(key);
					for (WatchEvent<?> event : key.pollEvents()) {
						WatchEvent.Kind<?> kind = event.kind();

						if (kind == StandardWatchEventKinds.OVERFLOW) {
							continue;
						}

						WatchEvent<Path> ev = (WatchEvent<Path>) event;
						Path relativePath = ev.context();
						Path fullPath = basePath.resolve(relativePath);
						File file = fullPath.toFile();
						if ((!file.isDirectory())) {
							if ((kind == StandardWatchEventKinds.ENTRY_CREATE)
									|| (kind == StandardWatchEventKinds.ENTRY_MODIFY)) {
								try {
									cacheFile(file);
									LOGGER.info("Created/updated resource: " + file.getAbsolutePath());
								} catch (Throwable t) {
									LOGGER.error("Unable to cache resource: " + file.getAbsolutePath(), t);
								}
							} else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
								uncacheFile(file);
								LOGGER.info("Deleted resource: " + file.getAbsolutePath());
							}
						}
					}

					boolean valid = key.reset();
					if (!valid) {
						LOGGER.warn("Watch key no longer valid. No longer watching for file system changes.");
						return;
					}
				}
			} catch (IOException e) {
				LOGGER.error("Unable to create watcher for file updates.", e);
			}
		}

		/**
		 * Recursively register filesystem events for all folders under the
		 * configuration root.
		 * 
		 * @throws IOException
		 */
		protected void registerRecursively() throws IOException {
			Files.walkFileTree(getRootFolder().toPath(), new FileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(Path path, IOException e) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attr) throws IOException {
					WatchKey key = path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
					keysToPaths.put(key, path);
					LOGGER.debug("Listening for changes to: " + path.toFile().getAbsolutePath());
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path path, IOException e) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
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

	public ResourceMap getGlobalResourceMap() {
		return globalResourceMap;
	}

	public void setGlobalResourceMap(ResourceMap globalResourceMap) {
		this.globalResourceMap = globalResourceMap;
	}

	public Map<String, ResourceMap> getTenantResourceMaps() {
		return tenantResourceMaps;
	}

	public void setTenantResourceMaps(Map<String, ResourceMap> tenantResourceMaps) {
		this.tenantResourceMaps = tenantResourceMaps;
	}

	/**
	 * Class for storing path->resource relationships.
	 * 
	 * @author Derek
	 */
	private class ResourceMap extends HashMap<String, IResource> {

		/** Serial version UID */
		private static final long serialVersionUID = -7209786156575267473L;
	}
}