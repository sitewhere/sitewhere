/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb;

import com.sitewhere.rdb.repositories.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * DbManager is a database client for data management
 *
 * Simeon Chen
 */
public class DbManager {

    private ConfigurableApplicationContext context;

    /**
     * Constructor
     */
    public DbManager() {}

    public void start() {
	SpringApplication app = new SpringApplication(Application.class);
	app.addListeners(new MainBusiListeners());
	context = app.run();
    }

    public void stop() {
	if(context != null) {
	    System.exit(SpringApplication.exit(context));
	}
    }

    public DeviceTypeRepository getDeviceTypeRepository() {
	DeviceTypeRepository bean = ApplicationContextUtils.getBean(DeviceTypeRepository.class);
	return bean;
    }

    public AreaRepository getAreaRepository() {
	AreaRepository bean = ApplicationContextUtils.getBean(AreaRepository.class);
	return bean;
    }

    public LocationRepository getLocationRepository() {
	LocationRepository bean = ApplicationContextUtils.getBean(LocationRepository.class);
	return bean;
    }

    public AreaTypeRepository getAreaTypeRepository() {
	AreaTypeRepository bean = ApplicationContextUtils.getBean(AreaTypeRepository.class);
	return bean;
    }

    public CommandParameterRepository getCommandParameterRepository() {
	CommandParameterRepository bean = ApplicationContextUtils.getBean(CommandParameterRepository.class);
	return bean;
    }

    public CustomerRepository getCustomerRepository() {
	CustomerRepository bean = ApplicationContextUtils.getBean(CustomerRepository.class);
	return bean;
    }

    public CustomerTypeRepository getCustomerTypeRepository() {
	CustomerTypeRepository bean = ApplicationContextUtils.getBean(CustomerTypeRepository.class);
	return bean;
    }

    public DeviceRepository getDeviceRepository() {
	DeviceRepository bean = ApplicationContextUtils.getBean(DeviceRepository.class);
	return bean;
    }

    public DeviceElementMappingRepository getDeviceElementMappingRepository() {
	DeviceElementMappingRepository bean = ApplicationContextUtils.getBean(DeviceElementMappingRepository.class);
	return bean;
    }

    public DeviceAlarmRepository getDeviceAlarmRepository() {
	DeviceAlarmRepository bean = ApplicationContextUtils.getBean(DeviceAlarmRepository.class);
	return bean;
    }

    public DeviceAssignmentRepository getDeviceAssignmentRepository() {
	DeviceAssignmentRepository bean = ApplicationContextUtils.getBean(DeviceAssignmentRepository.class);
	return bean;
    }

    public DeviceCommandRepository getDeviceCommandRepository() {
	DeviceCommandRepository bean = ApplicationContextUtils.getBean(DeviceCommandRepository.class);
	return bean;
    }

    public DeviceGroupRepository getDeviceGroupRepository() {
	DeviceGroupRepository bean = ApplicationContextUtils.getBean(DeviceGroupRepository.class);
	return bean;
    }

    public DeviceGroupElementRepository getDeviceGroupElementRepository() {
	DeviceGroupElementRepository bean = ApplicationContextUtils.getBean(DeviceGroupElementRepository.class);
	return bean;
    }

    public DeviceStatusRepository getDeviceStatusRepository() {
	DeviceStatusRepository bean = ApplicationContextUtils.getBean(DeviceStatusRepository.class);
	return bean;
    }

    public ZoneRepository getZoneRepository() {
	ZoneRepository bean = ApplicationContextUtils.getBean(ZoneRepository.class);
	return bean;
    }

    public AssetRepository getAssetRepository() {
	AssetRepository bean = ApplicationContextUtils.getBean(AssetRepository.class);
	return bean;
    }

    public AssetTypeRepository getAssetTypeRepository() {
	AssetTypeRepository bean = ApplicationContextUtils.getBean(AssetTypeRepository.class);
	return bean;
    }

    public BatchOperationRepository getBatchOperationRepository() {
	BatchOperationRepository bean = ApplicationContextUtils.getBean(BatchOperationRepository.class);
	return bean;
    }

    public BatchOperationElementRepository getBatchOperationElementRepository() {
	BatchOperationElementRepository bean = ApplicationContextUtils.getBean(BatchOperationElementRepository.class);
	return bean;
    }

    public ScheduleRepository getScheduleRepository() {
	ScheduleRepository bean = ApplicationContextUtils.getBean(ScheduleRepository.class);
	return bean;
    }

    public ScheduledJobRepository getScheduledJobRepository() {
	ScheduledJobRepository bean = ApplicationContextUtils.getBean(ScheduledJobRepository.class);
	return bean;
    }
}
