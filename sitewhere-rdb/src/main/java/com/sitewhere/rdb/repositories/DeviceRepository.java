package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.Device;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface DeviceRepository extends CrudRepository<Device, UUID>, JpaSpecificationExecutor<Device> {

    Optional<Device> findByToken(String token);

    List<Device> findAllOrderByCreatedDateDesc(Specification<Device> spec);
}
