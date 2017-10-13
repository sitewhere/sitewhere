package com.sitewhere.microservice.grpc;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.microservice.spi.grpc.IManagedGrpcServer;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Base class for GRPC servers used by microservices.
 * 
 * @author Derek
 */
public class ManagedGrpcServer extends TenantLifecycleComponent implements IManagedGrpcServer {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Port for GRPC server */
    private int port;

    /** Wrapped GRPC server */
    private Server server;

    /** Parent microservice */
    private IMicroservice microservice;

    /** Service implementation */
    private BindableService serviceImplementation;

    /** Interceptor for JWT authentication */
    private JwtServerInterceptor jwt;

    public ManagedGrpcServer(IMicroservice microservice, BindableService serviceImplementation) {
	this(microservice, serviceImplementation, microservice.getInstanceSettings().getGrpcPort());
    }

    public ManagedGrpcServer(IMicroservice microservice, BindableService serviceImplementation, int port) {
	this.microservice = microservice;
	this.serviceImplementation = serviceImplementation;
	this.port = port;
	this.jwt = new JwtServerInterceptor(microservice, serviceImplementation.getClass());
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
	    this.server = builder.addService(getServiceImplementation()).intercept(jwt).build();
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
     * @see
     * com.sitewhere.microservice.spi.grpc.IManagedGrpcServer#getMicroservice()
     */
    @Override
    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.grpc.IManagedGrpcServer#
     * getServiceImplementation()
     */
    @Override
    public BindableService getServiceImplementation() {
	return serviceImplementation;
    }

    public void setServiceImplementation(BindableService serviceImplementation) {
	this.serviceImplementation = serviceImplementation;
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