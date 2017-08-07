/**
 * API calls associated with SiteWhere users.
 */
import {restAuthGet, restAuthPost, restAuthDelete, restAuthPut} from './sitewhere-api'

/**
 * Create a user.
 */
export function createUser (axios, payload) {
  return restAuthPost(axios, '/users', payload)
}

/**
 * Update an existing user.
 */
export function updateUser (axios, username, payload) {
  return restAuthPut(axios, '/users/' + username, payload)
}

/**
 * Get a user by username.
 */
export function getUser (axios, username) {
  return restAuthGet(axios, 'users/' + username)
}

/**
 * Delete an existing user.
 */
export function deleteUser (axios, username, force) {
  let query = ''
  if (force) {
    query += '?force=true'
  }
  return restAuthDelete(axios, 'users/' + username + query)
}

/**
 * List users.
 */
export function listUsers (axios, includeDeleted, count) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  if (count) {
    query += '&count=' + count
  }
  return restAuthGet(axios, 'users' + query)
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
 * Get authorities hierarchy.
 */
export function getAuthoritiesHierarchy (axios) {
  return restAuthGet(axios, 'authorities/hierarchy')
}
