package com.sitewhere.web.vue;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class VueConfiguration extends WebMvcConfigurationSupport {

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.config.annotation.
     * WebMvcConfigurationSupport#configureContentNegotiation(org.
     * springframework.web.servlet.config.annotation.
     * ContentNegotiationConfigurer)
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	configurer.favorPathExtension(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.config.annotation.
     * WebMvcConfigurationSupport#addResourceHandlers(org.springframework.web.
     * servlet.config.annotation.ResourceHandlerRegistry)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	registry.addResourceHandler("/static/**").addResourceLocations("/vueadmin/static/");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.web.servlet.config.annotation.
     * WebMvcConfigurationSupport#addViewControllers(org.springframework.web.
     * servlet.config.annotation.ViewControllerRegistry)
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
	registry.addViewController("/").setViewName("forward:/vueadmin/index.html");
    }
}