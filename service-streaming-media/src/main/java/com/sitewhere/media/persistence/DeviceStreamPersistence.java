package com.sitewhere.media.persistence;

import java.util.UUID;

import com.sitewhere.persistence.Persistence;
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
