package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public interface DeviceTypeRepository extends CrudRepository<DeviceType, UUID> , JpaSpecificationExecutor<DeviceType>, PagingAndSortingRepository<DeviceType,UUID> {

    Optional<DeviceType> findByToken(String token);
}
