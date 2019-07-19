package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.AreaType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public interface AreaTypeRepository extends CrudRepository<AreaType, UUID>, PagingAndSortingRepository<AreaType, UUID>, JpaSpecificationExecutor<AreaType> {

    Optional<AreaType> findByToken(String token);
}
