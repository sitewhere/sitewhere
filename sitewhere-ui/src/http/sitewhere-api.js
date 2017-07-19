import axios from 'axios'

/**
 * Base URL for SiteWhere server.
 */
export const BASE_URL = `http://localhost:9090/sitewhere/api/`

/**
 * Create authorized axios client based on values.
 */
export function createAxiosAuthorized (baseUrl, authToken, tenantToken) {
  let headers = {}
  if (authToken) {
    headers['Authorization'] = 'Basic ' + authToken
  }
  if (tenantToken) {
    headers['X-SiteWhere-Tenant'] = tenantToken
  }
  return axios.create({
    baseURL: baseUrl,
    headers: headers
  })
}

/**
 * Get a user by username.
 */
export function getUser (axios, username) {
  return restAuthGet(axios, 'users/' + username)
}

/**
 * List authorized tenants for a user.
 */
export function listUserTenants (axios, username, includeRuntimeInfo) {
  let query = ''
  if (includeRuntimeInfo) {
    query += '?includeRuntimeInfo=true'
  }
  return restAuthGet(axios, 'users/' + username + '/tenants' + query)
}

/**
 * Get a tenant by tenant id.
 */
export function getTenant (axios, tenantId) {
  return restAuthGet(axios, 'tenants/' + tenantId)
}

/**
 * Create a new site.
 */
export function createSite (axios, site) {
  return restAuthPost(axios, 'sites', site)
}

/**
 * Get a site by unique token.
 */
export function getSite (axios, siteToken) {
  return restAuthGet(axios, 'sites/' + siteToken)
}

/**
 * Update an existing site.
 */
export function updateSite (axios, siteToken, payload) {
  return restAuthPut(axios, 'sites/' + siteToken, payload)
}

/**
 * Delete an existing site.
 */
export function deleteSite (axios, siteToken, force) {
  let query = ''
  if (force) {
    query += '?force=true'
  }
  return restAuthDelete(axios, 'sites/' + siteToken + query)
}

/**
 * List sites.
 */
export function listSites (axios, includeAssignments, includeZones, paging) {
  let query = ''
  query += (includeAssignments)
    ? '?includeAssignments=true' : '?includeAssignments=false'
  query += (includeZones)
    ? '&includeZones=true' : '&includeZones=false'
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'sites' + query)
}

/**
 * List assignments for a site.
 */
export function listAssignmentsForSite (axios, siteToken,
  includeDevice, includeAsset, paging) {
  let query = ''
  query += (includeDevice)
    ? '?includeDevice=true' : '?includeDevice=false'
  query += (includeAsset)
    ? '&includeAsset=true' : '&includeAsset=false'
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'sites/' + siteToken + '/assignments' + query)
}

/**
 * List location events for a site.
 */
export function listLocationsForSite (axios, siteToken, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'sites/' + siteToken + '/locations' + query)
}

/**
 * List measurement events for a site.
 */
export function listMeasurementsForSite (axios, siteToken, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'sites/' + siteToken + '/measurements' + query)
}

/**
 * List alert events for a site.
 */
export function listAlertsForSite (axios, siteToken, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'sites/' + siteToken + '/alerts' + query)
}

/**
 * List zones for a site.
 */
export function listZonesForSite (axios, siteToken, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'sites/' + siteToken + '/zones' + query)
}

/**
 * Create zone.
 */
export function createZone (axios, siteToken, payload) {
  return restAuthPost(axios, '/sites/' + siteToken + '/zones', payload)
}

/**
 * Get zone by unique token.
 */
export function getZone (axios, zoneToken) {
  return restAuthGet(axios, '/zones/' + zoneToken)
}

/**
 * Update an existing zone.
 */
export function updateZone (axios, zoneToken, payload) {
  return restAuthPut(axios, '/zones/' + zoneToken, payload)
}

/**
 * Delete zone.
 */
export function deleteZone (axios, zoneToken) {
  return restAuthDelete(axios, 'zones/' + zoneToken + '?force=true')
}

/**
 * Get an assignment by unique token.
 */
export function getDeviceAssignment (axios, token) {
  return restAuthGet(axios, 'assignments/' + token)
}

/**
 * List measurement events for an assignment.
 */
export function listMeasurementsForAssignment (axios, token, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'assignments/' + token + '/measurements' + query)
}

/**
 * List location events for an assignment.
 */
export function listLocationsForAssignment (axios, token, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'assignments/' + token + '/locations' + query)
}

/**
 * List alert events for an assignment.
 */
export function listAlertsForAssignment (axios, token, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'assignments/' + token + '/alerts' + query)
}

/**
 * Create command invocation for an assignment.
 */
