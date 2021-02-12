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
package com.sitewhere.inbound.kafka;

import java.util.List;

import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;

/**
 * Context built by processing inbound events.
 */
public class InboundEventContext {

    /** Decoded event payload */
    private GDecodedEventPayload decodedEventPayload;

    /** Device */
    private IDevice device;

    /** Device assignments list */
    private List<? extends IDeviceAssignment> deviceAssignments;

    /** Exception in processing */
    private Exception exception;

    public InboundEventContext(GDecodedEventPayload decodedEventPayload) {
	this.decodedEventPayload = decodedEventPayload;
    }

    public GDecodedEventPayload getDecodedEventPayload() {
	return decodedEventPayload;
    }

    public void setDecodedEventPayload(GDecodedEventPayload decodedEventPayload) {
	this.decodedEventPayload = decodedEventPayload;
    }

    public IDevice getDevice() {
	return device;
    }

    public void setDevice(IDevice device) {
	this.device = device;
    }

    public List<? extends IDeviceAssignment> getDeviceAssignments() {
	return deviceAssignments;
    }

    public void setDeviceAssignments(List<? extends IDeviceAssignment> deviceAssignments) {
	this.deviceAssignments = deviceAssignments;
    }

    public Exception getException() {
	return exception;
    }

    public void setException(Exception exception) {
	this.exception = exception;
    }
}
