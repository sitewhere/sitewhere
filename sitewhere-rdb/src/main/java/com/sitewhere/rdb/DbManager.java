package com.sitewhere.rdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import com.sitewhere.rdb.entities.*;
import com.sitewhere.rdb.repositories.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;

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


    public static void main(String[] args) {
        RDBConfiguration config = new RDBConfiguration();
        config.setUrl("jdbc:postgresql://114.116.1.182:5432/tenant1");
        config.setDriver("org.postgresql.Driver");
        config.setUsername("DL");
        config.setPassword("123456");
        config.setDialect("org.hibernate.dialect.PostgreSQL94Dialect");
        config.setHbm2ddlAuto("update");
        config.setFormatSql("true");
        config.setShowSql("true");

        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = mapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
        }

        System.out.println("jsonStr="+jsonStr);

        DbManager manager = new DbManager(jsonStr);
        manager.start();
        /// ============================== started


//        DeviceTypeRepository bean = manager.getDeviceTypeRepository();
//        System.out.println("=================== "+bean);
//        DeviceType dt = new DeviceType();
//        dt.setDescription("555");
//        bean.save(dt);

//        AreaRepository bean = manager.getAreaRepository();
//        LocationRepository bean2 = manager.getLocationRepository();
//        Location local1 = new Location();
//        Location local2 = new Location();
//        bean2.save(local1);
//        bean2.save(local2);
//        List<Location> lst = new ArrayList<>();
//        lst.add(local1);
//        lst.add(local2);
//        Area area = new Area();
//        area.setDescription("123123123");
//        area.setBounds(lst);
//        bean.save(area);

//        AreaTypeRepository bean = manager.getAreaTypeRepository();
//        AreaType at = new AreaType();
//        at.setCreatedBy("ddd");
//        at.setDescription("dsdsdsds");
//        List lst = new ArrayList<>();
//        lst.add(UUID.randomUUID());
//        lst.add(UUID.randomUUID());
//        at.setContainedAreaTypeIds(lst);
//        bean.save(at);

        AreaRepository bean = manager.getAreaRepository();
        Optional<Area> opt = bean.findByToken("aaaaa");
        System.out.println("=============result===="+opt.get().getId());

//============================== stop
        manager.stop();
    }
}
