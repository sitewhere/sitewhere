/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.error;

/**
 * Enumerates various system error conditions and gives them unique codes.
 * 
 * @author Derek
 */
public enum ErrorCode {

	/**********
	 * COMMON *
	 **********/

	/** Bad username */
	InvalidMetadataFieldName(10, "Metadata field name contains invalid characters."),

	/*********
	 * USERS *
	 *********/

	/** Bad username */
	InvalidUsername(100, "Username does not exist."),

	/** Bad password */
	InvalidPassword(101, "Password did not match."),

	/** Username already used */
	DuplicateUser(102, "Username already in use."),

	/** One or more required user fields are missing */
	InvalidUserInformation(103, "Missing required fields for user."),

	/** Invalid authority reference */
	InvalidAuthority(120, "Authority does not exist"),

	/** Authority name already used */
	DuplicateAuthority(121, "Authority name already in use."),

	/** No user logged in for action that requires authorization */
	NotLoggedIn(103, "You must provide credentials to perform this action."),

	/***************************
	 * INVALID OR DUPLICATE ID *
	 ***************************/

	/** Attempting to create a device with a duplicate hardware id */
	DuplicateHardwareId(500, "The given hardware id is already in use."),

	/** Invalid device hardware id */
	InvalidHardwareId(501, "Hardware id not found."),

	/** Invalid asset reference id */
	InvalidAssetReferenceId(502, "Asset reference not found."),

	/** Invalid device specification token */
	InvalidDeviceSpecificationToken(503, "Device specification not found."),

	/** Invalid site token */
	InvalidDeviceCommandToken(505, "Device command not found."),

	/** Invalid site token */
	InvalidSiteToken(510, "Site not found."),

	/** Invalid site assignment id */
	InvalidDeviceAssignmentId(520, "Device assignment not found."),

	/** Invalid device assignment token */
	InvalidDeviceAssignmentToken(521, "Device assignment token not found."),

	/** Invalid zone token */
	InvalidZoneToken(522, "Zone not found."),

	/** Invalid device event id */
	InvalidDeviceEventId(523, "Device event not found for id."),

	/** Invalid device group token */
	InvalidDeviceGroupToken(525, "Device group not found."),

	/** Invalid batch operation token */
	InvalidBatchOperationToken(526, "Batch operation not found."),

	/** Invalid batch element */
	InvalidBatchElement(527, "Batch element not found."),

	/** Invalid search provider id */
	InvalidSearchProviderId(528, "Search provider not found."),

	/** Attempting to create a device stream with a stream id already in use */
	DuplicateStreamId(529, "Device assignment has an existing stream with the given id."),

	/** Attempting to access a device stream that does not exist */
	InvalidStreamId(530, "Device assignment does not have an existing stream with the given id."),

	/** Attempting to create a stream id that contains invalid characters */
	InvalidCharsInStreamId(531, "Stream id contains invalid characters."),

	/** Invalid asset category id */
	InvalidAssetCategoryId(535, "Asset category not found."),

	/** Invalid asset id */
	InvalidAssetId(538, "Asset not found."),

	/******************
	 * DEVICE COMMAND *
	 ******************/

	/** Attempting to create a new command that duplicates an existing command */
	DeviceCommandExists(550, "Device command with same namespace and name already exists for specification."),

	/**********
	 * DEVICE *
	 **********/

	/** Attempting to create a new assignment for a device with an active assignment */
	DeviceAlreadyAssigned(600, "Device already has an active assignment."),

	/** Operation assumes device is assigned but no assignment exists */
	DeviceNotAssigned(601, "Device is not currently assigned."),

	/** Attempting to change the hardware id of an existing device */
	DeviceHardwareIdCanNotBeChanged(602, "Device hardware id can not be updated."),

	/** Attempting to delete a device that is currently assigned */
	DeviceCanNotBeDeletedIfAssigned(603, "Device can not be deleted if it is currently assigned."),

	/** Attempting to change site for device that is currently assigned */
	DeviceSiteCanNotBeChangedIfAssigned(604, "Device site can not be changed if it is currently assigned."),

	/** Attempting to create a device element mapping for a path that is already mapped */
	DeviceElementMappingExists(610,
			"Device has an existing mapping for the given device element schema path."),

	/** Attempting to refrence a device element mapping that does not exist */
	DeviceElementMappingDoesNotExist(611, "Device element mapping does not exist."),

	/** Path references a non-existent slot or a device unit */
	InvalidDeviceSlotPath(612, "Path does not correspond to a valid device slot."),

	/** Attempting to create a device element mapping for a device that is already mapped */
	DeviceParentMappingExists(613, "Target device is already in use by an existing mapping."),

	/***********
	 * COMMAND *
	 ***********/

	/** Operation assumes device is assigned but no assignment exists */
	RequiredCommandParameterMissing(650, "Invocation does not specify a parameter marked as required."),

	/********
	 * ZONE *
	 ********/

	/** Zone delete failure message */
	ZoneDeleteFailed(700, "Unable to delete zoned."),

	/**********
	 * ASSETS *
	 **********/

	/** Attempting to create a new asset category with an id already in use */
	AssetCategoryIdInUse(800, "Asset category id is already in use."),

	/** Attempting to create a new asset that conflicts with an existing one */
	AssetIdInUse(801, "Asset id is already in use for this category."),

	/***********
	 * GENERIC *
	 ***********/

	/** Generic delete failure message */
	GenericDeleteFailed(1000, "Delete unsuccessful."),

	/** Some required data was missing */
	IncompleteData(1010, "Not all required data was provided."),

	/*********
	 * OTHER *
	 *********/

	/** Error with no explanation */
	Unknown(9999, "Unknown error.");

	/** Numeric code */
	private long code;

	/** Error message */
	private String message;

	private ErrorCode(long code, String message) {
		this.setCode(code);
		this.setMessage(message);
	}

	/**
	 * Look up the enum based on error code.
	 * 
	 * @param code
	 * @return
	 */
	public static ErrorCode fromCode(long code) {
		for (ErrorCode current : ErrorCode.values()) {
			if (current.getCode() == code) {
				return current;
			}
		}
		throw new RuntimeException("Invalid error code: " + code);
	}

	public void setCode(long code) {
		this.code = code;
	}

	public long getCode() {
		return code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}