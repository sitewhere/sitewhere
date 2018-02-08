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
export function updateDevice (axios, hardwareId, payload) {
  return restAuthPut(axios, '/devices/' + hardwareId, payload)
}

/**
 * List devices.
 */
export function listDevices (axios, includeDeviceType, includeAssignment,
  paging) {
  return listFilteredDevices(axios, null, null, false, false,
    includeDeviceType, includeAssignment, paging)
}

/**
 * List devices (with extra filter criteria).
 */
export function listFilteredDevices (axios, site, deviceType,
  includeDeleted, excludeAssigned, includeDeviceType, includeAssignment,
  paging) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  query += (excludeAssigned)
    ? '&excludeAssigned=true' : '&excludeAssigned=false'
  query += (includeDeviceType)
    ? '&includeDeviceType=true' : '&includeDeviceType=false'
  query += (includeAssignment)
    ? '&includeAssignment=true' : '&includeAssignment=false'
  query += (site) ? '&site=' + site : ''
  query += (deviceType) ? '&deviceType=' + deviceType : ''
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devices' + query)
}

/**
 * Get device by hardware id.
 */
export function getDevice (axios, hardwareId, includeSpecification,
  includeAssignment, includeSite, includeAsset, includeNested) {
  let query = ''
  query += (includeSpecification)
    ? '?includeSpecification=true' : '?includeSpecification=false'
  query += (includeAssignment)
    ? '&includeAssignment=true' : '&includeAssignment=false'
  query += (includeSite)
    ? '&includeSite=true' : '&includeSite=false'
  query += (includeAsset)
    ? '&includeAsset=true' : '&includeAsset=false'
  query += (includeNested)
    ? '&includeNested=true' : '&includeNested=false'
  return restAuthGet(axios, '/devices/' + hardwareId + query)
}

/**
 * Delete device.
 */
export function deleteDevice (axios, hardwareId, force) {
  let query = ''
  query += (force)
    ? '?force=true' : '?force=false'
  return restAuthDelete(axios, 'devices/' + hardwareId + query)
}

/**
 * List assignment history for a device.
 */
export function listDeviceAssignmentHistory (axios, hardwareId, includeAsset,
  includeDevice, includeSite, paging) {
  let query = ''
  query += (includeAsset)
    ? '?includeAsset=true' : '?includeAsset=false'
  query += (includeDevice)
    ? '&includeDevice=true' : '&includeDevice=false'
  query += (includeSite)
    ? '&includeSite=true' : '&includeSite=false'
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devices/' + hardwareId + '/assignments' + query)
}
