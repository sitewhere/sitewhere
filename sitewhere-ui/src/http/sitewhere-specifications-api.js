/**
 * API calls associated with SiteWhere device specifications.
 */
import {restAuthGet, restAuthPost, restAuthPut, restAuthDelete} from './sitewhere-api'

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
