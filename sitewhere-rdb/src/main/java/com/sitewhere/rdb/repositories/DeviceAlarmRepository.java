package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceAlarm;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface DeviceAlarmRepository extends CrudRepository<DeviceAlarm, UUID>, JpaSpecificationExecutor<DeviceAlarm> {
}
