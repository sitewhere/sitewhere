package com.sitewhere.hazelcast;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hazelcast.core.HazelcastInstance;

/**
 * Wraps a {@link HazelcastInstance} and prevents the shutdown method from being
 * called.
 * 
 * @author Derek
 */
public class NoShutdownHazelcastInstance extends HazelcastInstanceDecorator {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public NoShutdownHazelcastInstance(HazelcastInstance delegate) {
	super(delegate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.hazelcast.HazelcastInstanceDecorator#shutdown()
     */
    @Override
    public void shutdown() {
	LOGGER.info("Ignoring Hazelcast shutdown request from Spring.");
    }
}