export function createCommandInvocation (axios, token, payload) {
  return restAuthPost(axios, 'assignments/' + token + '/invocations', payload)
}

/**
 * Schedule command invocation for an assignment.
 */
export function scheduleCommandInvocation (axios, token, schedule, payload) {
  return restAuthPost(axios, 'assignments/' + token +
    '/invocations/schedules/' + schedule, payload)
}

/**
 * List command invocation events for an assignment.
 */
export function listCommandInvocationsForAssignment (axios, token, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'assignments/' + token + '/invocations' + query)
}

/**
 * List command invocation events for an assignment.
 */
export function listCommandResponsesForAssignment (axios, token, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'assignments/' + token + '/responses' + query)
}

/**
 * Release an active assignment.
 */
export function releaseAssignment (axios, token) {
  return restAuthPost(axios, '/assignments/' + token + '/end', null)
}

/**
 * Mark an assignment as missing.
 */
export function missingAssignment (axios, token) {
  return restAuthPost(axios, '/assignments/' + token + '/missing', null)
}

/**
 * Create a device specification.
 */
export function createDeviceSpecification (axios, payload) {
  return restAuthPost(axios, '/specifications', payload)
}

/**
 * Get device specification by unique token.
 */
export function getDeviceSpecification (axios, token) {
  return restAuthGet(axios, '/specifications/' + token)
}

/**
 * Get device specification protocol buffer definition.
 */
export function getDeviceSpecificationProtobuf (axios, token) {
  return restAuthGet(axios, '/specifications/' + token + '/proto')
}

/**
 * Update an existing device specification.
 */
export function updateDeviceSpecification (axios, token, payload) {
  return restAuthPut(axios, '/specifications/' + token, payload)
}

/**
 * List device specifications.
 */
export function listDeviceSpecifications (axios, includeDeleted, includeAsset, paging) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  query += (includeAsset)
    ? '&includeAsset=true' : '&includeAsset=false'
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'specifications' + query)
}

/**
 * Delete device specification.
 */
export function deleteDeviceSpecification (axios, token, force) {
  let query = ''
  query += (force)
    ? '?force=true' : '?force=false'
  return restAuthDelete(axios, 'specifications/' + token + query)
}

/**
 * Create a device command.
 */
export function createDeviceCommand (axios, token, payload) {
  return restAuthPost(axios, '/specifications/' + token + '/commands', payload)
}

/**
 * Get a device command by token.
 */
export function getDeviceCommand (axios, token) {
  return restAuthGet(axios, '/commands/' + token)
}

/**
 * Update an existing device command.
 */
export function updateDeviceCommand (axios, token, payload) {
  return restAuthPut(axios, '/commands/' + token, payload)
}

/**
 * List all device commands for a specification.
 */
export function listDeviceCommands (axios, token, includeDeleted) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  return restAuthGet(axios, '/specifications/' + token + '/commands' + query)
}

/**
 * List device specification commands by namespace.
 */
export function listDeviceCommandsByNamespace (axios, token, includeDeleted) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  return restAuthGet(axios, 'specifications/' + token + '/namespaces' + query)
}

/**
 * Delete device command.
 */
export function deleteDeviceCommand (axios, token, force) {
  let query = ''
  query += (force)
    ? '?force=true' : '?force=false'
  return restAuthDelete(axios, 'commands/' + token + query)
}

/**
 * List schedules.
 */
export function listSchedules (axios, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'schedules' + query)
}

/**
 * Get asset modules.
 */
export function getAssetModules (axios, type) {
  let query = ''
  if (type) {
    query += '?assetType=' + type
  }
  return restAuthGet(axios, 'assets/modules' + query)
}

/**
 * Search module assets using the given criteria.
 */
export function searchAssets (axios, moduleId, criteria) {
  let query = ''
  if (criteria) {
    query += '?criteria=' + criteria
  }
  return restAuthGet(axios, 'assets/modules/' + moduleId + '/assets' + query)
}

/**
 * Given an asset module get an asset by id.
 */
export function getAssetById (axios, moduleId, assetId) {
  return restAuthGet(axios, 'assets/modules/' + moduleId + '/assets/' + assetId)
}

/**
 * Perform a REST get call.
 */
function restAuthGet (axios, path) {
  return axios.get(path)
}

/**
 * Perform a REST post call.
 */
function restAuthPost (axios, path, payload) {
  return axios.post(path, payload)
}

/**
 * Perform a REST put call.
 */
function restAuthPut (axios, path, payload) {
  return axios.put(path, payload)
}

/**
 * Perform a REST delete call.
 */
function restAuthDelete (axios, path) {
  return axios.delete(path)
}
