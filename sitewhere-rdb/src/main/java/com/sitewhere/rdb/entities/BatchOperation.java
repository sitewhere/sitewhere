/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.batch.IBatchOperation;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "batch_operation")
public class BatchOperation implements IBatchOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "token")
    private String token;

    @Column(name = "operation_type")
    private String operationType;

    @Column(name = "processing_status")
    @Enumerated(EnumType.STRING)
    private BatchOperationStatus processingStatus;

    @Column(name = "processing_started_date")
    private Date processingStartedDate;

    @Column(name = "processing_ended_date")
    private Date processingEndedDate;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="batch_operation_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> metadata = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="batch_operation_parameters")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> parameters = new HashMap<>();

    @Override
    public String getOperationType() {
        return operationType;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public BatchOperationStatus getProcessingStatus() {
        return processingStatus;
    }

    @Override
    public Date getProcessingStartedDate() {
        return processingStartedDate;
    }

    @Override
    public Date getProcessingEndedDate() {
        return processingEndedDate;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public void setProcessingStatus(BatchOperationStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public void setProcessingStartedDate(Date processingStartedDate) {
        this.processingStartedDate = processingStartedDate;
    }

    public void setProcessingEndedDate(Date processingEndedDate) {
        this.processingEndedDate = processingEndedDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
