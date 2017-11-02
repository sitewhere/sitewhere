package com.sitewhere.device.communication.mqtt;

/**
 * Used to convert quality of service values for MQTT.
 * 
 * @author Derek
 */
public enum QoS {

    AtMostOnce("AT_MOST_ONCE", 0),

    AtLeastOnce("AT_LEAST_ONCE", 1),

    ExactlyOnce("EXACTLY_ONCE", 2);

    /** Label */
    private String label;

    /** Value */
    private int value;

    private QoS(String label, int value) {
	this.label = label;
	this.value = value;
    }

    public static int getValueFor(String label) {
	for (QoS current : QoS.values()) {
	    if (current.getLabel().equals(label)) {
		return current.getValue();
	    }
	}
	return 0;
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public int getValue() {
	return value;
    }

    public void setValue(int value) {
	this.value = value;
    }
}