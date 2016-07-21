package com.sitewhere.hbase.tenant;

public enum TenantGroupSubtype {

	/** Tenant group record */
	TenantGroup((byte) 0x00),

	/** Tenant group record */
	TenantGroupElement((byte) 0x01);

	/** Type indicator */
	private byte type;

	/**
	 * Create a unique id type with the given byte value.
	 * 
	 * @param value
	 */
	private TenantGroupSubtype(byte type) {
		this.type = type;
	}

	/**
	 * Get the record type indicator.
	 * 
	 * @return
	 */
	public byte getType() {
		return type;
	}

}
