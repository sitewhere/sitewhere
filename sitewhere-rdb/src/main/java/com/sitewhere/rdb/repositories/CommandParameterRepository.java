package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.CommandParameter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface CommandParameterRepository extends CrudRepository<CommandParameter, UUID> {
}
