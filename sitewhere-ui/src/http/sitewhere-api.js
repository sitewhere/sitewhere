import axios from 'axios'

/**
 * Create basic auth axios client for retrieving JWT based on credentials.
 */
export function createAxiosBasicAuth (baseUrl, authToken) {
  let headers = {}
  if (authToken) {
    headers['Authorization'] = 'Basic ' + authToken
  }
  return axios.create({
    baseURL: baseUrl,
    headers: headers
  })
}

/**
 * Create basic auth axios client for retrieving JWT based on credentials.
 */
export function createAxiosJwt (baseUrl, jwt, tenantToken) {
  let headers = {}
  if (jwt) {
    headers['Authorization'] = 'Bearer ' + jwt
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
 * Perform a REST post call with progress monitoring.
 */
export function restAuthPostWithProgress (axios, path, payload, callback) {
  let config = {
    onDownloadProgress: callback
  }
  return axios.post(path, payload, config)
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
