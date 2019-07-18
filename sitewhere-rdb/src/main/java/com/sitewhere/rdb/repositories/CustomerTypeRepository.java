package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.CustomerType;
import com.sitewhere.rdb.entities.Device;
import com.sitewhere.rdb.entities.DeviceStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface CustomerTypeRepository extends CrudRepository<CustomerType, UUID> {

    Optional<CustomerType> findByToken(String token);

    List<CustomerType> findAllOrderByName();
}
