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
package com.sitewhere.devicestate.presence;

import com.sitewhere.devicestate.spi.IPresenceNotificationStrategy;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.state.IDeviceState;

/**
 * Various implementations of {@link IPresenceNotificationStrategy}.
 */
public class PresenceNotificationStrategies {

    /**
     * Only stores/sends a single presence missing notification and keeps state. If
     * presence is regained, the state will be reset.
     */
    public static class SendOnceNotificationStrategy implements IPresenceNotificationStrategy {

	/*
	 * @see com.sitewhere.devicestate.spi.IPresenceNotificationStrategy#
	 * shouldGenerateEvent(com.sitewhere.spi.device.state.IDeviceState,
	 * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
	 */
	@Override
	public boolean shouldGenerateEvent(IDeviceState deviceState, IDeviceStateChangeCreateRequest request)
		throws SiteWhereException {
	    if (deviceState.getPresenceMissingDate() != null) {
		return false;
	    }
	    return true;
	}
    }
}