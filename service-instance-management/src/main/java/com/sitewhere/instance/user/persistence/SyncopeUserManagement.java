/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.user.persistence;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.codec.binary.Base64;
import org.apache.syncope.client.lib.SyncopeClient;
import org.apache.syncope.client.lib.SyncopeClientFactoryBean;
import org.apache.syncope.common.lib.SyncopeClientException;
import org.apache.syncope.common.lib.to.AnyTypeClassTO;
import org.apache.syncope.common.lib.to.ApplicationTO;
import org.apache.syncope.common.lib.to.AttrTO;
import org.apache.syncope.common.lib.to.PagedResult;
import org.apache.syncope.common.lib.to.PlainSchemaTO;
import org.apache.syncope.common.lib.to.PrivilegeTO;
import org.apache.syncope.common.lib.to.UserTO;
import org.apache.syncope.common.lib.types.AttrSchemaType;
import org.apache.syncope.common.lib.types.SchemaType;
import org.apache.syncope.common.rest.api.beans.AnyQuery;
import org.apache.syncope.common.rest.api.service.AnyTypeClassService;
import org.apache.syncope.common.rest.api.service.ApplicationService;
import org.apache.syncope.common.rest.api.service.SchemaService;
import org.apache.syncope.common.rest.api.service.UserService;

import com.evanlennick.retry4j.CallExecutorBuilder;
import com.evanlennick.retry4j.Status;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import com.evanlennick.retry4j.exception.RetriesExhaustedException;
import com.evanlennick.retry4j.listener.RetryListener;
import com.sitewhere.common.MarshalUtils;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Interact with Apache Synope instance to manage users.
 */
public class SyncopeUserManagement extends LifecycleComponent implements IUserManagement {

    /** Default Syncope username */
    private static final String SYNCOPE_USERNAME = "synadmin";

    /** Default Syncope password */
    private static final String SYNCOPE_PASSWORD = "password";

    /** SiteWhere Syncope application key */
    private static final String SITEWHERE_APPLICATION_KEY = "sitewhere";

    /** Attribute that holds first name */
    private static final String ATTR_FIRST_NAME = "firstName";

    /** Attribute that holds JSON payload */
    private static final String ATTR_LAST_NAME = "lastName";

    /** Attribute that holds JSON payload */
    private static final String ATTR_JSON = "json";

    /** Client for interacting with Syncope */
    private SyncopeClient client;

    /** Indicates whether connection to Syncope failed */
    private AtomicBoolean connectionFailed = new AtomicBoolean(false);

    /** Allows blocking until Syncope is available */
    private CountDownLatch syncopeAvailable = new CountDownLatch(1);

    /** Application service */
    private ApplicationService applicationService;

    /** Provides thread for waiter */
    private ExecutorService executor;

    /** User service */
    private UserService userService;

    /** Schema service */
    private SchemaService schemaService;

    /** AnyType service */
    private AnyTypeClassService anyTypeClassService;

    public SyncopeUserManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Wait for connection in background thread.
	this.executor = Executors.newSingleThreadExecutor();
	executor.execute(new SyncopeConnectionWaiter());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Block until Syncope is available.
	try {
	    getSyncopeAvailable().await();
	} catch (InterruptedException e) {
	    getLogger().info("Interrupted while waiting for Syncope to become available.");
	    return;
	}

	// Verify that connection was successful.
	if (getConnectionFailed().get()) {
	    throw new SiteWhereException("Connection to Syncope could not be established.");
	}

	IInstanceSettings settings = getMicroservice().getInstanceSettings();
	String domain = getClient().getDomain();
	getLogger().info(String.format("Syncope client connected to %s:%d using domain %s.", settings.getSyncopeHost(),
		settings.getSyncopePort(), domain));
	this.applicationService = client.getService(ApplicationService.class);
	this.userService = client.getService(UserService.class);
	this.schemaService = client.getService(SchemaService.class);
	this.anyTypeClassService = client.getService(AnyTypeClassService.class);

