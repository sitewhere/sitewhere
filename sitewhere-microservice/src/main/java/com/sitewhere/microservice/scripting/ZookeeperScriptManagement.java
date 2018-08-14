/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.scripting;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.zookeeper.KeeperException.NoNodeException;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.core.Base58;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice;
import com.sitewhere.spi.microservice.configuration.IZookeeperManager;
import com.sitewhere.spi.microservice.scripting.IScriptCreateRequest;
import com.sitewhere.spi.microservice.scripting.IScriptManagement;
import com.sitewhere.spi.microservice.scripting.IScriptMetadata;
import com.sitewhere.spi.microservice.scripting.IScriptVersion;

/**
 * Default {@link IScriptManagement} implementation. Stores scripts in
 * Zookeeper.
 * 
 * @author Derek
 */
public class ZookeeperScriptManagement extends LifecycleComponent implements IScriptManagement {

    /** Folder path for script metadata */
    public static final String METADATA_FOLDER = "metadata";

    /** Folder path for script content */
    public static final String CONTENT_FOLDER = "content";

    /** Suffix added to indicate a metadata file */
    public static final String METADATA_SUFFIX = ".meta";

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManagement#
     * getScriptMetadataZkPath(com.sitewhere.spi.microservice.IFunctionIdentifier,
     * java.util.UUID)
     */
    @Override
    public String getScriptMetadataZkPath(IFunctionIdentifier identifier, UUID tenantId) throws SiteWhereException {
	if (tenantId == null) {
	    return ((IConfigurableMicroservice<?>) getMicroservice()).getInstanceGlobalScriptsPath() + "/"
		    + identifier.getPath() + "/" + METADATA_FOLDER;
	} else {
	    return ((IConfigurableMicroservice<?>) getMicroservice()).getInstanceTenantScriptsPath(tenantId) + "/"
		    + identifier.getPath() + "/" + METADATA_FOLDER;
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManagement#
     * getScriptContentZkPath(com.sitewhere.spi.microservice.IFunctionIdentifier,
     * java.util.UUID)
     */
    @Override
    public String getScriptContentZkPath(IFunctionIdentifier identifier, UUID tenantId) throws SiteWhereException {
	if (tenantId == null) {
	    return ((IConfigurableMicroservice<?>) getMicroservice()).getInstanceGlobalScriptsPath() + "/"
		    + identifier.getPath() + "/" + CONTENT_FOLDER;
	} else {
	    return ((IConfigurableMicroservice<?>) getMicroservice()).getInstanceTenantScriptsPath(tenantId) + "/"
		    + identifier.getPath() + "/" + CONTENT_FOLDER;
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.scripting.IScriptManagement#
     * getScriptMetadataList(com.sitewhere.spi.microservice.IFunctionIdentifier,
     * java.util.UUID)
     */
    @Override
    public List<IScriptMetadata> getScriptMetadataList(IFunctionIdentifier identifier, UUID tenantId)
	    throws SiteWhereException {
	try {
	    List<String> children = getZookeeperManager().getCurator().getChildren()
		    .forPath(getScriptMetadataZkPath(identifier, tenantId));
	    List<IScriptMetadata> result = new ArrayList<>();
	    for (String child : children) {
		if (child.endsWith(METADATA_SUFFIX)) {
		    result.add(getScriptMetadata(identifier, tenantId,
			    child.substring(0, child.indexOf(METADATA_SUFFIX))));
		}
	    }
	    result.sort(new Comparator<IScriptMetadata>() {

		@Override
		public int compare(IScriptMetadata o1, IScriptMetadata o2) {
		    return o1.getName().compareTo(o2.getName());
		}
	    });
	    return result;
	} catch (NoNodeException e) {
	    return new ArrayList<>();
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to retrieve script metadata list.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#getScriptMetadata(
     * com.sitewhere.spi.microservice.IFunctionIdentifier, java.util.UUID,
     * java.lang.String)
     */
    @Override
    public IScriptMetadata getScriptMetadata(IFunctionIdentifier identifier, UUID tenantId, String scriptId)
	    throws SiteWhereException {
	try {
	    String path = getScriptMetadataZkPath(identifier, tenantId) + "/" + scriptId + METADATA_SUFFIX;
	    byte[] content = getZookeeperManager().getCurator().getData().forPath(path);
	    return MarshalUtils.unmarshalJson(content, ScriptMetadata.class);
	} catch (NoNodeException e) {
	    return null;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to retrieve script metadata.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#createScript(com.
     * sitewhere.spi.microservice.IFunctionIdentifier, java.util.UUID,
     * com.sitewhere.spi.microservice.scripting.IScriptCreateRequest)
     */
    @Override
    public IScriptMetadata createScript(IFunctionIdentifier identifier, UUID tenantId, IScriptCreateRequest request)
	    throws SiteWhereException {
	IScriptMetadata existing = getScriptMetadata(identifier, tenantId, request.getId());
	if (existing != null) {
	    throw new SiteWhereException("A script with that id already exists.");
	}
	ScriptMetadata created = createScriptMetadata(request);
	try {
	    IScriptVersion version = created.getVersions().get(0);
	    store(identifier, tenantId, created, version, request.getContent());
	    activateScript(identifier, tenantId, request.getId(), version.getVersionId());
	    return created;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to store script metadata.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#getScriptContent(
     * com.sitewhere.spi.microservice.IFunctionIdentifier, java.util.UUID,
     * java.lang.String, java.lang.String)
     */
    @Override
    public byte[] getScriptContent(IFunctionIdentifier identifier, UUID tenantId, String scriptId, String versionId)
	    throws SiteWhereException {
	IScriptMetadata meta = assureScriptMetadata(identifier, tenantId, scriptId);
	IScriptVersion version = assureScriptVersion(meta, versionId);
	String contentPath = getScriptMetadataZkPath(identifier, tenantId) + "/" + getVersionContentPath(meta, version);
	try {
	    return getZookeeperManager().getCurator().getData().forPath(contentPath);
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to read script content for '" + versionId + "'.");
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#updateScript(com.
     * sitewhere.spi.microservice.IFunctionIdentifier, java.util.UUID,
     * java.lang.String, java.lang.String,
     * com.sitewhere.spi.microservice.scripting.IScriptCreateRequest)
     */
    @Override
    public IScriptMetadata updateScript(IFunctionIdentifier identifier, UUID tenantId, String scriptId,
	    String versionId, IScriptCreateRequest request) throws SiteWhereException {
	IScriptMetadata meta = assureScriptMetadata(identifier, tenantId, scriptId);
	IScriptVersion version = assureScriptVersion(meta, versionId);
	try {
	    store(identifier, tenantId, meta, version, request.getContent());
	    return meta;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to store script metadata.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#cloneScript(com.
     * sitewhere.spi.microservice.IFunctionIdentifier, java.util.UUID,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public IScriptVersion cloneScript(IFunctionIdentifier identifier, UUID tenantId, String scriptId, String versionId,
	    String comment) throws SiteWhereException {
	IScriptMetadata meta = assureScriptMetadata(identifier, tenantId, scriptId);
	assureScriptVersion(meta, versionId);
	ScriptVersion created = new ScriptVersion();
	created.setVersionId(generateUniqueId());
	created.setComment(comment);
	created.setCreatedDate(new Date());
	meta.getVersions().add(created);

	try {
	    // Save updated metadata.
	    String metaPath = getScriptMetadataZkPath(identifier, tenantId) + "/" + getMetadataFilePath(meta);
	    byte[] metaContent = MarshalUtils.marshalJson(meta);
	    if (getZookeeperManager().getCurator().checkExists().forPath(metaPath) == null) {
		getZookeeperManager().getCurator().create().creatingParentsIfNeeded().forPath(metaPath, metaContent);
	    } else {
		getZookeeperManager().getCurator().setData().forPath(metaPath, metaContent);
	    }

	    // Save new version.
	    String contentPath = getScriptMetadataZkPath(identifier, tenantId) + "/"
		    + getVersionContentPath(meta, created);
	    byte[] content = getScriptContent(identifier, tenantId, scriptId, versionId);
	    if (getZookeeperManager().getCurator().checkExists().forPath(contentPath) == null) {
		getZookeeperManager().getCurator().create().creatingParentsIfNeeded().forPath(contentPath, content);
	    } else {
		getZookeeperManager().getCurator().setData().forPath(contentPath, content);
	    }
	    return created;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to clone script.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#activateScript(com
     * .sitewhere.spi.microservice.IFunctionIdentifier, java.util.UUID,
     * java.lang.String, java.lang.String)
     */
    @Override
    public IScriptMetadata activateScript(IFunctionIdentifier identifier, UUID tenantId, String scriptId,
	    String versionId) throws SiteWhereException {
	IScriptMetadata meta = assureScriptMetadata(identifier, tenantId, scriptId);
	assureScriptVersion(meta, versionId);

	try {
	    // Update active version id if changed.
	    if (!meta.getActiveVersion().equals(versionId)) {
		((ScriptMetadata) meta).setActiveVersion(versionId);
		String metaPath = getScriptMetadataZkPath(identifier, tenantId) + "/" + getMetadataFilePath(meta);
		byte[] metaContent = MarshalUtils.marshalJson(meta);
		if (getZookeeperManager().getCurator().checkExists().forPath(metaPath) == null) {
		    getZookeeperManager().getCurator().create().creatingParentsIfNeeded().forPath(metaPath,
			    metaContent);
		} else {
		    getZookeeperManager().getCurator().setData().forPath(metaPath, metaContent);
		}
	    }

	    // Create content file.
	    String contentPath = getScriptContentZkPath(identifier, tenantId) + "/" + meta.getId() + "."
		    + meta.getType();
	    byte[] content = getScriptContent(identifier, tenantId, scriptId, versionId);
	    if (getZookeeperManager().getCurator().checkExists().forPath(contentPath) == null) {
		getZookeeperManager().getCurator().create().creatingParentsIfNeeded().forPath(contentPath, content);
	    } else {
		getZookeeperManager().getCurator().setData().forPath(contentPath, content);
	    }
	    return meta;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to activate script version.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.scripting.IScriptManagement#deleteScript(com.
     * sitewhere.spi.microservice.IFunctionIdentifier, java.util.UUID,
     * java.lang.String)
     */
    @Override
    public IScriptMetadata deleteScript(IFunctionIdentifier identifier, UUID tenantId, String scriptId)
	    throws SiteWhereException {
	IScriptMetadata meta = assureScriptMetadata(identifier, tenantId, scriptId);

	try {
	    // Delete metadata.
	    String metaPath = getScriptMetadataZkPath(identifier, tenantId) + "/" + getMetadataFilePath(meta);
	    if (getZookeeperManager().getCurator().checkExists().forPath(metaPath) != null) {
		getZookeeperManager().getCurator().delete().forPath(metaPath);
	    }

	    // Delete all versions.
	    String versionsPath = getScriptMetadataZkPath(identifier, tenantId) + "/" + meta.getId();
	    if (getZookeeperManager().getCurator().checkExists().forPath(versionsPath) != null) {
		getZookeeperManager().getCurator().delete().deletingChildrenIfNeeded().forPath(versionsPath);
	    }

	    // Delete content.
	    String contentPath = getScriptContentZkPath(identifier, tenantId) + "/" + meta.getId() + "."
		    + meta.getType();
	    if (getZookeeperManager().getCurator().checkExists().forPath(contentPath) != null) {
		getZookeeperManager().getCurator().delete().forPath(contentPath);
	    }
	    return meta;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to delete script.", e);
	}
    }

    /**
     * Assure that script metadata exists for the given id.
     * 
     * @param identifier
     * @param tenantId
     * @param scriptId
     * @return
     * @throws SiteWhereException
     */
    protected IScriptMetadata assureScriptMetadata(IFunctionIdentifier identifier, UUID tenantId, String scriptId)
	    throws SiteWhereException {
	IScriptMetadata meta = getScriptMetadata(identifier, tenantId, scriptId);
	if (meta == null) {
	    throw new SiteWhereException("Script not found: " + scriptId);
	}
	return meta;
    }

    /**
     * Assure that the script meta content contains the given version id.
     * 
     * @param meta
     * @param versionId
     * @return
     * @throws SiteWhereException
     */
    protected IScriptVersion assureScriptVersion(IScriptMetadata meta, String versionId) throws SiteWhereException {
	IScriptVersion version = getScriptVersion(meta, versionId);
	if (version == null) {
	    throw new SiteWhereException("No version of '" + meta.getId() + "' matches '" + versionId + "'.");
	}
	return version;
    }

    /**
     * Store updated metadata and content for a script/version.
     * 
     * @param identifier
     * @param tenantId
     * @param meta
     * @param version
     * @param contentStr
     * @throws SiteWhereException
     */
    protected void store(IFunctionIdentifier identifier, UUID tenantId, IScriptMetadata meta, IScriptVersion version,
	    String contentStr) throws SiteWhereException {
	try {
	    // Store metadata.
	    String metaPath = getScriptMetadataZkPath(identifier, tenantId) + "/" + getMetadataFilePath(meta);
	    byte[] metaContent = MarshalUtils.marshalJson(meta);
	    if (getZookeeperManager().getCurator().checkExists().forPath(metaPath) == null) {
		getZookeeperManager().getCurator().create().creatingParentsIfNeeded().forPath(metaPath, metaContent);
	    } else {
		getZookeeperManager().getCurator().setData().forPath(metaPath, metaContent);
	    }

	    // Store version content.
	    String contentPath = getScriptMetadataZkPath(identifier, tenantId) + "/"
		    + getVersionContentPath(meta, version);
	    byte[] content = Base64.getDecoder().decode(contentStr);
	    if (getZookeeperManager().getCurator().checkExists().forPath(contentPath) == null) {
		getZookeeperManager().getCurator().create().creatingParentsIfNeeded().forPath(contentPath, content);
	    } else {
		getZookeeperManager().getCurator().setData().forPath(contentPath, content);
	    }
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to store script metadata.", e);
	}
    }

    /**
     * Create script metadata from request information.
     * 
     * @param request
     * @return
     */
    protected ScriptMetadata createScriptMetadata(IScriptCreateRequest request) {
	ScriptMetadata created = new ScriptMetadata();
	created.setId(request.getId());
	created.setName(request.getName());
	created.setDescription(request.getDescription());
	created.setType(request.getType());
	String versionId = generateUniqueId();
	created.setActiveVersion(versionId);
	ScriptVersion version = new ScriptVersion();
	version.setVersionId(versionId);
	version.setCreatedDate(new Date());
	version.setComment(request.getDescription());
	created.getVersions().add(version);
	return created;
    }

    /**
     * Generate unique id by creating a UUID and encoding the bytes in base 58.
     * 
     * @return
     */
    protected String generateUniqueId() {
	UUID uuid = UUID.randomUUID();
	ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
	bb.putLong(uuid.getMostSignificantBits());
	bb.putLong(uuid.getLeastSignificantBits());
	return Base58.encode(bb.array());
    }

    /**
     * Get relative path for script metadata.
     * 
     * @param metadata
     * @return
     */
    protected String getMetadataFilePath(IScriptMetadata metadata) {
	return metadata.getId() + METADATA_SUFFIX;
    }

    /**
     * Get relative path for version content.
     * 
     * @param metadata
     * @param version
     * @return
     */
    protected String getVersionContentPath(IScriptMetadata metadata, IScriptVersion version) {
	return metadata.getId() + "/" + version.getVersionId();
    }

    /**
     * Get version information based on unique version id.
     * 
     * @param metadata
     * @param versionId
     * @return
     */
    protected IScriptVersion getScriptVersion(IScriptMetadata metadata, String versionId) {
	IScriptVersion version = null;
	for (IScriptVersion current : metadata.getVersions()) {
	    if (current.getVersionId().equals(versionId)) {
		version = current;
		break;
	    }
	}
	return version;
    }

    protected IZookeeperManager getZookeeperManager() {
	return getMicroservice().getZookeeperManager();
    }
}