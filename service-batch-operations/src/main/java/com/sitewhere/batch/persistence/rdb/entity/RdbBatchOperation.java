/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.batch.persistence.rdb.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.batch.IBatchOperation;

@Entity
@Table(name = "batch_operation")
@NamedQuery(name = Queries.QUERY_BATCH_OPERATION_BY_TOKEN, query = "SELECT b FROM RdbBatchOperation b WHERE b.token = :token")
public class RdbBatchOperation extends RdbPersistentEntity implements IBatchOperation {

    /** Serial version UID */
    private static final long serialVersionUID = 5437116801408956645L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "processing_status")
    @Enumerated(EnumType.STRING)
    private BatchOperationStatus processingStatus;

    @Column(name = "processing_started_date")
    private Date processingStartedDate;

    @Column(name = "processing_ended_date")
    private Date processingEndedDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "batch_operation_metadata", joinColumns = @JoinColumn(name = "batch_operation_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "batch_operation_parameters", joinColumns = @JoinColumn(name = "batch_operation_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> parameters = new HashMap<>();

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchOperation#getOperationType()
     */
    @Override
    public String getOperationType() {
	return operationType;
    }

    public void setOperationType(String operationType) {
	this.operationType = operationType;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchOperation#getProcessingStatus()
     */
    @Override
    public BatchOperationStatus getProcessingStatus() {
	return processingStatus;
    }

    public void setProcessingStatus(BatchOperationStatus processingStatus) {
	this.processingStatus = processingStatus;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchOperation#getProcessingStartedDate()
     */
    @Override
    public Date getProcessingStartedDate() {
	return processingStartedDate;
    }

    public void setProcessingStartedDate(Date processingStartedDate) {
	this.processingStartedDate = processingStartedDate;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchOperation#getProcessingEndedDate()
     */
    @Override
    public Date getProcessingEndedDate() {
	return processingEndedDate;
    }

    public void setProcessingEndedDate(Date processingEndedDate) {
	this.processingEndedDate = processingEndedDate;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    /*
     * @see
     * com.sitewhere.rdb.entities.RdbPersistentEntity#setMetadata(java.util.Map)
     */
    @Override
    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchOperation#getParameters()
     */
    @Override
    public Map<String, String> getParameters() {
	return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
	this.parameters = parameters;
    }

    public static void copy(IBatchOperation source, RdbBatchOperation target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getOperationType() != null) {
	    target.setOperationType(source.getOperationType());
	}
	if (source.getProcessingStatus() != null) {
	    target.setProcessingStatus(source.getProcessingStatus());
	}
	if (source.getProcessingStartedDate() != null) {
	    target.setProcessingStartedDate(source.getProcessingStartedDate());
	}
	if (source.getProcessingEndedDate() != null) {
	    target.setProcessingEndedDate(source.getProcessingEndedDate());
	}
	RdbPersistentEntity.copy(source, target);
    }
}
