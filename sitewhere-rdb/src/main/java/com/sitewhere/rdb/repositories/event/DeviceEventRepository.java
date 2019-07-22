package com.sitewhere.rdb.repositories.event;

import com.sitewhere.rdb.entities.event.DeviceEvent;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public interface DeviceEventRepository extends CrudRepository<DeviceEvent, UUID>, JpaSpecificationExecutor<DeviceEvent> {

    Optional<DeviceEvent> findByAlternateId(String alternateId);
}
