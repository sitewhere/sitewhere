package com.sitewhere.web.microservice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.sitewhere.microservice.GlobalMicroservice;
import com.sitewhere.microservice.spi.security.ITokenManagement;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Microservice that provides web/REST functionality.
 * 
 * @author Derek
 */
public class WebRestMicroservice extends GlobalMicroservice implements IWebRestMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Web/REST";

    /** Web/REST configuration file name */
    private static final String WEB_REST_CONFIGURATION = "web-rest.xml";

    /** List of configuration paths required by microservice */
    private static final String[] CONFIGURATION_PATHS = { WEB_REST_CONFIGURATION };

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#getConfigurationPaths(
     * )
     */
    @Override
    public String[] getConfigurationPaths() throws SiteWhereException {
	return CONFIGURATION_PATHS;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IGlobalMicroservice#
     * initializeFromSpringContexts(org.springframework.context.
     * ApplicationContext, java.util.Map)
     */
    @Override
    public void initializeFromSpringContexts(ApplicationContext global, Map<String, ApplicationContext> contexts)
	    throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.Microservice#afterMicroserviceStarted()
     */
    @Override
    public void afterMicroserviceStarted() {
	try {
	    LOGGER.info("Web/REST microservice started!!!!");
	    ITokenManagement tokens = WebRestApplication.getWebRestMicroservice().getTokenManagement();
	    User user = new User();
	    user.setUsername("dadams");
	    user.setAuthorities(Arrays.asList(new String[] { "this", "that", "other" }));
	    String token = tokens.generateToken(user);
	    LOGGER.info("Generated token '" + token + "' for '" + user.getUsername() + "'.");
	    String username = tokens.getUsernameFromToken(token);
	    LOGGER.info("Decoded username: " + username);
	    List<String> auths = tokens.getGrantedAuthoritiesFromToken(token);
	    LOGGER.info("Decoded auths: " + auths);
	} catch (SiteWhereException e) {
	    LOGGER.error("Token tests failed.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceInitialize
     * (com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStart(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IGlobalMicroservice#microserviceStop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }
}