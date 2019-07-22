package com.sitewhere.rdb.repositories.event;

import com.sitewhere.rdb.entities.event.DeviceStateChange;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface DeviceStateChangeRepository extends CrudRepository<DeviceStateChange, UUID>, JpaSpecificationExecutor<DeviceStateChange> {
}
