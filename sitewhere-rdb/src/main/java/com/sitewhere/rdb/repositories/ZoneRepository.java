package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.Customer;
import com.sitewhere.rdb.entities.DeviceStatus;
import com.sitewhere.rdb.entities.Zone;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface ZoneRepository extends CrudRepository<Zone, UUID>, JpaSpecificationExecutor<Customer> {

    Optional<Zone> findByToken(String token);

    List<Zone> findAllOrderByCreatedDateDesc(Specification<Zone> spec);
}
