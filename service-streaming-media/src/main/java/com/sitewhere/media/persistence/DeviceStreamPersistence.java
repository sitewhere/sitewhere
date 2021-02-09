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
package com.sitewhere.media.persistence;

import java.util.UUID;

import com.sitewhere.microservice.persistence.Persistence;
import com.sitewhere.rest.model.device.streaming.DeviceStream;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;

public class DeviceStreamPersistence extends Persistence {

    /**
     * Common logic for creating {@link DeviceStream} from
     * {@link IDeviceStreamCreateRequest}.
     * 
     * @param assignment
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStream deviceStreamCreateLogic(IDeviceAssignment assignment, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	DeviceStream stream = new DeviceStream();
	stream.setId(UUID.randomUUID());
	stream.setAssignmentId(assignment.getId());

	// Content type is required.
	require("Content Type", request.getContentType());
	stream.setContentType(request.getContentType());

	return stream;
    }
}
