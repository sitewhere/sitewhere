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
package com.sitewhere.event.persistence.warp10;

import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.warp10.Warp10Converter;
import com.sitewhere.warp10.rest.GTSInput;
import com.sitewhere.warp10.rest.GTSOutput;

/**
 * Used to load or save device state change data to Warp 10.
 *
 * @author Luciano
 */
public class Warp10DeviceStateChange implements Warp10Converter<IDeviceStateChange> {


    /**
     * Property for state attribute
     */
    public static final String PROP_ATTRIBUTE = "attr";

    /**
     * Property for state change type
     */
    public static final String PROP_TYPE = "type";

    /**
     * Property for previous state value
     */
    public static final String PROP_PREVIOUS_STATE = "prev";

    /**
     * Property for new state value
     */
    public static final String PROP_NEW_STATE = "news";

    @Override
    public GTSInput convert(IDeviceStateChange source) {
        return Warp10DeviceStateChange.toGTS(source);
    }

    @Override
    public IDeviceStateChange convert(GTSOutput source) {
        return Warp10DeviceStateChange.fromGTS(source);
    }

    public static GTSInput toGTS(IDeviceStateChange source) {
        GTSInput gtsInput = GTSInput.builder();
        Warp10DeviceStateChange.toGTS(source, gtsInput);
        return gtsInput;
    }

    public static void toGTS(IDeviceStateChange source, GTSInput target) {
        Warp10DeviceEvent.toGTS(source, target, false);
        target.setName(source.getDeviceAssignmentId().toString());
        target.setTs(source.getReceivedDate().getTime());
        target.getLabels().put(PROP_ATTRIBUTE, source.getAttribute());
        target.getLabels().put(PROP_TYPE, source.getType());
        target.getLabels().put(PROP_PREVIOUS_STATE, source.getPreviousState());
        target.getLabels().put(PROP_NEW_STATE, source.getNewState());
    }

    public static DeviceStateChange fromGTS(GTSOutput source){
        DeviceStateChange deviceStateChange = new DeviceStateChange();
        Warp10DeviceStateChange.fromGTS(source, deviceStateChange);
        return deviceStateChange;
    }

    public static void fromGTS(GTSOutput source , DeviceStateChange target){
        Warp10DeviceEvent.fromGTS(source, target, false);
        String attribute = source.getLabels().get(PROP_ATTRIBUTE);
        String type = source.getLabels().get(PROP_TYPE);
        String previousState = source.getLabels().get(PROP_PREVIOUS_STATE);
        String newState = source.getLabels().get(PROP_NEW_STATE);

        target.setAttribute(attribute);
        target.setType(type);
        target.setPreviousState(previousState);
        target.setNewState(newState);
    }
}
