import {
  createAxiosBasicAuth,
  createAxiosJwt
} from './sitewhere-api'

import {
  getJwt
} from './sitewhere-auth-api'

import {
  getTopology,
  getGlobalTopology,
  getTenantTopology,
  getMicroserviceTenantRuntimeState,
  getConfigurationModel,
  getGlobalConfiguration,
  getTenantConfiguration,
  updateGlobalConfiguration,
  updateTenantConfiguration
} from './sitewhere-instance-api'

// Users.
import {
  createUser,
  updateUser,
  getUser,
  deleteUser,
  listUsers,
  listUserTenants,
  getAuthoritiesHierarchy
} from './sitewhere-users-api.js'

// Tenants.
import {
  createTenant,
  getTenant,
  updateTenant,
  listTenants,
  deleteTenant,
  getTenantTemplates
} from './sitewhere-tenants-api.js'

// Sites.
import {
  createSite,
  getSite,
  updateSite,
  listSites,
  deleteSite,
  listAssignmentsForSite,
  listLocationsForSite,
  listMeasurementsForSite,
  listAlertsForSite,
  listZonesForSite,
  createZone,
  getZone,
  updateZone,
  deleteZone
} from './sitewhere-sites-api.js'

// Specifications.
import {
  createDeviceSpecification,
  getDeviceSpecification,
  getDeviceSpecificationProtobuf,
  updateDeviceSpecification,
  listDeviceSpecifications,
  deleteDeviceSpecification,
  createDeviceCommand,
  getDeviceCommand,
  listDeviceCommands,
  updateDeviceCommand,
  listDeviceCommandsByNamespace,
  deleteDeviceCommand,
  createDeviceStatus,
  getDeviceStatus,
  listDeviceStatuses,
  updateDeviceStatus,
  deleteDeviceStatus
} from './sitewhere-specifications-api.js'

// Devices.
import {
  createDevice,
  listDevices,
  listFilteredDevices,
  getDevice,
  updateDevice,
  deleteDevice,
  listDeviceAssignmentHistory
} from './sitewhere-devices-api.js'

// Assignments.
import {
  createDeviceAssignment,
  getDeviceAssignment,
  releaseAssignment,
  missingAssignment,
  deleteDeviceAssignment,
  createMeasurementsForAssignment,
  listMeasurementsForAssignment,
  createLocationForAssignment,
  listLocationsForAssignment,
  createAlertForAssignment,
  listAlertsForAssignment,
  createCommandInvocationForAssignment,
  scheduleCommandInvocation,
  listCommandInvocationsForAssignment,
  listCommandResponsesForAssignment
} from './sitewhere-assignments-api.js'

// Device Groups.
import {
  createDeviceGroup,
  updateDeviceGroup,
  getDeviceGroup,
  listDeviceGroups,
  listDeviceGroupElements,
  addDeviceGroupElement,
  deleteDeviceGroupElement,
  deleteDeviceGroup
} from './sitewhere-device-groups-api.js'

// Assets.
import {
  getAssetModules,
  createAssetCategory,
  updateAssetCategory,
  getAssetCategory,
  deleteAssetCategory,
  listAssetCategories,
  listCategoryAssets,
  searchAssets,
  createHardwareAsset,
  createPersonAsset,
  createLocationAsset,
  getAssetById,
  updateHardwareAsset,
  updatePersonAsset,
  updateLocationAsset,
  deleteAsset
} from './sitewhere-assets-api.js'

// Batch operations.
import {
  getBatchOperation,
  listBatchOperations,
  listBatchOperationElements,
  createBatchCommandInvocation,
  createBatchCommandByCriteria,
  scheduleBatchCommandByCriteria
} from './sitewhere-batch-api.js'

// Schedules.
import {
  createSchedule,
  getSchedule,
  updateSchedule,
  deleteSchedule,
  listSchedules
} from './sitewhere-schedules-api.js'

/**
 * Create core API URL based on hostname/port settings.
 */
export function createCoreApiUrl (store) {
  return store.getters.protocol + '://' + store.getters.server + ':' +
    store.getters.port + '/sitewhere/api/'
}

/**
 * Create authentication API URL based on hostname/port settings.
 */
export function createAuthApiUrl (store) {
  return store.getters.protocol + '://' + store.getters.server + ':' +
    store.getters.port + '/sitewhere/authapi/'
}

/**
 * Create websocket URL based on hostname/port settings.
 */
