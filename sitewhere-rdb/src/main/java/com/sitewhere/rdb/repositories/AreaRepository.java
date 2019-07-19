package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.Area;
import com.sitewhere.rdb.entities.DeviceCommand;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface AreaRepository extends CrudRepository<Area, UUID>, JpaSpecificationExecutor<Area> {

    Optional<Area> findByToken(String token);
}
