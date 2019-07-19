package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.Zone;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public interface ZoneRepository extends CrudRepository<Zone, UUID>, JpaSpecificationExecutor<Zone>, PagingAndSortingRepository<Zone, UUID> {

    Optional<Zone> findByToken(String token);
}