export function createAdminWebSocketUrl (store) {
  return store.getters.protocol + '://' + store.getters.server + ':' +
    store.getters.port + '/sitewhere/ws/admin/'
}

/**
 * Create JWT authenticated axios client based on store values.
 */
export function createCoreApiCall (store) {
  var baseUrl = createCoreApiUrl(store)
  var jwt = store.getters.jwt
  var tenantId = (store.getters.selectedTenant) ? store.getters.selectedTenant.id : ''
  var tenantAuth = (store.getters.selectedTenant) ? store.getters.selectedTenant.authenticationToken : ''
  return createAxiosJwt(baseUrl, jwt, tenantId, tenantAuth)
}

/**
 * Create basic auth axios client for getting JWT based on store values.
 */
export function createAuthApiCall (store) {
  var baseUrl = createAuthApiUrl(store)
  var authToken = store.getters.authToken
  return createAxiosBasicAuth(baseUrl, authToken)
}

/**
 * Wrap API call to indicate loading status.
 */
function loaderWrapper (store, apiCall) {
  return new Promise((resolve, reject) => {
    store.commit('startLoading')
    apiCall.then(function (response) {
      store.commit('stopLoading')
      store.commit('error', null)
      resolve(response)
    }).catch(function (e) {
      store.commit('stopLoading')
      store.commit('error', e)
      reject(e)
    })
  })
}

/**
 * Get JWT based on credentials.
 */
export function _getJwt (store) {
  let axios = createAuthApiCall(store)
  let api = getJwt(axios)
  return loaderWrapper(store, api)
}

/**
 * Get all microservices in instance topology.
 */
export function _getTopology (store) {
  let axios = createCoreApiCall(store)
  let api = getTopology(axios)
  return loaderWrapper(store, api)
}

/**
 * Get global microservices in instance topology.
 */
export function _getGlobalTopology (store) {
  let axios = createCoreApiCall(store)
  let api = getGlobalTopology(axios)
  return loaderWrapper(store, api)
}

/**
 * Get tenant microservices in instance topology.
 */
export function _getTenantTopology (store) {
  let axios = createCoreApiCall(store)
  let api = getTenantTopology(axios)
  return loaderWrapper(store, api)
}

/**
 * Get the state of all tenant engines (across all microservice instances)
 * for a given tenant id.
 */
export function _getMicroserviceTenantRuntimeState (store, identifier, tenantId) {
  let axios = createCoreApiCall(store)
  let api = getMicroserviceTenantRuntimeState(axios, identifier, tenantId)
  return loaderWrapper(store, api)
}

/**
 * Get configuration model for a microservice based on identifier.
 */
export function _getConfigurationModel (store, identifier) {
  let axios = createCoreApiCall(store)
  let api = getConfigurationModel(axios, identifier)
  return loaderWrapper(store, api)
}

/**
 * Get global microservice configuration based on identifier.
 */
export function _getGlobalConfiguration (store, identifier) {
  let axios = createCoreApiCall(store)
  let api = getGlobalConfiguration(axios, identifier)
  return loaderWrapper(store, api)
}

/**
 * Get tenant microservice configuration based on identifier.
 */
export function _getTenantConfiguration (store, tenantId, identifier) {
  let axios = createCoreApiCall(store)
  let api = getTenantConfiguration(axios, tenantId, identifier)
  return loaderWrapper(store, api)
}

/**
 * Update global microservice configuration based on identifier.
 */
export function _updateGlobalConfiguration (store, identifier, content) {
  let axios = createCoreApiCall(store)
  let api = updateGlobalConfiguration(axios, identifier, content)
  return loaderWrapper(store, api)
}

/**
 * Update tenant microservice configuration based on identifier.
 */
export function _updateTenantConfiguration (store, tenantId, identifier, content) {
  let axios = createCoreApiCall(store)
  let api = updateTenantConfiguration(axios, tenantId, identifier, content)
  return loaderWrapper(store, api)
}

/**
 * Create a user.
 */
