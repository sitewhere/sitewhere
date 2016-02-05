/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.sitewhere.security.SitewhereAuthenticationProvider;
import com.sitewhere.spi.user.SiteWhereRoles;

/**
 * Configures Spring web application settings for MVC and REST.
 * 
 * @author Derek
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebConfiguration {

	@Autowired
	public void registerSharedAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(new SitewhereAuthenticationProvider());
	}

	@Order(1)
	@Configuration
	@ComponentScan(basePackages = "com.sitewhere.web.rest.controllers")
	@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
	public static class RestConfiguration extends WebSecurityMvcConfigurerAdapter {

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(new SitewhereAuthenticationProvider());
		}

		@Bean(name = "myAuthenticationManager")
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.config.annotation.web.configuration.
		 * WebSecurityConfigurerAdapter#configure(org.springframework.security.config.
		 * annotation.web.builders.HttpSecurity)
		 */
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();
			http.antMatcher("/api/**").authorizeRequests().antMatchers("/api/**").hasRole(
					SiteWhereRoles.AUTH_REST).and().httpBasic();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sitewhere.web.WebSecurityMvcConfigurerAdapter#configureContentNegotiation(
		 * org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer)
		 */
		@Override
		public void configureContentNegotiation(ContentNegotiationConfigurer content) {
			content.favorPathExtension(false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.sitewhere.web.WebSecurityMvcConfigurerAdapter#configureMessageConverters(
		 * java.util.List)
		 */
		@Override
		public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
			converters.add(new MappingJackson2HttpMessageConverter());
		}
	}

	@Configuration
	@ComponentScan(basePackages = "com.sitewhere.web.mvc")
	@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
	public static class MvcConfiguration extends WebSecurityConfigurerAdapter {

		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(new SitewhereAuthenticationProvider());
		}

		@Bean(name = "myAuthenticationManager")
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.config.annotation.web.configuration.
		 * WebSecurityConfigurerAdapter#configure(org.springframework.security.config.
		 * annotation.web.builders.WebSecurity)
		 */
		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers("/css/**", "/fonts/**", "/img/**", "/lib/**", "/locales/**",
					"/scripts/**");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.springframework.security.config.annotation.web.configuration.
		 * WebSecurityConfigurerAdapter#configure(org.springframework.security.config.
		 * annotation.web.builders.HttpSecurity)
		 */
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();
			http.authorizeRequests().antMatchers("/admin", "/admin/",
					"/admin/loginFailed.html").permitAll().antMatchers("/admin/**").hasRole(
							SiteWhereRoles.AUTH_ADMIN_CONSOLE).anyRequest().authenticated();
			http.formLogin().loginPage("/admin/").loginProcessingUrl("/admin/login.html").defaultSuccessUrl(
					"/admin/tenant.html").failureUrl("/admin/loginFailed.html");
			http.logout().logoutSuccessUrl("/admin/");
		}

		/**
		 * Set up the JSP view resolver.
		 * 
		 * @return
		 */
		@Bean
		public UrlBasedViewResolver viewResolver() {
			UrlBasedViewResolver resolver = new UrlBasedViewResolver();
			resolver.setViewClass(JstlView.class);
			resolver.setPrefix("/WEB-INF/jsp/");
			resolver.setSuffix(".jsp");
			return resolver;
		}
	}
}