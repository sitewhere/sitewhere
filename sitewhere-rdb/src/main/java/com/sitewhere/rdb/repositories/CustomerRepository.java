package com.sitewhere.rdb.repositories;

import com.sitewhere.rdb.entities.Customer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public interface CustomerRepository extends CrudRepository<Customer, UUID>, PagingAndSortingRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByToken(String token);
}
