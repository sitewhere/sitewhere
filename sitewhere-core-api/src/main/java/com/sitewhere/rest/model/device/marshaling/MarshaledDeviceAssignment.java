package com.sitewhere.rest.model.device.marshaling;

import com.sitewhere.rest.model.asset.HardwareAsset;
import com.sitewhere.rest.model.asset.LocationAsset;
import com.sitewhere.rest.model.asset.PersonAsset;
import com.sitewhere.rest.model.device.DeviceAssignment;

/**
 * Extends {@link DeviceAssignment} to support fields that can be included on
 * REST calls.
 * 
 * @author Derek
 */
public class MarshaledDeviceAssignment extends DeviceAssignment {

    /** Serial version UID */
    private static final long serialVersionUID = 2395477856520830666L;

    /** Device being assigned */
    private MarshaledDevice device;

    /** Associated person asset */
    private PersonAsset associatedPerson;

    /** Associated hardware asset */
    private HardwareAsset associatedHardware;

    /** Associated location asset */
    private LocationAsset associatedLocation;

    public MarshaledDevice getDevice() {
	return device;
    }

    public void setDevice(MarshaledDevice device) {
	this.device = device;
    }

    public PersonAsset getAssociatedPerson() {
	return associatedPerson;
    }

    public void setAssociatedPerson(PersonAsset associatedPerson) {
	this.associatedPerson = associatedPerson;
    }

    public HardwareAsset getAssociatedHardware() {
	return associatedHardware;
    }

    public void setAssociatedHardware(HardwareAsset associatedHardware) {
	this.associatedHardware = associatedHardware;
    }

    public LocationAsset getAssociatedLocation() {
	return associatedLocation;
    }

    public void setAssociatedLocation(LocationAsset associatedLocation) {
	this.associatedLocation = associatedLocation;
    }
}