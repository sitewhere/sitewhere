package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.AreaType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public interface AreaTypeRepository extends CrudRepository<AreaType, UUID> {

    Optional<AreaType> findByToken(String token);

    List<AreaType> findAllOrderByName();
}
