package com.sitewhere.communication.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.sitewhere.spi.server.lifecycle.ILifecycleComponent;

/**
 * Wraps Hazelcast instance configuration.
 * 
 * @author Derek
 */
public interface IHazelcastConfiguration extends ILifecycleComponent {

    /**
     * Get Hazelcast instance for this node.
     * 
     * @return
     */
    public HazelcastInstance getHazelcastInstance();
}
