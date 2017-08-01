/**
 * API calls associated with SiteWhere device groups.
 */
import {restAuthGet, restAuthPost, restAuthPut, restAuthDelete} from './sitewhere-api'

/**
 * Create a device group.
 */
export function createDeviceGroup (axios, payload) {
  return restAuthPost(axios, '/devicegroups', payload)
}

/**
 * Update an existing device group.
 */
export function updateDeviceGroup (axios, token, payload) {
  return restAuthPut(axios, '/devicegroups/' + token, payload)
}

/**
 * Get device group by token.
 */
export function getDeviceGroup (axios, token) {
  return restAuthGet(axios, '/devicegroups/' + token)
}

/**
 * List sites.
 */
export function listDeviceGroups (axios, role, includeDeleted, paging) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  if (role) {
    query += '&role=' + role
  }
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devicegroups' + query)
}

/**
 * List device group elements.
 */
export function listDeviceGroupElements (axios, token, includeDetails, paging) {
  let query = ''
  query += (includeDetails)
    ? '?includeDetails=true' : '?includeDetails=false'
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devicegroups/' + token + '/elements' + query)
}

/**
 * Delete a device group element.
 */
export function deleteDeviceGroupElement (axios, token, type, elementId) {
  return restAuthDelete(axios, 'devicegroups/' + token + '/elements/' +
    type + '/' + elementId)
}

/**
 * Delete device group.
 */
export function deleteDeviceGroup (axios, token, force) {
  let query = ''
  query += (force)
    ? '?force=true' : '?force=false'
  return restAuthDelete(axios, 'devicegroups/' + token + query)
}
