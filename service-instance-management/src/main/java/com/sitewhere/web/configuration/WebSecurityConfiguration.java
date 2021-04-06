/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.web.configuration;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.sitewhere.spi.web.ISiteWhereWebConstants;
import com.sitewhere.web.security.SiteWhereAuthenticationProvider;
import com.sitewhere.web.security.TokenAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = false)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    /** Matcher for all API methods */
    private static final String ALL_APIS = "/api/**";

    /** Matcher for all symbols such as QR codes */
    private static final String ALL_APIS_WITH_SYMBOLS = "/api/**/symbol";

    /** Matcher for all authentication API methods */
    private static final String ALL_AUTH_APIS = "/authapi/**";

    @Order(1)
    @Configuration
    public static class AuthWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    // Enable CORS and disable CSRF.
	    http = http.cors().and().csrf().disable();

	    // Use stateless approach.
	    http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();

	    // Use basic auth to establish JWT.
	    http.antMatcher(ALL_AUTH_APIS).authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll().anyRequest()
		    .authenticated().and().httpBasic();
	}
    }

    @Order(2)
    @Configuration
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    // Enable CORS and disable CSRF.
	    http = http.cors().and().csrf().disable();

	    // Use stateless approach.
	    http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();

	    // Allow options and symbols, but authenticate everything else.
	    http.antMatcher(ALL_APIS).authorizeRequests().antMatchers(HttpMethod.OPTIONS).permitAll()
		    .antMatchers(HttpMethod.GET, ALL_APIS_WITH_SYMBOLS).permitAll().anyRequest().authenticated();

	    // Look for JWT bearer tokens to establish identity.
	    http.addFilterBefore(tokenAuthenticationFilter(), BasicAuthenticationFilter.class);
	}

	@Bean
	protected TokenAuthenticationFilter tokenAuthenticationFilter() throws Exception {
	    return new TokenAuthenticationFilter();
	}
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    protected SiteWhereAuthenticationProvider authenticationProvider() throws Exception {
	return new SiteWhereAuthenticationProvider();
    }

    @Bean
    public CorsFilter corsFilter() {
	UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	CorsConfiguration config = new CorsConfiguration();
	config.addAllowedOrigin("*");
	config.addAllowedHeader("*");
	config.addAllowedMethod("*");
	config.setExposedHeaders(new ArrayList<>());
	config.getExposedHeaders().add(ISiteWhereWebConstants.HEADER_JWT);
	config.getExposedHeaders().add(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR);
	config.getExposedHeaders().add(ISiteWhereWebConstants.HEADER_SITEWHERE_ERROR_CODE);
	source.registerCorsConfiguration("/**", config);
	return new CorsFilter(source);
    }
}