package com.sitewhere.tenant.kafka;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitewhere.grpc.kafka.model.KafkaModel.GTenantModelUpdate;
import com.sitewhere.grpc.kafka.model.KafkaModel.GTenantModelUpdateType;
import com.sitewhere.grpc.model.converter.KafkaModelConverter;
import com.sitewhere.grpc.model.converter.TenantModelConverter;
import com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer;
import com.sitewhere.microservice.spi.instance.IInstanceSettings;
import com.sitewhere.microservice.spi.kafka.IKafkaTopicNaming;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.tenant.spi.kafka.ITenantBootstrapModelConsumer;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;

/**
 * Kafka consumer that listens to the tenant model updates topic and bootstraps
 * newly added tenants.
 * 
 * @author Derek
 */
public class TenantBootstrapModelConsumer extends MicroserviceKafkaConsumer implements ITenantBootstrapModelConsumer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Consumer id */
    private static String CONSUMER_ID = UUID.randomUUID().toString();

    /** Suffix for group id */
    private static String GROUP_ID_SUFFIX = ".tenant-bootstrap-consumers";

    /** Number of threads bootstrapping tenants before queued */
    private static final int CONCURRENT_TENANT_BOOTSTRAP_THREADS = 3;

    /** Injected reference to instance settings */
    @Autowired
    private IInstanceSettings instanceSettings;

    /** Injected reference to Kafka topic naming */
    @Autowired
    private IKafkaTopicNaming kafkaTopicNaming;

    /** Injected reference to microservice */
    @Autowired
    private ITenantManagementMicroservice microservice;

    /** Executor */
    private ExecutorService executor;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getConsumerId()
     */
    @Override
    public String getConsumerId() throws SiteWhereException {
	return CONSUMER_ID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getConsumerGroupId()
     */
    @Override
    public String getConsumerGroupId() throws SiteWhereException {
	return getKafkaTopicNaming().getInstancePrefix() + GROUP_ID_SUFFIX;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getSourceTopicName()
     */
    @Override
    public String getSourceTopicName() throws SiteWhereException {
	return getKafkaTopicNaming().getTenantUpdatesTopic();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#start(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	executor = Executors.newFixedThreadPool(CONCURRENT_TENANT_BOOTSTRAP_THREADS,
		new TenantBootstrapperThreadFactory());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.kafka.MicroserviceKafkaConsumer#stop(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);
	if (executor != null) {
	    executor.shutdown();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#received(
     * java.lang.String, byte[])
     */
    @Override
    public void received(String key, byte[] message) throws SiteWhereException {
	GTenantModelUpdate update = KafkaModelConverter.parseTenantModelUpdateMessage(message);

	// If a tenant was added, bootstrap it.
	if (update.getType() == GTenantModelUpdateType.TENANTMODEL_TENANT_ADDED) {
	    ITenant tenant = TenantModelConverter.asApiTenant(update.getTenant());
	    executor.execute(new TenantBootstrapper(tenant));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.kafka.IMicroserviceKafkaConsumer#
     * getInstanceSettings()
     */
    @Override
    public IInstanceSettings getInstanceSettings() {
	return instanceSettings;
    }

    protected void setInstanceSettings(IInstanceSettings instanceSettings) {
	this.instanceSettings = instanceSettings;
    }

    protected IKafkaTopicNaming getKafkaTopicNaming() {
	return kafkaTopicNaming;
    }

    protected void setKafkaTopicNaming(IKafkaTopicNaming kafkaTopicNaming) {
	this.kafkaTopicNaming = kafkaTopicNaming;
    }

    public ITenantManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(ITenantManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    /**
     * Thread that takes care of bootstrapping a tenant based on its template.
     * 
     * @author Derek
     *
     */
    private class TenantBootstrapper implements Runnable {

	/** Tenant to bootstrap */
	private ITenant tenant;

	public TenantBootstrapper(ITenant tenant) {
	    this.tenant = tenant;
	}

	@Override
	public void run() {
	    try {
		CuratorFramework curator = getMicroservice().getZookeeperManager().getCurator();
		createTenantsConfigurationRootIfNotFound(curator);
		createTenantConfigurationIfNotFound(curator);
	    } catch (SiteWhereException e) {
		LOGGER.error("Unable to bootstrap tenant.", e);
	    } catch (Throwable e) {
		LOGGER.error("Unhandled exception while bootstrapping tenant.", e);
	    }
	}

	/**
	 * Verify that instance tenants configuration node has been created.
	 * 
	 * @param curator
	 * @throws Exception
	 */
	protected void createTenantsConfigurationRootIfNotFound(CuratorFramework curator) throws Exception {
	    Stat existing = curator.checkExists().forPath(getMicroservice().getInstanceTenantsConfigurationPath());
	    if (existing == null) {
		LOGGER.info("Zk node for tenant configurations not found. Creating...");
		curator.create().forPath(getMicroservice().getInstanceTenantsConfigurationPath());
		LOGGER.info("Created tenant configurations Zk node.");
	    } else {
		LOGGER.info("Found Zk node for tenant configurations.");
	    }
	}

	/**
	 * Verify that tenant configuration node has been created.
	 * 
	 * @param curator
	 * @throws Exception
	 */
	protected void createTenantConfigurationIfNotFound(CuratorFramework curator) throws Exception {
	    String tenantPath = getMicroservice().getInstanceTenantConfigurationPath(getTenant().getId());
	    Stat existing = curator.checkExists().forPath(tenantPath);
	    if (existing == null) {
		LOGGER.info("Zk node for tenant '" + getTenant().getName() + "' configuration not found. Creating...");
		curator.create().forPath(tenantPath);
		LOGGER.info("Copying tenant template contents into Zk node...");
		getMicroservice().getTenantTemplateManager().copyTemplateContentsToZk(getTenant().getTenantTemplateId(),
			curator, tenantPath);
		curator.create()
			.forPath(getMicroservice().getInstanceTenantBootstrappedIndicatorPath(getTenant().getId()));
		LOGGER.info("Tenant '" + getTenant().getName() + "' bootstrapped with template data.");
	    } else {
		LOGGER.info("Found Zk node for tenant '" + getTenant().getName() + "'.");
	    }
	}

	public ITenant getTenant() {
	    return tenant;
	}
    }

    /** Used for naming tenant bootstrapper threads */
    private class TenantBootstrapperThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "Tenant Bootstrapper " + counter.incrementAndGet());
	}
    }
}