	// Verify that SiteWhere application exists.
	getOrCreateSiteWhereApplication();
    }

    /**
     * Get or create Syncope application for SiteWhere.
     * 
     * @throws SiteWhereException
     */
    protected ApplicationTO getOrCreateSiteWhereApplication() throws SiteWhereException {
	try {
	    return getApplicationService().read(SITEWHERE_APPLICATION_KEY);
	} catch (SyncopeClientException e) {
	    getLogger().info("SiteWhere Syncope application descriptor not found. Creating descriptor...");
	    ApplicationTO application = new ApplicationTO();
	    application.setDescription("SiteWhere Application");
	    application.setKey(SITEWHERE_APPLICATION_KEY);
	    getApplicationService().create(application);
	    getLogger()
		    .info(String.format("Created application '%s' with SiteWhere key.", application.getDescription()));

	    // First name user attribute.
	    PlainSchemaTO firstName = new PlainSchemaTO();
	    firstName.setKey(ATTR_FIRST_NAME);
	    firstName.setType(AttrSchemaType.String);
	    getSchemaService().create(SchemaType.PLAIN, firstName);

	    // Last name user attribute.
	    PlainSchemaTO lastName = new PlainSchemaTO();
	    lastName.setKey(ATTR_LAST_NAME);
	    lastName.setType(AttrSchemaType.String);
	    getSchemaService().create(SchemaType.PLAIN, lastName);

	    // JSON user attribute.
	    PlainSchemaTO json = new PlainSchemaTO();
	    json.setKey(ATTR_JSON);
	    json.setType(AttrSchemaType.Binary);
	    getSchemaService().create(SchemaType.PLAIN, json);

	    AnyTypeClassTO baseUser = new AnyTypeClassTO();
	    baseUser.setKey("BaseUser");
	    baseUser.getPlainSchemas().add(ATTR_FIRST_NAME);
	    baseUser.getPlainSchemas().add(ATTR_LAST_NAME);
	    baseUser.getPlainSchemas().add(ATTR_JSON);
	    getAnyTypeClassService().update(baseUser);

	    return application;
	}
    }

    /**
     * Waits for Syncope connection to become available.
     */
    protected class SyncopeConnectionWaiter implements Runnable {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void run() {
	    IInstanceSettings settings = getMicroservice().getInstanceSettings();
	    String syncopeUrl = String.format("http://%s:%d/syncope/rest", settings.getSyncopeHost(),
		    settings.getSyncopePort());
	    SyncopeClientFactoryBean clientFactory = new SyncopeClientFactoryBean().setAddress(syncopeUrl);

	    try {
		getLogger().info(String.format("Attempting to connect to Syncope at '%s:%d' as '%s'...",
			settings.getSyncopeHost(), settings.getSyncopePort(), SYNCOPE_USERNAME));
		Callable<Boolean> connectCheck = () -> {
		    SyncopeUserManagement.this.client = clientFactory.create(SYNCOPE_USERNAME, SYNCOPE_PASSWORD);
		    getSyncopeAvailable().countDown();
		    return true;
		};
		RetryConfig config = new RetryConfigBuilder().retryOnAnyException().withMaxNumberOfTries(7)
			.withDelayBetweenTries(Duration.ofSeconds(2)).withRandomExponentialBackoff().build();
		RetryListener listener = new RetryListener<Boolean>() {

		    @Override
		    public void onEvent(Status<Boolean> status) {
			getLogger().info(String.format(
				"Unable to connect to Syncope on attempt %d [%s] (total wait so far %dms). Retrying after fallback...",
				status.getTotalTries(), status.getLastExceptionThatCausedRetry().getMessage(),
				status.getTotalElapsedDuration().toMillis()));
		    }
		};
		new CallExecutorBuilder().config(config).afterFailedTryListener(listener).build().execute(connectCheck);
	    } catch (RetriesExhaustedException e) {
		Status status = e.getStatus();
		getLogger().error(String.format("Unable to connect to Syncope at '%s:%d' after %d attempts (%dms).",
			settings.getSyncopeHost(), settings.getSyncopePort(), status.getTotalTries(),
			status.getTotalElapsedDuration().toMillis()));
		getConnectionFailed().set(true);
		getSyncopeAvailable().countDown();
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#createUser(com.sitewhere.spi.user.
     * request.IUserCreateRequest, java.lang.Boolean)
     */
    @Override
    public IUser createUser(IUserCreateRequest request, Boolean encodePassword) throws SiteWhereException {
	User swuser = UserManagementPersistenceLogic.userCreateLogic(request, encodePassword);

	UserTO user = new UserTO();
	user.setRealm("/");
	user.setCreationDate(swuser.getCreatedDate());
	user.setCreator(swuser.getCreatedBy());
	user.setUsername(request.getUsername());
	user.setPassword(request.getPassword());
	user.getPlainAttrs().add(createAttribute(ATTR_FIRST_NAME, request.getFirstName()));
	user.getPlainAttrs().add(createAttribute(ATTR_LAST_NAME, request.getLastName()));
	user.getPlainAttrs().add(createAttribute(ATTR_JSON,
		Base64.encodeBase64String(MarshalUtils.marshalJsonAsString(swuser).getBytes())));

	swuser.getAuthorities().forEach(auth -> {
	    user.getPrivileges().add(auth);
	});

	try {
	    getUserService().create(user, true);
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to create user.", t);
	}

	return swuser;
    }

    protected AttrTO createAttribute(String name, String value) {
	AttrTO.Builder attr = new AttrTO.Builder();
	return attr.schema(name).value(value).build();
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#importUser(com.sitewhere.spi.user.
     * IUser, boolean)
     */
    @Override
    public IUser importUser(IUser user, boolean overwrite) throws SiteWhereException {
	throw new RuntimeException("Not implemented.");
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
	IUser match = getUserByUsername(username);
	if (!UserManagementPersistenceLogic.passwordMatches(password, match.getHashedPassword())) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidPassword, ErrorLevel.ERROR);
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
	throw new RuntimeException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#getUserByUsername(java.lang.String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	try {
	    UserTO match = getUserService().read(username);
	    getLogger().info(String.format("User data returned in getUserByUsername()\n\n%s",
		    MarshalUtils.marshalJsonAsPrettyString(match)));
	    return convertUser(match);
	} catch (Throwable t) {
	    throw new SiteWhereException("Unable to get user by username.", t);
	}
    }

    /**
     * Convert Syncope user to SiteWhere user.
     * 
     * @param user
     * @return
     * @throws SiteWhereException
     */
    protected IUser convertUser(UserTO user) throws SiteWhereException {
	Optional<AttrTO> json = user.getPlainAttr(ATTR_JSON);
	if (json.isPresent()) {
	    String encoded = new String(json.get().getValues().get(0).getBytes());
	    return MarshalUtils.unmarshalJson(Base64.decodeBase64(encoded), User.class);
	}
	throw new SiteWhereException("Syncope user did not contain JSON data.");
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
	throw new RuntimeException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#removeGrantedAuthorities(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IGrantedAuthority> removeGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	throw new RuntimeException("Not implemented.");
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
     * IUserSearchCriteria)
     */
    @Override
    public ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	PagedResult<UserTO> users = getUserService()
		.search(new AnyQuery.Builder().page(criteria.getPageNumber() - 1).size(criteria.getPageSize()).build());
	List<IUser> matches = new ArrayList<>();
	for (UserTO user : users.getResult()) {
	    matches.add(convertUser(user));
	}
	return new SearchResults<>(matches, users.getTotalCount());
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String)
     */
    @Override
    public IUser deleteUser(String username) throws SiteWhereException {
	throw new RuntimeException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#createGrantedAuthority(com.sitewhere.
     * spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	IGrantedAuthority existing = getGrantedAuthorityByName(request.getAuthority());
	if (existing != null) {
	    throw new SiteWhereSystemException(ErrorCode.DuplicateAuthority, ErrorLevel.ERROR);
	}
	GrantedAuthority auth = UserManagementPersistenceLogic.grantedAuthorityCreateLogic(request);

	// Convert auth to priv and save to application.
	ApplicationTO application = getOrCreateSiteWhereApplication();
	application.getPrivileges().add(createPrivilegeFromAuthorityRequest(auth));
	try {
	    getApplicationService().update(application);
	} catch (Throwable t) {
	    throw new SiteWhereException(
		    String.format("Unable to create granted authority '%s'.", request.getAuthority()), t);
	}

	return auth;
    }

    /**
     * Create a Syncope privilege from an SiteWhere authority.
     * 
     * @param authority
     * @return
     * @throws SiteWhereException
     */
    protected PrivilegeTO createPrivilegeFromAuthorityRequest(GrantedAuthority authority) throws SiteWhereException {
	PrivilegeTO priv = new PrivilegeTO();
	priv.setApplication(SITEWHERE_APPLICATION_KEY);
	priv.setDescription(authority.getDescription());
	priv.setKey(authority.getAuthority());
	priv.setSpec(MarshalUtils.marshalJsonAsString(authority));
	return priv;
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.lang.
     * String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	ISearchResults<IGrantedAuthority> auths = listGrantedAuthorities(new GrantedAuthoritySearchCriteria(1, 0));
	for (IGrantedAuthority auth : auths.getResults()) {
	    if (auth.getAuthority().equals(name)) {
		return auth;
	    }
	}
	return null;
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.
     * String, com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	throw new RuntimeException("Not implemented.");
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.sitewhere.
     * spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	ApplicationTO application = getOrCreateSiteWhereApplication();
	List<PrivilegeTO> privs = application.getPrivileges();

	List<IGrantedAuthority> auths = new ArrayList<>();
	if (privs != null) {
	    privs.forEach(priv -> {
		auths.add(MarshalUtils.unmarshalJson(priv.getSpec().getBytes(), GrantedAuthority.class));
	    });
	}
	return new SearchResults<IGrantedAuthority>(auths);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.
     * String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	PrivilegeTO toRemove = null;
	ApplicationTO application = getOrCreateSiteWhereApplication();
	List<PrivilegeTO> privs = application.getPrivileges();
	for (PrivilegeTO priv : privs) {
	    if (priv.getKey().equals(authority)) {
		toRemove = priv;
	    }
	}
	if (toRemove != null) {
	    application.getPrivileges().remove(toRemove);
	    try {
		getApplicationService().update(application);
	    } catch (Throwable t) {
		throw new SiteWhereException(String.format("Unable to delete granted authority '%s'.", authority), t);
	    }
	}
    }

    protected SyncopeClient getClient() {
	return client;
    }

    protected AtomicBoolean getConnectionFailed() {
	return connectionFailed;
    }

    protected CountDownLatch getSyncopeAvailable() {
	return syncopeAvailable;
    }

    protected ApplicationService getApplicationService() {
	return applicationService;
    }

    protected UserService getUserService() {
	return userService;
    }

    protected SchemaService getSchemaService() {
	return schemaService;
    }

    protected AnyTypeClassService getAnyTypeClassService() {
	return anyTypeClassService;
    }
}
