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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.security.jwt.TokenAuthenticationFilter;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Configures Spring Security for REST services.
 * 
 * @author Derek
 */
@Order(2)
@Configuration
@EnableWebSecurity
public class RestApiSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    protected IWebRestMicroservice<?> webRestMicroservice;

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
	return super.authenticationManagerBean();
    }

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

    /**
     * Filters inbound requests to pull JWT header and establish Spring Security
     * context.
     * 
     * @return
     */
    protected TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
	return new TokenAuthenticationFilter(getWebRestMicroservice(), authenticationManagerBean());
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

	http.antMatcher("/api/**").authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
		.antMatchers(HttpMethod.GET, "/api/**/symbol").permitAll().antMatchers(HttpMethod.GET, "/api/v2/**")
		.permitAll().antMatchers("/api/**").hasRole(SiteWhereRoles.AUTH_REST).and()
		.addFilterBefore(tokenAuthenticationFilter(), BasicAuthenticationFilter.class);
    }

    public IWebRestMicroservice<?> getWebRestMicroservice() {
	return webRestMicroservice;
    }

    public void setWebRestMicroservice(IWebRestMicroservice<?> webRestMicroservice) {
	this.webRestMicroservice = webRestMicroservice;
    }
}