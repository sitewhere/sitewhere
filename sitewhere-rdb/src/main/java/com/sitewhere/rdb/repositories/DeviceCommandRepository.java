package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceCommand;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface DeviceCommandRepository extends CrudRepository<DeviceCommand, UUID>, JpaSpecificationExecutor<DeviceCommand> {

    Optional<DeviceCommand> findByToken(String token);

    List<DeviceCommand> findAllOrderByName(Specification<DeviceCommand> spec);
}
