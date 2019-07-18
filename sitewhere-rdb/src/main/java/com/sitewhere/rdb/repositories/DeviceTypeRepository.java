package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.DeviceType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface DeviceTypeRepository extends CrudRepository<DeviceType, UUID> {

    Optional<DeviceType> findByToken(String token);

    List<DeviceType> findAllOrderByCreatedDateDesc();


}
