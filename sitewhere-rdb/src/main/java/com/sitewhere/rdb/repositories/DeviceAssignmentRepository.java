package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceAssignment;
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
public interface DeviceAssignmentRepository extends CrudRepository<DeviceAssignment, UUID>, JpaSpecificationExecutor<DeviceAssignment> {

    Optional<DeviceAssignment> findByToken(String token);

    List<DeviceAssignment> findByDeviceId(UUID deviceId);

    List<DeviceAssignment> findAllOrderByActiveDateDesc(Specification<DeviceAssignment> spec);
}
