package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceGroup;
import com.sitewhere.rdb.entities.DeviceGroupElement;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface DeviceGroupElementRepository extends CrudRepository<DeviceGroupElement, UUID>, JpaSpecificationExecutor<DeviceGroup> {

    List<DeviceGroupElement> findList(Specification<DeviceGroupElement> spec);

    List<DeviceGroupElement> findAllById(UUID id);
}
