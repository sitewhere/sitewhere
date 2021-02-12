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
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;

@Entity
@Table(name = "batch_element")
public class RdbBatchElement implements IBatchElement {

    /** Serial version UID */
    private static final long serialVersionUID = 90247662858403978L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Batch operation id */
    @Column(name = "batch_operation_id")
    private UUID batchOperationId;

    /** Device id */
    @Column(name = "device_id")
    private UUID deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status")
    private ElementProcessingStatus processingStatus;

    /** Date entity was created */
    @Column(name = "processed_date")
    private Date processedDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "batch_element_metadata", joinColumns = @JoinColumn(name = "batch_element_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

    /*
     * @see com.sitewhere.spi.batch.IBatchElement#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchElement#getBatchOperationId()
     */
    @Override
    public UUID getBatchOperationId() {
	return batchOperationId;
    }

    public void setBatchOperationId(UUID batchOperationId) {
	this.batchOperationId = batchOperationId;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchElement#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchElement#getProcessingStatus()
     */
    @Override
    public ElementProcessingStatus getProcessingStatus() {
	return processingStatus;
    }

    public void setProcessingStatus(ElementProcessingStatus processingStatus) {
	this.processingStatus = processingStatus;
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchElement#getProcessedDate()
     */
    @Override
    public Date getProcessedDate() {
	return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
	this.processedDate = processedDate;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static void copy(IBatchElement source, RdbBatchElement target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getBatchOperationId() != null) {
	    target.setBatchOperationId(source.getBatchOperationId());
	}
	if (source.getDeviceId() != null) {
	    target.setDeviceId(source.getDeviceId());
	}
	if (source.getProcessedDate() != null) {
	    target.setProcessedDate(source.getProcessedDate());
	}
	if (source.getProcessingStatus() != null) {
	    target.setProcessingStatus(source.getProcessingStatus());
	}
    }
}
