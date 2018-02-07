/**
 * API calls associated with SiteWhere device specifications.
 */
import {restAuthGet, restAuthPost, restAuthPut, restAuthDelete} from './sitewhere-api'

/**
 * Create a device type.
 */
export function createDeviceType (axios, payload) {
  return restAuthPost(axios, '/devicetypes', payload)
}

/**
 * Get device type by unique token.
 */
export function getDeviceType (axios, token) {
  return restAuthGet(axios, '/devicetypes/' + token)
}

/**
 * Get device type protocol buffer definition.
 */
export function getDeviceTypeProtobuf (axios, token) {
  return restAuthGet(axios, '/devicetypes/' + token + '/proto')
}

/**
 * Update an existing device type.
 */
export function updateDeviceType (axios, token, payload) {
  return restAuthPut(axios, '/devicetypes/' + token, payload)
}

/**
 * List device types.
 */
export function listDeviceTypes (axios, includeDeleted, includeAsset, paging) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  query += (includeAsset)
    ? '&includeAsset=true' : '&includeAsset=false'
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devicetypes' + query)
}

/**
 * Delete device type.
 */
export function deleteDeviceType (axios, token, force) {
  let query = ''
  query += (force)
    ? '?force=true' : '?force=false'
  return restAuthDelete(axios, 'devicetypes/' + token + query)
}

/**
 * Create a device command.
 */
export function createDeviceCommand (axios, token, payload) {
  return restAuthPost(axios, '/devicetypes/' + token + '/commands', payload)
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
  return restAuthGet(axios, '/devicetypes/' + token + '/commands' + query)
}

/**
 * List device specification commands by namespace.
 */
export function listDeviceCommandsByNamespace (axios, token, includeDeleted) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  return restAuthGet(axios, 'devicetypes/' + token + '/namespaces' + query)
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
 * Create a device status.
 */
export function createDeviceStatus (axios, token, payload) {
  return restAuthPost(axios, '/devicetypes/' + token + '/statuses', payload)
}

/**
 * Get a device status by code.
 */
export function getDeviceStatus (axios, token, code) {
  return restAuthGet(axios, '/devicetypes/' + token + '/statuses/' + code)
}

/**
 * Update an existing device status.
 */
export function updateDeviceStatus (axios, token, code, payload) {
  return restAuthPut(axios, '/devicetypes/' + token + '/statuses/' + code,
    payload)
}

/**
 * List all device statuses for a device type.
 */
export function listDeviceStatuses (axios, token) {
  return restAuthGet(axios, '/devicetypes/' + token + '/statuses/')
}

/**
 * Delete device status.
 */
export function deleteDeviceStatus (axios, token, code) {
  return restAuthDelete(axios, '/devicetypes/' + token + '/statuses/' + code)
}
