/**
 * API calls associated with SiteWhere device assignments.
 */
import {restAuthGet, restAuthPost, restAuthDelete, restAuthPut} from './sitewhere-api'

/**
 * Create a device.
 */
export function createDevice (axios, payload) {
  return restAuthPost(axios, '/devices', payload)
}

/**
 * Update an existing device.
 */
export function updateDevice (axios, token, payload) {
  return restAuthPut(axios, '/devices/' + token, payload)
}

/**
 * List devices.
 */
export function listDevices (axios, options, paging) {
  let query = ''
  query += (options.includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  query += (options.excludeAssigned) ? '&excludeAssigned=true' : ''
  query += (options.includeDeviceType) ? '&includeDeviceType=true' : ''
  query += (options.includeAssignment) ? '&includeAssignment=true' : ' '
  query += (options.area) ? '&area=' + options.area : ''
  query += (options.deviceType) ? '&deviceType=' + options.deviceType : ''
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devices' + query)
}

/**
 * Get device by token.
 */
export function getDevice (axios, token, options) {
  let query = ''
  query += (options.includeDeviceType)
    ? '?includeDeviceType=true' : '?includeDeviceType=false'
  query += (options.includeAssignment) ? '&includeAssignment=true' : ''
  query += (options.includeAsset) ? '&includeAsset=true' : ''
  query += (options.includeNested) ? '&includeNested=true' : ''
  return restAuthGet(axios, '/devices/' + token + query)
}

/**
 * Delete device.
 */
export function deleteDevice (axios, token, force) {
  let query = ''
  query += (force)
    ? '?force=true' : '?force=false'
  return restAuthDelete(axios, 'devices/' + token + query)
}

/**
 * List assignment history for a device.
 */
export function listDeviceAssignmentHistory (axios, token, options, paging) {
  let query = ''
  query += (options.includeAsset)
    ? '?includeAsset=true' : '?includeAsset=false'
  query += (options.includeDevice)
    ? '&includeDevice=true' : '&includeDevice=false'
  query += (options.includeSite)
    ? '&includeSite=true' : '&includeSite=false'
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devices/' + token + '/assignments' + query)
}
