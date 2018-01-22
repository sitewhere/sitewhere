package com.sitewhere.hazelcast;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import com.hazelcast.config.Config;
import com.hazelcast.core.ClientService;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.DistributedObjectListener;
import com.hazelcast.core.Endpoint;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.ICacheManager;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IList;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.core.ISet;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.IdGenerator;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.PartitionService;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.durableexecutor.DurableExecutorService;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.quorum.QuorumService;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalTask;

/**
 * Allows a class to override methods on an underlying
 * {@link HazelcastInstance}.
 * 
 * @author Derek
 */
public class HazelcastInstanceDecorator implements HazelcastInstance {

    /** Delgate being wrapped */
    private HazelcastInstance delegate;

    public HazelcastInstanceDecorator(HazelcastInstance delegate) {
	this.delegate = delegate;
    }

    @Override
    public String getName() {
	return delegate.getName();
    }

    @Override
    public <E> IQueue<E> getQueue(String name) {
	return delegate.getQueue(name);
    }

    @Override
    public <E> ITopic<E> getTopic(String name) {
	return delegate.getTopic(name);
    }

    @Override
    public <E> ISet<E> getSet(String name) {
	return delegate.getSet(name);
    }

    @Override
    public <E> IList<E> getList(String name) {
	return delegate.getList(name);
    }

    @Override
    public <K, V> IMap<K, V> getMap(String name) {
	return delegate.getMap(name);
    }

    @Override
    public <K, V> ReplicatedMap<K, V> getReplicatedMap(String name) {
	return delegate.getReplicatedMap(name);
    }

    @Override
    public JobTracker getJobTracker(String name) {
	return delegate.getJobTracker(name);
    }

    @Override
    public <K, V> MultiMap<K, V> getMultiMap(String name) {
	return delegate.getMultiMap(name);
    }

    @Override
    public ILock getLock(String key) {
	return delegate.getLock(key);
    }

    @Override
    public <E> Ringbuffer<E> getRingbuffer(String name) {
	return delegate.getRingbuffer(name);
    }

    @Override
    public <E> ITopic<E> getReliableTopic(String name) {
	return delegate.getReliableTopic(name);
    }

    @Override
    public Cluster getCluster() {
	return delegate.getCluster();
    }

    @Override
    public Endpoint getLocalEndpoint() {
	return delegate.getLocalEndpoint();
    }

    @Override
    public IExecutorService getExecutorService(String name) {
	return delegate.getExecutorService(name);
    }

    @Override
    public <T> T executeTransaction(TransactionalTask<T> task) throws TransactionException {
	return delegate.executeTransaction(task);
    }

    @Override
    public <T> T executeTransaction(TransactionOptions options, TransactionalTask<T> task) throws TransactionException {
	return delegate.executeTransaction(options, task);
    }

    @Override
    public TransactionContext newTransactionContext() {
	return delegate.newTransactionContext();
    }

    @Override
    public TransactionContext newTransactionContext(TransactionOptions options) {
	return delegate.newTransactionContext(options);
    }

    @Override
    public IdGenerator getIdGenerator(String name) {
	return delegate.getIdGenerator(name);
    }

    @Override
    public IAtomicLong getAtomicLong(String name) {
	return delegate.getAtomicLong(name);
    }

    @Override
    public <E> IAtomicReference<E> getAtomicReference(String name) {
	return delegate.getAtomicReference(name);
    }

    @Override
    public ICountDownLatch getCountDownLatch(String name) {
	return delegate.getCountDownLatch(name);
    }

    @Override
    public ISemaphore getSemaphore(String name) {
	return delegate.getSemaphore(name);
    }

    @Override
    public Collection<DistributedObject> getDistributedObjects() {
	return delegate.getDistributedObjects();
    }

    @Override
    public String addDistributedObjectListener(DistributedObjectListener distributedObjectListener) {
	return delegate.addDistributedObjectListener(distributedObjectListener);
    }

    @Override
    public boolean removeDistributedObjectListener(String registrationId) {
	return delegate.removeDistributedObjectListener(registrationId);
    }

    @Override
    public Config getConfig() {
	return delegate.getConfig();
    }

    @Override
    public PartitionService getPartitionService() {
	return delegate.getPartitionService();
    }

    @Override
    public QuorumService getQuorumService() {
	return delegate.getQuorumService();
    }

    @Override
    public ClientService getClientService() {
	return delegate.getClientService();
    }

    @Override
    public LoggingService getLoggingService() {
	return delegate.getLoggingService();
    }

    @Override
    public LifecycleService getLifecycleService() {
	return delegate.getLifecycleService();
    }

    @Override
    public <T extends DistributedObject> T getDistributedObject(String serviceName, String name) {
	return getDistributedObject(serviceName, name);
    }

    @Override
    public ConcurrentMap<String, Object> getUserContext() {
	return delegate.getUserContext();
    }

    @Override
    public HazelcastXAResource getXAResource() {
	return delegate.getXAResource();
    }

    @Override
    public DurableExecutorService getDurableExecutorService(String name) {
	return delegate.getDurableExecutorService(name);
    }

    @Override
    public ICacheManager getCacheManager() {
	return delegate.getCacheManager();
    }

    @Override
    public void shutdown() {
	delegate.shutdown();
    }
}