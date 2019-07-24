package com.sitewhere.rdb;

import com.sitewhere.configuration.instance.rdb.RDBConfiguration;
import com.sitewhere.rdb.entities.event.DeviceAlert;
import com.sitewhere.rdb.multitenancy.MapMultiTenantConnectionProviderImpl;
import com.sitewhere.rdb.multitenancy.TenantContext;
import com.sitewhere.spi.device.event.DeviceEventType;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;

/**
 * DbManager testcase
 *
 * Simeon Chen
 */
public class Test {

    private DbManager manager = new DbManager();

    @Before
    public void setup() throws IOException {
        manager.start();
        TenantContext.setCurrentTenant("tenancy_4");
        MapMultiTenantConnectionProviderImpl provider = manager.getMapMultiTenantConnectionProvider();
        RDBConfiguration config = new RDBConfiguration();
        config.setUrl("jdbc:postgresql://114.116.1.182:5432/tenancy_4");
        config.setDriver("org.postgresql.Driver");
        config.setUsername("MultiTenancy");
        config.setPassword("123456");
        config.setDialect("org.hibernate.dialect.PostgreSQL94Dialect");
        config.setHbm2ddlAuto("update");
        config.setFormatSql("true");
        config.setShowSql("true");
        provider.registerTenantConnectionProvider(TenantContext.getCurrentTenant(), config);
    }

    @org.junit.Test
    public void testSave() {
        /// ============================== started
        DeviceAlert da = new DeviceAlert();
        da.setEventType(DeviceEventType.CommandInvocation);
        da = manager.getDeviceAlertRepository().save(da);
        Assert.assertNotNull(da.getId());
    }

    @org.junit.Test
    public void testFindAll() {
        /// ============================== started
        Sort sort = new Sort(Sort.Direction.DESC,"eventType");
        Specification<DeviceAlert> specification = new Specification<DeviceAlert>() {
            @Override
            public Predicate toPredicate(Root<DeviceAlert> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        };
        Page<DeviceAlert> page  = manager.getDeviceAlertRepository().findAll(specification, PageRequest.of(0, 20, sort));
        for(DeviceAlert da : page.getContent()) {
            System.out.println(da.getId()+" "+da.getEventType());
        }
        Assert.assertNotEquals(0, page.getContent().size());
    }
}
