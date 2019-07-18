package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface LocationRepository extends CrudRepository<Location, UUID> {
}