export function _createUser (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createUser(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Get user by username.
 */
export function _getUser (store, username) {
  let axios = createCoreApiCall(store)
  var api = getUser(axios, username)
  return loaderWrapper(store, api)
}

/**
 * Update a user.
 */
export function _updateUser (store, username, payload) {
  let axios = createCoreApiCall(store)
  let api = updateUser(axios, username, payload)
  return loaderWrapper(store, api)
}

/**
 * Delete a user.
 */
export function _deleteUser (store, username, force) {
  let axios = createCoreApiCall(store)
  let api = deleteUser(axios, username, force)
  return loaderWrapper(store, api)
}

/**
 * List users.
 */
export function _listUsers (store, includeDeleted, count) {
  let axios = createCoreApiCall(store)
  let api = listUsers(axios, includeDeleted, count)
  return loaderWrapper(store, api)
}

/**
 * List authorized tenants for currently logged in user.
 */
export function _listTenantsForCurrentUser (store) {
  let axios = createCoreApiCall(store)
  let username = store.getters.user.username
  var api = listUserTenants(axios, username, false)
  return loaderWrapper(store, api)
}

/**
 * Get hierarchical list of granted authorities.
 */
export function _getAuthoritiesHierarchy (store) {
  let axios = createCoreApiCall(store)
  let api = getAuthoritiesHierarchy(axios)
  return loaderWrapper(store, api)
}

/**
 * Create a tenant.
 */
export function _createTenant (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createTenant(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Get a tenant by tenant id.
 */
export function _getTenant (store, tenantId, includeRuntimeInfo) {
  let axios = createCoreApiCall(store)
  let api = getTenant(axios, tenantId, includeRuntimeInfo)
  return loaderWrapper(store, api)
}

/**
 * Update an existing tenant.
 */
export function _updateTenant (store, tenantId, payload) {
  let axios = createCoreApiCall(store)
  let api = updateTenant(axios, tenantId, payload)
  return loaderWrapper(store, api)
}

/**
 * List tenants.
 */
export function _listTenants (store, textSearch, authUserId, includeRuntime,
  paging) {
  let axios = createCoreApiCall(store)
  let api = listTenants(axios, textSearch, authUserId, includeRuntime,
    paging)
  return loaderWrapper(store, api)
}

/**
 * Delete a tenant.
 */
export function _deleteTenant (store, tenantId, force) {
  let axios = createCoreApiCall(store)
  let api = deleteTenant(axios, tenantId, force)
  return loaderWrapper(store, api)
}

/**
 * Get list of tenant templates.
 */
export function _getTenantTemplates (store) {
  let axios = createCoreApiCall(store)
  let api = getTenantTemplates(axios)
  return loaderWrapper(store, api)
}

/**
 * Create a site.
 */
export function _createSite (store, site) {
  let axios = createCoreApiCall(store)
  let api = createSite(axios, site)
  return loaderWrapper(store, api)
}

/**
 * Get a site by unique token.
 */
export function _getSite (store, siteToken) {
  let axios = createCoreApiCall(store)
  let api = getSite(axios, siteToken)
  return loaderWrapper(store, api)
}

/**
 * Update an existing site.
 */
export function _updateSite (store, siteToken, payload) {
  let axios = createCoreApiCall(store)
  let api = updateSite(axios, siteToken, payload)
  return loaderWrapper(store, api)
}

/**
 * List sites.
 */
export function _listSites (store, includeAssignments, includeZones, paging) {
  let axios = createCoreApiCall(store)
  let api = listSites(axios, includeAssignments, includeZones, paging)
  return loaderWrapper(store, api)
}

/**
 * Delete an existing site.
 */
export function _deleteSite (store, siteToken, force) {
  let axios = createCoreApiCall(store)
  let api = deleteSite(axios, siteToken, force)
  return loaderWrapper(store, api)
}

/**
 * List assignments for a given site.
 */
export function _listAssignmentsForSite (store, siteToken, includeDevice,
  includeAsset, paging) {
  let axios = createCoreApiCall(store)
  let api = listAssignmentsForSite(axios, siteToken, includeDevice, includeAsset, paging)
  return loaderWrapper(store, api)
}

/**
 * List location events for a given site.
 */
export function _listLocationsForSite (store, siteToken, paging) {
  let axios = createCoreApiCall(store)
  let api = listLocationsForSite(axios, siteToken, paging)
  return loaderWrapper(store, api)
}

/**
 * List measurement events for a given site.
 */
export function _listMeasurementsForSite (store, siteToken, paging) {
  let axios = createCoreApiCall(store)
  let api = listMeasurementsForSite(axios, siteToken, paging)
  return loaderWrapper(store, api)
}

/**
 * List alert events for a given site.
 */
export function _listAlertsForSite (store, siteToken, paging) {
  let axios = createCoreApiCall(store)
  let api = listAlertsForSite(axios, siteToken, paging)
  return loaderWrapper(store, api)
}

/**
 * List zones for a given site.
 */
export function _listZonesForSite (store, siteToken, paging) {
  let axios = createCoreApiCall(store)
  let api = listZonesForSite(axios, siteToken, paging)
  return loaderWrapper(store, api)
}

/**
 * Create a zone.
 */
export function _createZone (store, siteToken, zone) {
  let axios = createCoreApiCall(store)
  let api = createZone(axios, siteToken, zone)
  return loaderWrapper(store, api)
}

/**
 * Get a zone by unique token.
 */
export function _getZone (store, zoneToken) {
  let axios = createCoreApiCall(store)
  let api = getZone(axios, zoneToken)
  return loaderWrapper(store, api)
}

/**
 * Update an existing zone.
 */
export function _updateZone (store, zoneToken, updated) {
  let axios = createCoreApiCall(store)
  let api = updateZone(axios, zoneToken, updated)
  return loaderWrapper(store, api)
}

/**
 * Delete an existing zone.
 */
export function _deleteZone (store, zoneToken) {
  let axios = createCoreApiCall(store)
  let api = deleteZone(axios, zoneToken)
  return loaderWrapper(store, api)
}

/**
 * Create a device assignment.
 */
export function _createDeviceAssignment (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createDeviceAssignment(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Get a device assignment by unique token.
 */
export function _getDeviceAssignment (store, token) {
  let axios = createCoreApiCall(store)
  let api = getDeviceAssignment(axios, token)
  return loaderWrapper(store, api)
}

/**
 * Delete a device assignment.
 */
export function _deleteDeviceAssignment (store, token, force) {
  let axios = createCoreApiCall(store)
  let api = deleteDeviceAssignment(axios, token, force)
  return loaderWrapper(store, api)
}

/**
 * Create measurements event for a device assignment.
 */
export function _createMeasurementsForAssignment (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = createMeasurementsForAssignment(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * List measurement events for a device assignment.
 */
export function _listMeasurementsForAssignment (store, token, query) {
  let axios = createCoreApiCall(store)
  let api = listMeasurementsForAssignment(axios, token, query)
  return loaderWrapper(store, api)
}

/**
 * Create location event for a device assignment.
 */
export function _createLocationForAssignment (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = createLocationForAssignment(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * List location events for a device assignment.
 */
export function _listLocationsForAssignment (store, token, query) {
  let axios = createCoreApiCall(store)
  let api = listLocationsForAssignment(axios, token, query)
  return loaderWrapper(store, api)
}

/**
 * Create alert event for a device assignment.
 */
export function _createAlertForAssignment (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = createAlertForAssignment(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * List alert events for a device assignment.
 */
export function _listAlertsForAssignment (store, token, query) {
  let axios = createCoreApiCall(store)
  let api = listAlertsForAssignment(axios, token, query)
  return loaderWrapper(store, api)
}

/**
 * Create command invocation for a device assignment.
 */
export function _createCommandInvocationForAssignment (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = createCommandInvocationForAssignment(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * Schedule command invocation for a device assignment.
 */
export function _scheduleCommandInvocation (store, token, schedule, payload) {
  let axios = createCoreApiCall(store)
  let api = scheduleCommandInvocation(axios, token, schedule, payload)
  return loaderWrapper(store, api)
}

/**
 * List command invocation events for a device assignment.
 */
export function _listCommandInvocationsForAssignment (store, token, query) {
  let axios = createCoreApiCall(store)
  let api = listCommandInvocationsForAssignment(axios, token, query)
  return loaderWrapper(store, api)
}

/**
 * List command response events for a device assignment.
 */
export function _listCommandResponsesForAssignment (store, token, query) {
  let axios = createCoreApiCall(store)
  let api = listCommandResponsesForAssignment(axios, token, query)
  return loaderWrapper(store, api)
}

/**
 * Release an assignment.
 */
export function _releaseAssignment (store, token) {
  let axios = createCoreApiCall(store)
  let api = releaseAssignment(axios, token)
  return loaderWrapper(store, api)
}

/**
 * Report an assignment as missing.
 */
export function _missingAssignment (store, token) {
  let axios = createCoreApiCall(store)
  let api = missingAssignment(axios, token)
  return loaderWrapper(store, api)
}

/**
 * Create a new device specification.
 */
export function _createDeviceSpecification (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createDeviceSpecification(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Get a device specification by token.
 */
export function _getDeviceSpecification (store, token) {
  let axios = createCoreApiCall(store)
  let api = getDeviceSpecification(axios, token)
  return loaderWrapper(store, api)
}

/**
 * Get a device specification protocol buffer definition.
 */
export function _getDeviceSpecificationProtobuf (store, token) {
  let axios = createCoreApiCall(store)
  let api = getDeviceSpecificationProtobuf(axios, token)
  return loaderWrapper(store, api)
}

/**
 * Update an existing device specification.
 */
export function _updateDeviceSpecification (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = updateDeviceSpecification(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * List device specifications.
 */
export function _listDeviceSpecifications (store, includeDeleted, includeAsset, paging) {
  let axios = createCoreApiCall(store)
  let api = listDeviceSpecifications(axios, includeDeleted, includeAsset, paging)
  return loaderWrapper(store, api)
}

/**
 * Delete an existing device specification.
 */
export function _deleteDeviceSpecification (store, token, force) {
  let axios = createCoreApiCall(store)
  let api = deleteDeviceSpecification(axios, token, force)
  return loaderWrapper(store, api)
}

/**
 * Create a device command.
 */
export function _createDeviceCommand (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = createDeviceCommand(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * Get a device command by token.
 */
export function _getDeviceCommand (store, token) {
  let axios = createCoreApiCall(store)
  let api = getDeviceCommand(axios, token)
  return loaderWrapper(store, api)
}

/**
 * Update an exiting device command.
 */
export function _updateDeviceCommand (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = updateDeviceCommand(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * List commands for a device specification.
 */
export function _listDeviceCommands (store, token, includeDeleted) {
  let axios = createCoreApiCall(store)
  let api = listDeviceCommands(axios, token, includeDeleted)
  return loaderWrapper(store, api)
}

/**
 * List device specification commands organized by namespace.
 */
export function _listDeviceCommandsByNamespace (store, token, includeDeleted) {
  let axios = createCoreApiCall(store)
  let api = listDeviceCommandsByNamespace(axios, token, includeDeleted)
  return loaderWrapper(store, api)
}

/**
 * Delete a device command.
 */
export function _deleteDeviceCommand (store, token, force) {
  let axios = createCoreApiCall(store)
  let api = deleteDeviceCommand(axios, token, force)
  return loaderWrapper(store, api)
}

/**
 * Create a device status.
 */
export function _createDeviceStatus (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = createDeviceStatus(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * Get a device status by code.
 */
export function _getDeviceStatus (store, token, code) {
  let axios = createCoreApiCall(store)
  let api = getDeviceStatus(axios, token, code)
  return loaderWrapper(store, api)
}

/**
 * Update an existing device status.
 */
export function _updateDeviceStatus (store, token, code, payload) {
  let axios = createCoreApiCall(store)
  let api = updateDeviceStatus(axios, token, code, payload)
  return loaderWrapper(store, api)
}

/**
 * List statuses for a device specification.
 */
export function _listDeviceStatuses (store, token) {
  let axios = createCoreApiCall(store)
  let api = listDeviceStatuses(axios, token)
  return loaderWrapper(store, api)
}

/**
 * Delete a device status.
 */
export function _deleteDeviceStatus (store, token, code) {
  let axios = createCoreApiCall(store)
  let api = deleteDeviceStatus(axios, token, code)
  return loaderWrapper(store, api)
}

/**
 * Create a device.
 */
export function _createDevice (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createDevice(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Update an existing device.
 */
export function _updateDevice (store, hardwareId, payload) {
  let axios = createCoreApiCall(store)
  let api = updateDevice(axios, hardwareId, payload)
  return loaderWrapper(store, api)
}

/**
 * List devices.
 */
export function _listDevices (store, includeSpecification, includeAssignment,
  paging) {
  let axios = createCoreApiCall(store)
  let api = listDevices(axios, includeSpecification, includeAssignment,
    paging)
  return loaderWrapper(store, api)
}

/**
 * List devices.
 */
export function _listFilteredDevices (store, site, specification, includeDeleted,
  excludeAssigned, includeSpecification, includeAssignment, paging) {
  let axios = createCoreApiCall(store)
  let api = listFilteredDevices(axios, site, specification, includeDeleted,
    excludeAssigned, includeSpecification, includeAssignment, paging)
  return loaderWrapper(store, api)
}

/**
 * List assignment history for a device.
 */
export function _listDeviceAssignmentHistory (store, hardwareId, includeAsset,
  includeDevice, includeSite, paging) {
  let axios = createCoreApiCall(store)
  let api = listDeviceAssignmentHistory(axios, hardwareId, includeAsset,
    includeDevice, includeSite, paging)
  return loaderWrapper(store, api)
}

/**
 * Get a device by hardware id.
 */
export function _getDevice (store, hardwareId, includeSpecification,
  includeAssignment, includeSite, includeAsset, includeNested) {
  let axios = createCoreApiCall(store)
  let api = getDevice(axios, hardwareId, includeSpecification,
    includeAssignment, includeSite, includeAsset, includeNested)
  return loaderWrapper(store, api)
}

/**
 * Delete an existing device.
 */
export function _deleteDevice (store, hardwareId, force) {
  let axios = createCoreApiCall(store)
  let api = deleteDevice(axios, hardwareId, force)
  return loaderWrapper(store, api)
}

/**
 * Create a device group.
 */
export function _createDeviceGroup (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createDeviceGroup(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Update an existing device group.
 */
export function _updateDeviceGroup (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = updateDeviceGroup(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * Get a device group by token.
 */
export function _getDeviceGroup (store, token) {
  let axios = createCoreApiCall(store)
  let api = getDeviceGroup(axios, token)
  return loaderWrapper(store, api)
}

/**
 * List device groups.
 */
export function _listDeviceGroups (store, role, includeDeleted, paging) {
  let axios = createCoreApiCall(store)
  let api = listDeviceGroups(axios, role, includeDeleted, paging)
  return loaderWrapper(store, api)
}

/**
 * List device group elements.
 */
export function _listDeviceGroupElements (store, token, includeDetails, paging) {
  let axios = createCoreApiCall(store)
  let api = listDeviceGroupElements(axios, token, includeDetails, paging)
  return loaderWrapper(store, api)
}

/**
 * Add a device group element.
 */
export function _addDeviceGroupElement (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = addDeviceGroupElement(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * Delete a device group element.
 */
export function _deleteDeviceGroupElement (store, token, type, elementId) {
  let axios = createCoreApiCall(store)
  let api = deleteDeviceGroupElement(axios, token, type, elementId)
  return loaderWrapper(store, api)
}

/**
 * Delete a device group.
 */
export function _deleteDeviceGroup (store, token, force) {
  let axios = createCoreApiCall(store)
  let api = deleteDeviceGroup(axios, token, force)
  return loaderWrapper(store, api)
}

/**
 * Get asset modules.
 */
export function _getAssetModules (store, type) {
  let axios = createCoreApiCall(store)
  let api = getAssetModules(axios, type)
  return loaderWrapper(store, api)
}

/**
 * Create an asset category.
 */
export function _createAssetCategory (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createAssetCategory(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Update an asset category.
 */
export function _updateAssetCategory (store, categoryId, payload) {
  let axios = createCoreApiCall(store)
  let api = updateAssetCategory(axios, categoryId, payload)
  return loaderWrapper(store, api)
}

/**
 * Get asset category by unique id.
 */
export function _getAssetCategory (store, id) {
  let axios = createCoreApiCall(store)
  let api = getAssetCategory(axios, id)
  return loaderWrapper(store, api)
}

/**
 * Delete an asset category.
 */
export function _deleteAssetCategory (store, categoryId) {
  let axios = createCoreApiCall(store)
  let api = deleteAssetCategory(axios, categoryId)
  return loaderWrapper(store, api)
}

/**
 * List asset categories.
 */
export function _listAssetCategories (store, paging) {
  let axios = createCoreApiCall(store)
  let api = listAssetCategories(axios, paging)
  return loaderWrapper(store, api)
}

/**
 * List assets for a given category.
 */
export function _listCategoryAssets (store, categoryId, paging) {
  let axios = createCoreApiCall(store)
  let api = listCategoryAssets(axios, categoryId, paging)
  return loaderWrapper(store, api)
}

/**
 * Search asset module for assets matching criteria.
 */
export function _searchAssets (store, moduleId, criteria) {
  let axios = createCoreApiCall(store)
  let api = searchAssets(axios, moduleId, criteria)
  return loaderWrapper(store, api)
}

/**
 * Create a hardware asset.
 */
export function _createHardwareAsset (store, categoryId, payload) {
  let axios = createCoreApiCall(store)
  let api = createHardwareAsset(axios, categoryId, payload)
  return loaderWrapper(store, api)
}

/**
 * Create a person asset.
 */
export function _createPersonAsset (store, categoryId, payload) {
  let axios = createCoreApiCall(store)
  let api = createPersonAsset(axios, categoryId, payload)
  return loaderWrapper(store, api)
}

/**
 * Create a location asset.
 */
export function _createLocationAsset (store, categoryId, payload) {
  let axios = createCoreApiCall(store)
  let api = createLocationAsset(axios, categoryId, payload)
  return loaderWrapper(store, api)
}

/**
 * Get asset by unique id.
 */
export function _getAssetById (store, moduleId, assetId) {
  let axios = createCoreApiCall(store)
  let api = getAssetById(axios, moduleId, assetId)
  return loaderWrapper(store, api)
}

/**
 * Update an existing hardware asset.
 */
export function _updateHardwareAsset (store, moduleId, assetId, payload) {
  let axios = createCoreApiCall(store)
  let api = updateHardwareAsset(axios, moduleId, assetId, payload)
  return loaderWrapper(store, api)
}

/**
 * Update an existing person asset.
 */
export function _updatePersonAsset (store, moduleId, assetId, payload) {
  let axios = createCoreApiCall(store)
  let api = updatePersonAsset(axios, moduleId, assetId, payload)
  return loaderWrapper(store, api)
}

/**
 * Update an existing location asset.
 */
export function _updateLocationAsset (store, moduleId, assetId, payload) {
  let axios = createCoreApiCall(store)
  let api = updateLocationAsset(axios, moduleId, assetId, payload)
  return loaderWrapper(store, api)
}

/**
 * Delete an existing site.
 */
export function _deleteAsset (store, categoryId, assetId) {
  let axios = createCoreApiCall(store)
  let api = deleteAsset(axios, categoryId, assetId)
  return loaderWrapper(store, api)
}

/**
 * Get batch operation by token.
 */
export function _getBatchOperation (store, token) {
  let axios = createCoreApiCall(store)
  let api = getBatchOperation(axios, token)
  return loaderWrapper(store, api)
}

/**
 * List batch operations.
 */
export function _listBatchOperations (store, token, includeDeleted,
  paging) {
  let axios = createCoreApiCall(store)
  let api = listBatchOperations(axios, token, includeDeleted, paging)
  return loaderWrapper(store, api)
}

/**
 * List batch operation elements.
 */
export function _listBatchOperationElements (store, token, paging) {
  let axios = createCoreApiCall(store)
  let api = listBatchOperationElements(axios, token, paging)
  return loaderWrapper(store, api)
}

/**
 * Create a batch command invocation.
 */
export function _createBatchCommandInvocation (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createBatchCommandInvocation(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Create a batch command invocation based on criteria.
 */
export function _createBatchCommandByCriteria (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createBatchCommandByCriteria(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Schedule a batch command invocation based on criteria.
 */
export function _scheduleBatchCommandByCriteria (store, schedule, payload) {
  let axios = createCoreApiCall(store)
  let api = scheduleBatchCommandByCriteria(axios, schedule, payload)
  return loaderWrapper(store, api)
}

/**
 * Create a schedule.
 */
export function _createSchedule (store, payload) {
  let axios = createCoreApiCall(store)
  let api = createSchedule(axios, payload)
  return loaderWrapper(store, api)
}

/**
 * Get a schedule by unique token.
 */
export function _getSchedule (store, token) {
  let axios = createCoreApiCall(store)
  let api = getSchedule(axios, token)
  return loaderWrapper(store, api)
}

/**
 * Update an existing schedule.
 */
export function _updateSchedule (store, token, payload) {
  let axios = createCoreApiCall(store)
  let api = updateSchedule(axios, token, payload)
  return loaderWrapper(store, api)
}

/**
 * Delete a schedule.
 */
export function _deleteSchedule (store, token, force) {
  let axios = createCoreApiCall(store)
  let api = deleteSchedule(axios, token, token, force)
  return loaderWrapper(store, api)
}

/**
 * List schedules.
 */
export function _listSchedules (store, paging) {
  let axios = createCoreApiCall(store)
  let api = listSchedules(axios, paging)
  return loaderWrapper(store, api)
}
