package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceCommand;
import com.sitewhere.rdb.entities.DeviceStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface DeviceStatusRepository extends CrudRepository<DeviceStatus, UUID>, JpaSpecificationExecutor<DeviceStatus> {

    Optional<DeviceStatus> findByToken(String token);

    List<DeviceStatus> findAllOrderByName(Specification<DeviceStatus> spec);
}
