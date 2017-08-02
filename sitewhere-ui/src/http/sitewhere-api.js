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
 * Perform a REST get call.
 */
export function restAuthGet (axios, path) {
  return axios.get(path)
}

/**
 * Perform a REST post call.
 */
export function restAuthPost (axios, path, payload) {
  return axios.post(path, payload)
}

/**
 * Perform a REST put call.
 */
export function restAuthPut (axios, path, payload) {
  return axios.put(path, payload)
}

/**
 * Perform a REST delete call.
 */
export function restAuthDelete (axios, path) {
  return axios.delete(path)
}
