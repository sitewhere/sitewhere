/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.security.jwt.TokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SiteWhereSecurity extends WebSecurityConfigurerAdapter {

    @Bean
    public TokenAuthenticationFilter jwtAuthenticationTokenFilter() throws Exception {
        return new TokenAuthenticationFilter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.config.annotation.web.configuration.
     * WebSecurityConfigurerAdapter#configure(org.springframework.security.
     * config. annotation.authentication.builders.AuthenticationManagerBuilder)
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	auth.authenticationProvider(new JwtAuthenticationProvider());
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
	http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().antMatcher("/api/**")
		.authorizeRequests().antMatchers(HttpMethod.GET, "/").permitAll()
		.antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll().antMatchers(HttpMethod.GET, "/api/**/symbol")
		.permitAll().antMatchers("/api/**").hasRole(SiteWhereRoles.AUTH_REST).and().httpBasic();
    }
}
