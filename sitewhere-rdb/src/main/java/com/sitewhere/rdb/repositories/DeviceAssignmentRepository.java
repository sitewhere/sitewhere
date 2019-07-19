package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceAssignment;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public interface DeviceAssignmentRepository extends CrudRepository<DeviceAssignment, UUID>, JpaSpecificationExecutor<DeviceAssignment> {

    Optional<DeviceAssignment> findByToken(String token);
}
