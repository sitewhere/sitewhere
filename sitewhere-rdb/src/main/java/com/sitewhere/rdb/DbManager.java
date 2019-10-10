/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb;

import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import com.sitewhere.rdb.entities.Device;
import com.sitewhere.rdb.entities.Location;
import com.sitewhere.rdb.multitenancy.DvdRentalTenantContext;
import com.sitewhere.rdb.multitenancy.MultiTenantDvdRentalProperties;
import com.sitewhere.rdb.repositories.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;

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

    public static void main(String[] args) {
        // Define two datasources
        RDBConfiguration config1 = new RDBConfiguration();
        config1.setUrl("jdbc:postgresql://localhost:5432/tenant1");
        config1.setUsername("syncope");
        config1.setPassword("syncope");
        config1.setDriver("org.postgresql.Driver");

        Location a = new Location();
        a.getId();
       /* RDBConfiguration config2 = new RDBConfiguration();
        config2.setUrl("jdbc:postgresql://114.116.1.182:5432/tenancy_2");
        config2.setUsername("MultiTenancy");
        config2.setPassword("123456");
        config2.setDriver("org.postgresql.Driver");*/

        // Register these two datasources to rdb
        MultiTenantDvdRentalProperties.ADD_NEW_DATASOURCE(config1, "tenant1");
        //MultiTenantDvdRentalProperties.ADD_NEW_DATASOURCE(config2, "tenancy_2");

        // Set current tenantId
        DvdRentalTenantContext.setTenantId("tenant1");
        DbManager manager = new DbManager();
        manager.start();
        // Operate database
        Device devcie = new Device();
        devcie.setComments("iphone6");
        devcie.setCreatedDate(new Date());
        devcie.setStatus("hello6");
        manager.getDeviceRepository().save(devcie);

//        DvdRentalTenantContext.setTenantId("tenancy_2");
//        Device devcie2 = new Device();
//        devcie2.setComments("iphone8");
//        devcie2.setCreatedDate(new Date());
//        devcie2.setStatus("hello8");
//        manager.getDeviceRepository().deleteAll();
    }
}
