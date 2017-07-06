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
