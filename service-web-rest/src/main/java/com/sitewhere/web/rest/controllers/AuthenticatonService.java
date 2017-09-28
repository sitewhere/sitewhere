package com.sitewhere.web.rest.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.microservice.spi.security.ITokenManagement;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.SiteWhereAuthority;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.security.jwt.TokenAuthenticationFilter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Controller for security operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/authentication")
@Api(value = "authentication")
public class AuthenticatonService extends RestController {

    /** Injected reference to token management */
    @Autowired
    ITokenManagement tokenManagement;

    /**
     * Use basic authentication information to generate a JWT and return it as a
     * header in the servlet response. This is the only method that allows basic
     * authentication. All others expect the JWT in the Authorization header.
     * 
     * @param servletRequest
     * @param servletResponse
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/jwt", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Authenticate and receive a JWT")
    public ResponseEntity<?> jwt(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
	    throws SiteWhereException {
	checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.REST, true);

	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	if (auth == null) {
	    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	} else {
	    IUser user = (IUser) auth.getPrincipal();
	    String jwt = getTokenManagement().generateToken(user);
	    return ResponseEntity.ok().header(TokenAuthenticationFilter.JWT_HEADER, jwt).build();
	}
    }

    /**
     * Get {@link ITokenManagement} implementation.
     * 
     * @return
     */
    protected ITokenManagement getTokenManagement() {
	return tokenManagement;
    }
}