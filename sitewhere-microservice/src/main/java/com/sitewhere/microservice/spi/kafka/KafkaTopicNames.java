package com.sitewhere.microservice.spi.kafka;

/**
 * Class for locating SiteWhere Kafka topics.
 * 
 * @author Derek
 */
public class KafkaTopicNames {

    /** Separator used to partition topic name */
    protected static final String SEPARATOR = ".";

    /** Base name prepended to all SiteWhere topics */
    protected static final String BASE_NAME = "sitewhere";

    /** Global topic identifier */
    protected static final String GLOBAL_TOPIC_PREFIX = BASE_NAME + SEPARATOR + "global" + SEPARATOR;

    /** Tenant topic identifier */
    protected static final String TENANT_TOPIC_PREFIX = BASE_NAME + SEPARATOR + "tenant" + SEPARATOR;

    /** Topic for global model updates (changes to tenant/users) */
    protected static final String TOPIC_GLOBAL_MODEL_UPDATES = GLOBAL_TOPIC_PREFIX + "modelupdates";

    /**
     * Get topic name for listening to update notifications for global model
     * objects such as tenants and users.
     * 
     * @return
     */
    public static String getGlobalModelUpdatesTopicName() {
	return TOPIC_GLOBAL_MODEL_UPDATES;
    }
}