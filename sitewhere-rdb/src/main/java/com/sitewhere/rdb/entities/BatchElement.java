/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "batch_element")
public class BatchElement implements IBatchElement {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /** Batch operation id */
    private UUID batchOperationId;

    /** Device id */
    private UUID deviceId;

    @Enumerated(EnumType.STRING)
    private ElementProcessingStatus processingStatus;

    /** Date entity was created */
    private Date processedDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="batch_element_metadata")
    @MapKeyColumn(name="propKey")
    @Column(name="propValue")
    private Map<String, String> metadata = new HashMap<>();

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public UUID getBatchOperationId() {
        return batchOperationId;
    }

    @Override
    public UUID getDeviceId() {
        return deviceId;
    }

    @Override
    public ElementProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    @Override
    public Date getProcessedDate() {
        return processedDate;
    }

    @Override
    public Map<String, String> getMetadata() {
        return null;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setBatchOperationId(UUID batchOperationId) {
        this.batchOperationId = batchOperationId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public void setProcessingStatus(ElementProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
