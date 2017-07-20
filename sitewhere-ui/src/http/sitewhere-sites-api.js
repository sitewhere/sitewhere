/**
 * API calls associated with SiteWhere device assignments.
 */
import {restAuthGet, restAuthPost, restAuthPut, restAuthDelete} from './sitewhere-api'

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
