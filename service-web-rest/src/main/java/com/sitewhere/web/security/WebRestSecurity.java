/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.sitewhere.microservice.spi.MicroserviceNotAvailableException;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.security.basic.BasicAuthenticationProvider;
import com.sitewhere.web.security.jwt.JwtAuthenticationProvider;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Configures Spring Security for web/REST.
 * 
 * @author Derek
 */
@Configuration
@EnableWebSecurity
public class WebRestSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    protected IWebRestMicroservice webRestMicroservice;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.config.annotation.web.configuration.
     * WebSecurityConfigurerAdapter#configure(org.springframework.security.
     * config. annotation.authentication.builders.AuthenticationManagerBuilder)
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.authenticationProvider(jwtAuthenticationProvider()).authenticationProvider(basicAuthenticationProvider());
    }

    /**
     * Build {@link AuthenticationProvider} for JWT authentication.
     * 
     * @return
     */
    @Bean
    protected JwtAuthenticationProvider jwtAuthenticationProvider() {
	return new JwtAuthenticationProvider();
    }

    /**
     * Build {@link AuthenticationProvider} for basic authentication.
     * 
     * @return
     * @throws MicroserviceNotAvailableException
     */
    @Bean
    protected BasicAuthenticationProvider basicAuthenticationProvider() throws MicroserviceNotAvailableException {
	IUserManagement userManagement = getWebRestMicroservice().getUserManagement();
	return new BasicAuthenticationProvider(userManagement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.config.annotation.web.configuration.
     * WebSecurityConfigurerAdapter#configure(org.springframework.security.
     * config. annotation.web.builders.HttpSecurity)
     */
    protected void configure(HttpSecurity http) throws Exception {
	http.csrf().disable();
	http.anonymous().disable();
	http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	http.antMatcher("/api/**").authorizeRequests().antMatchers(HttpMethod.GET, "/").permitAll()
		.antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll().antMatchers(HttpMethod.GET, "/api/**/symbol")
		.permitAll().antMatchers("/api/**").hasRole(SiteWhereRoles.AUTH_REST).and().httpBasic();
    }

    public IWebRestMicroservice getWebRestMicroservice() {
	return webRestMicroservice;
    }

    public void setWebRestMicroservice(IWebRestMicroservice webRestMicroservice) {
	this.webRestMicroservice = webRestMicroservice;
    }
}