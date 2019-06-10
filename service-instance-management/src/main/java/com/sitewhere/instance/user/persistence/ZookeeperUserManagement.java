/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.user.persistence;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;

import com.sitewhere.common.MarshalUtils;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.search.Pager;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * User management implementation which stores data in Zookeeper.
 */
public class ZookeeperUserManagement extends LifecycleComponent implements IUserManagement {

    /** JSON data for authority model */
    private static final String AUTHORITY_JSON = "authority.json";

    /** JSON data for user model */
    private static final String USER_JSON = "user.json";

    public ZookeeperUserManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#createUser(com.sitewhere.spi.user.
     * request.IUserCreateRequest, java.lang.Boolean)
     */
    @Override
    public IUser createUser(IUserCreateRequest request, Boolean encodePassword) throws SiteWhereException {
	User user = UserManagementPersistenceLogic.userCreateLogic(request, encodePassword);

	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	String userPath = getUserModelPath(request.getUsername());
	try {
	    Stat existing = curator.checkExists().forPath(userPath);
	    if (existing == null) {
		getLogger().debug("Zk node for user '" + request.getUsername() + "' not found. Creating...");
		return storeUser(user);
	    } else {
		throw new SiteWhereSystemException(ErrorCode.DuplicateUser, ErrorLevel.ERROR);
	    }
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to create user.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#importUser(com.sitewhere.spi.user.
     * IUser, boolean)
     */
    @Override
    public IUser importUser(IUser user, boolean overwrite) throws SiteWhereException {
	IUser existing = getUserByUsername(user.getUsername());
	if (existing != null) {
	    if (!overwrite) {
		throw new SiteWhereSystemException(ErrorCode.DuplicateUser, ErrorLevel.ERROR);
	    }
	    deleteUser(user.getUsername());
	}

	getLogger().debug(String.format("Updating user '%s' at path '%s'", user.getUsername(),
		getUserModelPath(user.getUsername())));
	return storeUser(user);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#authenticate(java.lang.String,
     * java.lang.String, boolean)
     */
    @Override
    public IUser authenticate(String username, String password, boolean updateLastLogin) throws SiteWhereException {
	if (password == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidPassword, ErrorLevel.ERROR);
	}
	User match = User.copy(getUserByUsername(username));
	if (!UserManagementPersistenceLogic.passwordMatches(password, match.getHashedPassword())) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidPassword, ErrorLevel.ERROR);
	}

	// Update last login date if requested.
	if (updateLastLogin) {
	    match.setLastLogin(new Date());
	    storeUser(match);
	}

	return match;
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#updateUser(java.lang.String,
     * com.sitewhere.spi.user.request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	User match = User.copy(getUserByUsername(username));
	if (match == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR);
	}

	UserManagementPersistenceLogic.userUpdateLogic(request, match, encodePassword);
	return storeUser(match);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#getUserByUsername(java.lang.String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	String path = getUserModelPath(username);
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	try {
	    Stat existing = curator.checkExists().forPath(path);
	    if (existing != null) {
		byte[] content = curator.getData().forPath(path);
		return MarshalUtils.unmarshalJson(content, User.class);
	    }
	    return null;
	} catch (Exception e) {
	    throw new SiteWhereException(String.format("Unable to get user at path '%s'.", path));
	}
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#getGrantedAuthorities(java.lang.
     * String)
     */
    @Override
    public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
	IUser user = getUserByUsername(username);
	List<String> userAuths = user.getAuthorities();
	ISearchResults<IGrantedAuthority> all = listGrantedAuthorities(new GrantedAuthoritySearchCriteria(1, 0));
	List<IGrantedAuthority> matched = new ArrayList<IGrantedAuthority>();
	for (IGrantedAuthority auth : all.getResults()) {
	    if (userAuths.contains(auth.getAuthority())) {
		matched.add(auth);
	    }
	}
	return matched;
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#addGrantedAuthorities(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IGrantedAuthority> addGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#removeGrantedAuthorities(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IGrantedAuthority> removeGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
     * IUserSearchCriteria)
     */
    @Override
    public ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	try {
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    List<String> children = curator.getChildren()
		    .forPath(getInstanceManagementMicroservice().getInstanceUsersConfigurationPath());
	    List<IUser> users = new ArrayList<>();
	    for (String child : children) {
		users.add(getUserByUsername(child));
	    }
	    users.sort(new Comparator<IUser>() {

		@Override
		public int compare(IUser o1, IUser o2) {
		    return o1.getUsername().compareTo(o2.getUsername());
		}
	    });
	    Pager<IUser> pager = new Pager<IUser>(criteria);
	    for (IUser user : users) {
		pager.process(user);
	    }
	    return new SearchResults<IUser>(pager.getResults(), pager.getTotal());
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to retrieve user list from Zookeeper.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String)
     */
    @Override
    public IUser deleteUser(String username) throws SiteWhereException {
	try {
	    IUser user = getUserByUsername(username);
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    String path = getUserModelPath(username);
	    curator.delete().deletingChildrenIfNeeded().forPath(path);
	    return user;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to delete user from Zookeeper.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#createGrantedAuthority(com.sitewhere.
     * spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	GrantedAuthority ga = UserManagementPersistenceLogic.grantedAuthorityCreateLogic(request);

	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	String authPath = getAuthorityModelPath(request.getAuthority());
	try {
	    Stat existing = curator.checkExists().forPath(authPath);
	    if (existing == null) {
		getLogger().debug("Zk node for authority '" + request.getAuthority() + "' not found. Creating...");
		return storeAuthority(ga);
	    } else {
		throw new SiteWhereSystemException(ErrorCode.DuplicateAuthority, ErrorLevel.ERROR);
	    }
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to create authority.", e);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.lang.
     * String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	String authPath = getAuthorityModelPath(name);
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	try {
	    Stat existing = curator.checkExists().forPath(authPath);
	    if (existing != null) {
		byte[] content = curator.getData().forPath(authPath);
		return MarshalUtils.unmarshalJson(content, GrantedAuthority.class);
	    }
	    return null;
	} catch (Exception e) {
	    throw new SiteWhereException(String.format("Unable to get authority at path '%s'.", authPath));
	}
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.
     * String, com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.sitewhere.
     * spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	    List<String> children = curator.getChildren()
		    .forPath(getInstanceManagementMicroservice().getInstanceAuthoritiesConfigurationPath());
	    List<IGrantedAuthority> auths = new ArrayList<>();
	    for (String child : children) {
		auths.add(getGrantedAuthorityByName(child));
	    }
	    auths.sort(new Comparator<IGrantedAuthority>() {

		@Override
		public int compare(IGrantedAuthority o1, IGrantedAuthority o2) {
		    return o1.getAuthority().compareTo(o2.getAuthority());
		}
	    });
	    Pager<IGrantedAuthority> pager = new Pager<IGrantedAuthority>(criteria);
	    for (IGrantedAuthority auth : auths) {
		pager.process(auth);
	    }
	    return new SearchResults<IGrantedAuthority>(pager.getResults(), pager.getTotal());
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to retrieve authority list from Zookeeper.", e);
	}
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.
     * String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	throw new SiteWhereException("Not implemented.");
    }

    /**
     * Get path to model JSON for authority.
     * 
     * @param authority
     * @return
     * @throws SiteWhereException
     */
    protected String getAuthorityModelPath(String authority) throws SiteWhereException {
	return getInstanceManagementMicroservice().getInstanceAuthorityConfigurationPath(authority) + "/"
		+ AUTHORITY_JSON;
    }

    /**
     * Store an authority in Zookeeper.
     * 
     * @param ga
     * @return
     * @throws SiteWhereException
     */
    protected IGrantedAuthority storeAuthority(IGrantedAuthority ga) throws SiteWhereException {
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	String authPath = getAuthorityModelPath(ga.getAuthority());
	try {
	    curator.create().creatingParentsIfNeeded().forPath(authPath,
		    MarshalUtils.marshalJsonAsPrettyString(ga).getBytes());
	    return ga;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to store authority information in Zookeeper.", e);
	}
    }

    /**
     * Get path to model JSON for user.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    protected String getUserModelPath(String username) throws SiteWhereException {
	return getInstanceManagementMicroservice().getInstanceUserConfigurationPath(username) + "/" + USER_JSON;
    }

    /**
     * Store a user in Zookeeper.
     * 
     * @param user
     * @return
     * @throws SiteWhereException
     */
    protected IUser storeUser(IUser user) throws SiteWhereException {
	CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
	String userPath = getUserModelPath(user.getUsername());
	try {
	    curator.create().creatingParentsIfNeeded().forPath(userPath,
		    MarshalUtils.marshalJsonAsPrettyString(user).getBytes());
	    return user;
	} catch (Exception e) {
	    throw new SiteWhereException("Unable to store user information in Zookeeper.", e);
	}
    }

    protected IInstanceManagementMicroservice<?> getInstanceManagementMicroservice() {
	return (IInstanceManagementMicroservice<?>) getMicroservice();
    }
}
