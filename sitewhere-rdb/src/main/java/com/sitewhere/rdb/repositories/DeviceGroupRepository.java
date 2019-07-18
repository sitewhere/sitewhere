package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceGroup;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface DeviceGroupRepository extends CrudRepository<DeviceGroup, UUID>, JpaSpecificationExecutor<DeviceGroup> {

    Optional<DeviceGroup> findByToken(String token);

    List<DeviceGroup> findAllOrderByCreatedDateDesc();

    List<DeviceGroup> findAllOrderByCreatedDateDesc(Specification<DeviceGroup> spec);

    List<DeviceGroup> findAllBy();
}
