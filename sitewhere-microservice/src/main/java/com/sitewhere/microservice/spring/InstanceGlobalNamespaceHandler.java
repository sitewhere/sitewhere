package com.sitewhere.microservice.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Registers parsers for elements in the instance global configuration schema.
 * 
 * @author Derek
 */
public class InstanceGlobalNamespaceHandler extends NamespaceHandlerSupport {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    @Override
    public void init() {
	registerBeanDefinitionParser("instance-global", new InstanceGlobalParser());
    }
}