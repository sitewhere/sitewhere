package com.sitewhere.device.persistence.rdb;

import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import com.sitewhere.rdb.DbClient;

public class DeviceManagementRDBClient extends DbClient {
    /**
     * @param configuration
     */
    public DeviceManagementRDBClient(RDBConfiguration configuration) {
        super(configuration);
    }
}
