package com.sitewhere.spi.configuration;

/**
 * Static constants for default resource paths.
 * 
 * @author Derek
 */
public interface IDefaultResourcePaths {

    /** File name for SiteWhere global configuration file */
    public static final String GLOBAL_CONFIG_FILE_NAME = "sitewhere-server.xml";

    /** File name for SiteWhere state information in JSON format */
    public static final String STATE_FILE_NAME = "sitewhere-state.json";

    /** Folder containing global resources */
    public static final String GLOBAL_FOLDER_NAME = "global";

    /** Folder containing tenant resources */
    public static final String TENANTS_FOLDER_NAME = "tenants";

    /** Folder containing tenant template resources */
    public static final String TEMPLATES_FOLDER_NAME = "templates";

    /** Folder containing tenant asset resources */
    public static final String ASSETS_FOLDER = "assets";

    /** Folder containing tenant script resources */
    public static final String SCRIPTS_FOLDER = "scripts";

    /** File name for template descriptor */
    public static final String TEMPLATE_JSON_FILE_NAME = "template.json";

    /** Template that does not load any device data */
    public static final String EMPTY_TEMPLATE_NAME = "empty";
}