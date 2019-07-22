package com.sitewhere.rdb.repositories.event;

import com.sitewhere.rdb.entities.event.DeviceMeasurement;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface DeviceMeasurementRepository extends CrudRepository<DeviceMeasurement, UUID>, JpaSpecificationExecutor<DeviceMeasurement>, PagingAndSortingRepository<DeviceMeasurement, UUID> {
}
