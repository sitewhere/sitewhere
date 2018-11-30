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
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Configures Spring Security for authentication services.
 * 
 * @author Derek
 */
@Order(1)
@Configuration
@EnableWebSecurity
public class AuthApiSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    protected IWebRestMicroservice<?> webRestMicroservice;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.config.annotation.web.configuration.
     * WebSecurityConfigurerAdapter#configure(org.springframework.security. config.
     * annotation.authentication.builders.AuthenticationManagerBuilder)
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.authenticationProvider(sitewhereAuthenticationProvider());
    }

    /**
     * Build {@link AuthenticationProvider} for basic authentication.
     * 
     * @return
     */
    @Bean
    protected SiteWhereAuthenticationProvider sitewhereAuthenticationProvider() {
	return new SiteWhereAuthenticationProvider(getWebRestMicroservice());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.config.annotation.web.configuration.
     * WebSecurityConfigurerAdapter#configure(org.springframework.security. config.
     * annotation.web.builders.HttpSecurity)
     */
    protected void configure(HttpSecurity http) throws Exception {
	http.csrf().disable();
	http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	http.antMatcher("/authapi/**").authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/authapi/**").permitAll()
		.antMatchers(HttpMethod.GET, "/authapi/v2/**").permitAll().antMatchers("/authapi/**").authenticated()
		.and().httpBasic();
    }

    public IWebRestMicroservice<?> getWebRestMicroservice() {
	return webRestMicroservice;
    }

    public void setWebRestMicroservice(IWebRestMicroservice<?> webRestMicroservice) {
	this.webRestMicroservice = webRestMicroservice;
    }
}