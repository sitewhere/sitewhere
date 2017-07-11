import {
  BASE_URL, createAxiosAuthorized,
  getUser, listUserTenants,
  getTenant,
  createSite, getSite, updateSite, listSites, deleteSite, listAssignmentsForSite,
  listLocationsForSite, listMeasurementsForSite, listAlertsForSite,
  listZonesForSite, createZone, getZone, updateZone, deleteZone,
  releaseAssignment, missingAssignment,
  listDeviceSpecifications,
  getAssetModules, searchAssets, getAssetById
} from './sitewhere-api'

/**
 * Create authorized axios client based on store values.
 */
export function createAxiosFromStore (store) {
  var baseUrl = BASE_URL
  var authToken = store.getters.authToken
  var tenantToken = (store.getters.selectedTenant) ? store.getters.selectedTenant.authenticationToken : ''
  return createAxiosAuthorized(baseUrl, authToken, tenantToken)
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
 * Get user by username.
 */
export function _getUser (store, username) {
  let axios = createAxiosFromStore(store)
  var api = getUser(axios, username)
  return loaderWrapper(store, api)
}

/**
 * List authorized tenants for currently logged in user.
 */
export function _listTenantsForCurrentUser (store) {
  let axios = createAxiosFromStore(store)
  let username = store.getters.user.username
  var api = listUserTenants(axios, username, false)
  return loaderWrapper(store, api)
}

/**
 * Get a tenant by tenant id.
 */
export function _getTenant (store, tenantId) {
  let axios = createAxiosFromStore(store)
  let api = getTenant(axios, tenantId)
  return loaderWrapper(store, api)
}

/**
 * Create a site.
 */
export function _createSite (store, site) {
  let axios = createAxiosFromStore(store)
  let api = createSite(axios, site)
  return loaderWrapper(store, api)
}

/**
 * Get a site by unique token.
 */
export function _getSite (store, siteToken) {
  let axios = createAxiosFromStore(store)
  let api = getSite(axios, siteToken)
  return loaderWrapper(store, api)
}

/**
 * Update an existing site.
 */
export function _updateSite (store, siteToken, payload) {
  let axios = createAxiosFromStore(store)
  let api = updateSite(axios, siteToken, payload)
  return loaderWrapper(store, api)
}

/**
 * List sites.
 */
export function _listSites (store, includeAssignments, includeZones, paging) {
  let axios = createAxiosFromStore(store)
  let api = listSites(axios, includeAssignments, includeZones, paging)
  return loaderWrapper(store, api)
}

/**
 * Delete an existing site.
 */
export function _deleteSite (store, siteToken, force) {
  let axios = createAxiosFromStore(store)
  let api = deleteSite(axios, siteToken, force)
  return loaderWrapper(store, api)
}

/**
 * List assignments for a given site.
 */
export function _listAssignmentsForSite (store, siteToken, includeDevice,
  includeAsset, paging) {
  let axios = createAxiosFromStore(store)
  let api = listAssignmentsForSite(axios, siteToken, includeDevice, includeAsset, paging)
  return loaderWrapper(store, api)
}

/**
 * List location events for a given site.
 */
export function _listLocationsForSite (store, siteToken, paging) {
  let axios = createAxiosFromStore(store)
  let api = listLocationsForSite(axios, siteToken, paging)
  return loaderWrapper(store, api)
}

/**
 * List measurement events for a given site.
 */
export function _listMeasurementsForSite (store, siteToken, paging) {
  let axios = createAxiosFromStore(store)
  let api = listMeasurementsForSite(axios, siteToken, paging)
  return loaderWrapper(store, api)
}

/**
 * List alert events for a given site.
 */
export function _listAlertsForSite (store, siteToken, paging) {
  let axios = createAxiosFromStore(store)
  let api = listAlertsForSite(axios, siteToken, paging)
  return loaderWrapper(store, api)
}

/**
 * List zones for a given site.
 */
export function _listZonesForSite (store, siteToken, paging) {
  let axios = createAxiosFromStore(store)
  let api = listZonesForSite(axios, siteToken, paging)
  return loaderWrapper(store, api)
}

/**
 * Create a zone.
 */
export function _createZone (store, siteToken, zone) {
  let axios = createAxiosFromStore(store)
  let api = createZone(axios, siteToken, zone)
  return loaderWrapper(store, api)
}

/**
 * Get a zone by unique token.
 */
export function _getZone (store, zoneToken) {
  let axios = createAxiosFromStore(store)
  let api = getZone(axios, zoneToken)
  return loaderWrapper(store, api)
}

/**
 * Update an existing zone.
 */
export function _updateZone (store, zoneToken, updated) {
  let axios = createAxiosFromStore(store)
  let api = updateZone(axios, zoneToken, updated)
  return loaderWrapper(store, api)
}

/**
 * Delete an existing zone.
 */
export function _deleteZone (store, zoneToken) {
  let axios = createAxiosFromStore(store)
  let api = deleteZone(axios, zoneToken)
  return loaderWrapper(store, api)
}

/**
 * Release an assignment.
 */
export function _releaseAssignment (store, token) {
  let axios = createAxiosFromStore(store)
  let api = releaseAssignment(axios, token)
  return loaderWrapper(store, api)
}

/**
 * Report an assignment as missing.
 */
export function _missingAssignment (store, token) {
  let axios = createAxiosFromStore(store)
  let api = missingAssignment(axios, token)
  return loaderWrapper(store, api)
}

/**
 * List device specifications.
 */
export function _listDeviceSpecifications (store, includeDeleted, includeAsset, paging) {
  let axios = createAxiosFromStore(store)
  let api = listDeviceSpecifications(axios, includeDeleted, includeAsset, paging)
  return loaderWrapper(store, api)
}

/**
 * Get asset modules.
 */
export function _getAssetModules (store, type) {
  let axios = createAxiosFromStore(store)
  let api = getAssetModules(axios, type)
  return loaderWrapper(store, api)
}

/**
 * Search asset module for assets matching criteria.
 */
export function _searchAssets (store, moduleId, criteria) {
  let axios = createAxiosFromStore(store)
  let api = searchAssets(axios, moduleId, criteria)
  return loaderWrapper(store, api)
}

/**
 * Get asset by unique id.
 */
export function _getAssetById (store, moduleId, assetId) {
  let axios = createAxiosFromStore(store)
  let api = getAssetById(axios, moduleId, assetId)
  return loaderWrapper(store, api)
}
