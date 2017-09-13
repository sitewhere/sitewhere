package com.sitewhere.microservice.grpc;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.MicroserviceEnvironment;
import com.sitewhere.microservice.spi.grpc.IManagedGrpcServer;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Base class for GRPC servers used by microservices.
 * 
 * @author Derek
 */
public abstract class ManagedGrpcServer extends LifecycleComponent implements IManagedGrpcServer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Port for GRPC server */
    private int port;

    /** Wrapped GRPC server */
    private Server server;

    public ManagedGrpcServer() {
	this(getDefaultPortOrOverride());
    }

    public ManagedGrpcServer(int port) {
	this.port = port;
    }

    /**
     * Check environment variable for SiteWhere instance id.
     */
    protected static int getDefaultPortOrOverride() {
	String envPortOverride = System.getenv().get(MicroserviceEnvironment.ENV_GRPC_PORT_OVERRIDE);
	if (envPortOverride != null) {
	    LOGGER.info("GRPC port overridden using " + MicroserviceEnvironment.ENV_GRPC_PORT_OVERRIDE + ": "
		    + envPortOverride);
	    return Integer.parseInt(envPortOverride);
	}
	return MicroserviceEnvironment.DEFAULT_GRPC_PORT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    ServerBuilder<?> builder = ServerBuilder.forPort(port);
	    this.server = builder.addService(getServiceImplementation()).build();
	} catch (Throwable e) {
	    throw new SiteWhereException("Unable to initialize tenant management GRPC server.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    getServer().start();
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to start tenant management GRPC server.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getServer().shutdown();
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

    public Server getServer() {
	return server;
    }

    public void setServer(Server server) {
	this.server = server;
    }
}
