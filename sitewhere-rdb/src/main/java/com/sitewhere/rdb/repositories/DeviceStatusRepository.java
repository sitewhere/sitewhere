package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceStatus;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public interface DeviceStatusRepository extends CrudRepository<DeviceStatus, UUID>, JpaSpecificationExecutor<DeviceStatus> {

    Optional<DeviceStatus> findByToken(String token);
}
