package com.sitewhere.rdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import com.sitewhere.rdb.entities.*;
import com.sitewhere.rdb.repositories.*;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.search.ISearchCriteria;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

public class DbManager {

    private String paramInJsonFormat;
    private ConfigurableApplicationContext context;

    public DbManager(String paramInJsonFormat) {
        this.paramInJsonFormat = paramInJsonFormat;
    }

    public void start() {
        SpringApplication app = new SpringApplication(Application.class);
        app.addListeners(new MainBusiListeners());
        context = app.run( paramInJsonFormat);
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
}
