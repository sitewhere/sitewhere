package com.sitewhere.web.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Registers parsers for elements in the web/REST configuration schema.
 * 
 * @author Derek
 */
public class WebRestNamespaceHandler extends NamespaceHandlerSupport {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.beans.factory.xml.NamespaceHandler#init()
     */
    @Override
    public void init() {
	registerBeanDefinitionParser("web-rest", new WebRestParser());
    }
}