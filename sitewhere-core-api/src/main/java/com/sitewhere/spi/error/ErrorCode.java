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

    /***********
     * GENERIC *
     ***********/

    /** Used to indicate a custom application error */
    Error(1, "Generic application error."),

    /**********
     * COMMON *
     **********/

    /** Operation was not permitted */
    OperationNotPermitted(5, "User does not have permission for this operation."),

    /** Bad username */
    InvalidMetadataFieldName(10, "Metadata field name contains invalid characters."),

    /****************
     * MICROSERVICE *
     ***************/

    /** Unknown microservice identifier */
    InvalidMicroserviceIdentifier(500, "Microservice identifier was not recognized."),

    /*********
     * USERS *
     *********/

    /** Bad username */
    InvalidUsername(1000, "Username does not exist."),

    /** Bad password */
    InvalidPassword(1010, "Password did not match."),

    /** Username already used */
    DuplicateUser(1020, "Username already in use."),

    /** One or more required user fields are missing */
    InvalidUserInformation(1030, "Missing required fields for user."),

    /** Invalid authority reference */
    InvalidAuthority(1040, "Authority does not exist"),

    /** Authority name already used */
    DuplicateAuthority(1050, "Authority name already in use."),

    /** No user logged in for action that requires authorization */
    NotLoggedIn(1060, "You must provide credentials to perform this action."),

    /** Invalid tenant authentication token */
    InvalidTenantAuthToken(1070, "Tenant not found for authentication token."),

    /** Tenant authentication token not passed */
    MissingTenantAuthToken(1080, "Tenant authentication token required for request."),

    /** Tenant authentication token passed but tenant not authorized for user */
    NotAuthorizedForTenant(1090, "Not authorized to view information for tenant."),

    /** Invalid tenant engine reference */
    InvalidTenantEngineId(1100, "Tenant engine does not exist."),

    /** Invalid tenant reference */
    InvalidTenantId(1110, "Tenant does not exist."),

    /** Invalid tenant reference */
    InvalidTenantToken(1120, "Tenant does not exist."),

    /***********
     * TENANTS *
     ***********/

    /** Indicates 'start' command issued to tenant that was already started */
    TenantAlreadyStarted(3000, "Tenant was already started."),

    /** Indicates 'stop' command issued to tenant that was already stopped */
    TenantAlreadyStopped(3010, "Tenant was already stopped."),

    /** Indicates a tenant id was passed with the wrong format */
    TenantIdFormat(3020, "Tenant id should be an alphanumeric value with no spaces."),

    /***************************
     * INVALID OR DUPLICATE ID *
     ***************************/

    /** Malformed device hardware id */
    MalformedHardwareId(4010,
	    "Hardware id must consist of alphanumeric values with dashes, underscores, and no spaces."),

    /** Generic duplicate id error */
    DuplicateId(4020, "The given id is already in use."),

    /** Attempting to create a device with a duplicate token */
    DuplicateDeviceToken(4030, "Device token already in use."),

    /** Invalid device token */
    InvalidDeviceToken(4040, "Device token not found."),

    /** Invalid device id */
    InvalidDeviceId(4050, "Device id not found."),

    /** Invalid device type token */
    InvalidDeviceTypeToken(4060, "Device type not found."),

    /** Invalid device type token */
    DuplicateDeviceTypeToken(4070, "Device type token already in use."),

    /** Invalid device command token */
    InvalidDeviceCommandToken(4080, "Device command not found."),

    /** Invalid device command id */
    InvalidDeviceCommandId(4090, "Device command not found."),

    /** Invalid device status code */
    InvalidDeviceStatusCode(4100, "Device status not found."),

    /** Duplicate area type token */
    DuplicateAreaTypeToken(4110, "Area type token already in use."),

    /** Duplicate area token */
    DuplicateAreaToken(4120, "Area token already in use."),

    /** Invalid area token */
    InvalidAreaToken(4130, "Area not found."),

    /** Invalid area type token */
    InvalidAreaTypeToken(4140, "Area type not found."),

    /** Duplicate device assignment token */
    DuplicateDeviceAssignment(4150, "Device assignment token already in use."),

    /** Invalid site assignment id */
    InvalidDeviceAssignmentId(4160, "Device assignment not found."),

    /** Invalid device assignment token */
    InvalidDeviceAssignmentToken(4170, "Device assignment token not found."),

    /** Invalid zone token */
    InvalidZoneToken(4180, "Zone not found."),

    /** Invalid device event id */
    InvalidDeviceEventId(4190, "Device event not found for id."),

    /** Invalid search provider id */
    InvalidSearchProviderId(4200, "Search provider not found."),

    /** Attempting to create a device stream with a stream id already in use */
    DuplicateStreamId(4210, "Device assignment has an existing stream with the given id."),

    /** Attempting to access a device stream that does not exist */
    InvalidStreamId(4220, "Device assignment does not have an existing stream with the given id."),

    /** Attempting to create a stream id that contains invalid characters */
    InvalidCharsInStreamId(4230, "Stream id contains invalid characters."),

    /** Invalid asset type token */
    InvalidAssetTypeToken(4240, "Asset type token not found."),

    /** Invalid asset type id */
    InvalidAssetTypeId(4250, "Asset type not found."),

    /** Invalid asset token */
    InvalidAssetToken(4260, "Asset token not found."),

    /** Invalid asset id */
    InvalidAssetId(4270, "Asset not found."),

    /** Attempting to create a tenant with an id already in use */
    DuplicateTenantId(4280, "Tenant id already in use."),

    /** Attempting to create a tenant group with an id already in use */
    DuplicateTenantGroupId(4290, "Tenant group id already in use."),

    /** Invalid schedule token */
    InvalidScheduleToken(4300, "Schedule not found."),

    /** Invalid scheduled job token */
    InvalidScheduledJobToken(4310, "Scheduled job not found."),

    /** Invalid schedule token */
    DuplicateScheduleToken(4320, "Schedule token already in use."),

    /** Invalid scheduled job token */
    DuplicateScheduledJobToken(4330, "Scheduled job token already in use."),

    /** Invalid tenant template id */
    InvalidTenantTemplateId(4340, "Tenant template not found"),

    /** Duplicate zone token */
    DuplicateZoneToken(4350, "Zone token already in use."),

    /** Duplicate batch operation token */
    DuplicateBatchOperationToken(4360, "Batch operation token already in use."),

    /** Duplicate batch element */
    DuplicateBatchElement(4370, "Batch element already in use."),

    /** Duplicate device group token */
    DuplicateDeviceGroupToken(4380, "Device group token already in use."),

    /** Invalid device group token */
    InvalidDeviceGroupToken(4390, "Device group not found."),

    /** Invalid device group id */
    InvalidDeviceGroupId(4400, "Device group not found."),

    /** Invalid batch operation id */
    InvalidBatchOperationId(4410, "Batch operation not found."),

    /** Invalid batch operation token */
    InvalidBatchOperationToken(4420, "Batch operation not found."),

    /** Invalid batch element id */
    InvalidBatchElementId(4430, "Batch element not found."),

    /******************
     * DEVICE COMMAND *
     ******************/

    /** Attempting to create a new command that duplicates an existing command */
    DeviceCommandExists(5000, "Device command with same namespace and name already exists for specification."),

    /*****************
     * DEVICE STATUS *
     *****************/

    /** Attempting to create a new status that duplicates an existing status */
    DeviceStatusExists(6000, "Device status with same code already exists for specification."),

    /*********************
     * DEVICE ASSIGNMENT *
     *********************/

    /** Attempting to delete an active assignment. */
    CanNotDeleteActiveAssignment(7000, "Can not delete an active device assignment."),

    /**************
     * DEVICE TYPE*
     **************/

    DeviceTypeInUseByDevices(7500,
	    "Device type can not be deleted. One or more devices are currently using this device type."),

    /**********
     * DEVICE *
     **********/

    /**
     * Attempting to create a new assignment for a device with an active assignment
     */
    DeviceAlreadyAssigned(8000, "Device already has an active assignment."),

    /** Operation assumes device is assigned but no assignment exists */
    DeviceNotAssigned(8010, "Device is not currently assigned."),

    /** Attempting to change the hardware id of an existing device */
    DeviceHardwareIdCanNotBeChanged(8020, "Device hardware id can not be updated."),

    /** Attempting to delete a device that is currently assigned */
    DeviceCanNotBeDeletedIfAssigned(8030, "Device can not be deleted if it is currently assigned."),

    /** Attempting to change site for device that is currently assigned */
    DeviceSiteCanNotBeChangedIfAssigned(8040, "Device site can not be changed if it is currently assigned."),

    /**
     * Attempting to create a device element mapping for a path that is already
     * mapped
     */
    DeviceElementMappingExists(8050, "Device has an existing mapping for the given device element schema path."),

    /** Attempting to refrence a device element mapping that does not exist */
    DeviceElementMappingDoesNotExist(8060, "Device element mapping does not exist."),

    /** Path references a non-existent slot or a device unit */
    InvalidDeviceSlotPath(8070, "Path does not correspond to a valid device slot."),

    /**
     * Attempting to create a device element mapping for a device that is already
     * mapped
     */
    DeviceParentMappingExists(8080, "Target device is already in use by an existing mapping."),

    /** Unable to delete. Device referenced by assignments */
    DeviceDeleteHasAssignments(8090, "Unable to delete device. One or more device assignments reference this device."),

    /***********
     * COMMAND *
     ***********/

    /** Operation assumes device is assigned but no assignment exists */
    RequiredCommandParameterMissing(9000, "Invocation does not specify a parameter marked as required."),

    /*** Required Parameter does not have a value assigned to it */
    RequiredCommandParameterValueMissing(9010, "Invocation does not assign a value to a required parameter."),

    /********
     * ZONE *
     ********/

    /** Zone delete failure message */
    ZoneDeleteFailed(10000, "Unable to delete zoned."),

    /***************
     * ASSET TYPES *
     ***************/

    /** Attempting to create a new asset type that conflicts with an existing one */
    AssetTypeTokenInUse(11000, "Asset type token is already in use."),

    /** Attempting to create a new asset that conflicts with an existing one */
    AssetTokenInUse(11010, "Asset token is already in use."),

    /** Attempting to delete asset type used by assets */
    AssetTypeNoDeleteHasAssets(11020, "Unable to delete asset type. One or more assets reference this asset type."),

    /**********
     * ASSETS *
     **********/

    /** Attempting to delete asset used by device assignments */
    AssetNoDeleteHasAssignments(11500, "Unable to delete asset. One or more device assignments reference this asset."),

    /********************
     * LABEL GENERATION *
     ********************/

    /** Request for label generator that does not exist */
    LabelGeneratorNotFound(12000, "The requested label generator was not found."),

    /***********
     * GENERIC *
     ***********/

    /** Generic delete failure message */
    GenericDeleteFailed(13000, "Delete unsuccessful."),

    /** Some required data was missing */
    IncompleteData(13010, "Not all required data was provided."),

    /*********
     * OTHER *
     *********/

    /** Error with no explanation */
    Unknown(99999, "Unknown error.");

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