package com.sitewhere.rdb.repositories.event;

import com.sitewhere.rdb.entities.event.DeviceAlert;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface DeviceAlertRepository extends CrudRepository<DeviceAlert, UUID>, JpaSpecificationExecutor<DeviceAlert> , PagingAndSortingRepository<DeviceAlert, UUID> {

}
