package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.CustomerType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public interface CustomerTypeRepository extends CrudRepository<CustomerType, UUID>, PagingAndSortingRepository<CustomerType, UUID>, JpaSpecificationExecutor<CustomerType> {

    Optional<CustomerType> findByToken(String token);
}
