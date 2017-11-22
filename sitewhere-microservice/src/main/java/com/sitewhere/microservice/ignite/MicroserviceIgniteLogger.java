package com.sitewhere.microservice.ignite;

import org.apache.ignite.IgniteLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/**
 * Implements {@link IgniteLogger} on top of SiteWhere logging infrastructure.
 * 
 * @author Derek
 */
public class MicroserviceIgniteLogger implements IgniteLogger {

    /** Wrapped Logger instance */
    private Logger wrapped;

    public MicroserviceIgniteLogger() {
    }

    protected MicroserviceIgniteLogger(Logger wrapped) {
	this.wrapped = wrapped;
    }

    /*
     * @see org.apache.ignite.IgniteLogger#getLogger(java.lang.Object)
     */
    @Override
    public IgniteLogger getLogger(Object ctgr) {
	if (ctgr == null)
	    return new MicroserviceIgniteLogger((Logger) LogManager.getRootLogger());

	if (ctgr instanceof Class) {
	    String name = ((Class<?>) ctgr).getName();
	    return new MicroserviceIgniteLogger((Logger) LogManager.getLogger(name));
	}

	String name = ctgr.toString();
	return new MicroserviceIgniteLogger((Logger) LogManager.getLogger(name));
    }

    /*
     * @see org.apache.ignite.IgniteLogger#trace(java.lang.String)
     */
    @Override
    public void trace(String msg) {
	getWrapped().trace(msg);
    }

    /*
     * @see org.apache.ignite.IgniteLogger#debug(java.lang.String)
     */
    @Override
    public void debug(String msg) {
	getWrapped().debug(msg);
    }

    /*
     * @see org.apache.ignite.IgniteLogger#info(java.lang.String)
     */
    @Override
    public void info(String msg) {
	getWrapped().info(msg);
    }

    /*
     * @see org.apache.ignite.IgniteLogger#warning(java.lang.String)
     */
    @Override
    public void warning(String msg) {
	getWrapped().warn(msg);
    }

    /*
     * @see org.apache.ignite.IgniteLogger#warning(java.lang.String,
     * java.lang.Throwable)
     */
    @Override
    public void warning(String msg, Throwable e) {
	getWrapped().warn(msg, e);
    }

    /*
     * @see org.apache.ignite.IgniteLogger#error(java.lang.String)
     */
    @Override
    public void error(String msg) {
	getWrapped().error(msg);
    }

    /*
     * @see org.apache.ignite.IgniteLogger#error(java.lang.String,
     * java.lang.Throwable)
     */
    @Override
    public void error(String msg, Throwable e) {
	getWrapped().error(msg, e);
    }

    /*
     * @see org.apache.ignite.IgniteLogger#isTraceEnabled()
     */
    @Override
    public boolean isTraceEnabled() {
	return getWrapped().isTraceEnabled();
    }

    /*
     * @see org.apache.ignite.IgniteLogger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
	return getWrapped().isDebugEnabled();
    }

    /*
     * @see org.apache.ignite.IgniteLogger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
	return getWrapped().isInfoEnabled();
    }

    /*
     * @see org.apache.ignite.IgniteLogger#isQuiet()
     */
    @Override
    public boolean isQuiet() {
	return true;
    }

    /*
     * @see org.apache.ignite.IgniteLogger#fileName()
     */
    @Override
    public String fileName() {
	return null;
    }

    protected Logger getWrapped() {
	return wrapped;
    }

    protected void setWrapped(Logger wrapped) {
	this.wrapped = wrapped;
    }
}