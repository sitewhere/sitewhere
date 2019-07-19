package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceGroupElement;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface DeviceGroupElementRepository extends CrudRepository<DeviceGroupElement, UUID>, JpaSpecificationExecutor<DeviceGroupElement>, PagingAndSortingRepository<DeviceGroupElement, UUID> {
}
