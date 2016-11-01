package com.sitewhere.groovy.asset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.groovy.GroovyConfiguration;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.configuration.ITenantConfigurationResolver;
import com.sitewhere.spi.server.asset.IAssetModelInitializer;

/**
 * Implementation of {@link IAssetModelInitializer} that delegates creation
 * logic to a Groovy script.
 * 
 * @author Derek
 */
public class GroovyAssetModuleInitializer implements IAssetModelInitializer {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Injected Groovy configuration */
    private GroovyConfiguration configuration;

    /** Relative path to Groovy script */
    private String scriptPath;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.server.asset.IAssetModelInitializer#initialize(com.
     * sitewhere.spi.configuration.ITenantConfigurationResolver,
     * com.sitewhere.spi.asset.IAssetManagement)
     */
    @Override
    public void initialize(ITenantConfigurationResolver configuration, IAssetManagement assetManagement)
	    throws SiteWhereException {
    }

    public GroovyConfiguration getConfiguration() {
	return configuration;
    }

    public void setConfiguration(GroovyConfiguration configuration) {
	this.configuration = configuration;
    }

    public String getScriptPath() {
	return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
	this.scriptPath = scriptPath;
    }
}