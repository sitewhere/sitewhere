/**
 * API calls associated with SiteWhere device assignments.
 */
import {restAuthGet, restAuthPost, restAuthDelete} from './sitewhere-api'

/**
 * Create a device assignment.
 */
export function createDeviceAssignment (axios, payload) {
  return restAuthPost(axios, 'assignments/', payload)
}

/**
 * Get an assignment by unique token.
 */
export function getDeviceAssignment (axios, token) {
  return restAuthGet(axios, 'assignments/' + token)
}

/**
 * List assignments that match criteria.
 */
export function listDeviceAssignments (axios, options, paging) {
  let query = ''
  query += (options.includeDevice)
    ? '?includeDevice=true' : '?includeDevice=false'
  query += (options.includeArea) ? '&includeArea=true' : ''
  query += (options.includeAsset) ? '&includeAsset=true' : ''
  query += (options.deviceToken) ? '&deviceToken=' + options.deviceToken : ''
  query += (options.areaToken) ? '&areaToken=' + options.areaToken : ''
  query += (options.assetToken) ? '&assetToken=' + options.assetToken : ''
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'assignments' + query)
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
 * Delete a device assignment.
 */
export function deleteDeviceAssignment (axios, token, force) {
  let query = ''
  if (force) {
    query += '?force=true'
  }
  return restAuthDelete(axios, 'assignments/' + token + query)
}

/**
 * Create measurements event for an assignment.
 */
export function createMeasurementsForAssignment (axios, token, payload) {
  return restAuthPost(axios, 'assignments/' + token + '/measurements', payload)
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
 * Create location event for an assignment.
 */
export function createLocationForAssignment (axios, token, payload) {
  return restAuthPost(axios, 'assignments/' + token + '/locations', payload)
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
 * Create alert event for an assignment.
 */
export function createAlertForAssignment (axios, token, payload) {
  return restAuthPost(axios, 'assignments/' + token + '/alerts', payload)
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
export function createCommandInvocationForAssignment (axios, token, payload) {
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
