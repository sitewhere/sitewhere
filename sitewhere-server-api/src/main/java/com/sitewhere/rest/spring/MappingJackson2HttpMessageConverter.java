package com.sitewhere.rest.spring;

/**
 * Hack to use converter on local classpath since Jackson classes do not want to
 * resolve otherwise.
 * 
 * @author Derek
 */
public class MappingJackson2HttpMessageConverter
	extends org.springframework.http.converter.json.MappingJackson2HttpMessageConverter {
}