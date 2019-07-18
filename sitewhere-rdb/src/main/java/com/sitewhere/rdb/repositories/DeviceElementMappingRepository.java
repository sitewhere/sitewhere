package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceElementMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface DeviceElementMappingRepository extends CrudRepository<DeviceElementMapping, UUID> {
}
