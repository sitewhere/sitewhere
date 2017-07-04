import axios from 'axios'

/**
 * Base URL for SiteWhere server.
 */
export const BASE_URL = `http://localhost:9090/sitewhere/api/`

/**
 * Perform a REST get call.
 */
export function restAuthGet (store, path, success, failed) {
  store.commit('startLoading')
  var auth = store.getters.authToken
  var tenant = (store.getters.selectedTenant) ? store.getters.selectedTenant.authenticationToken : ''
  var http = axios.create({
    baseURL: BASE_URL,
    headers: {
      Authorization: 'Basic ' + auth,
      'X-SiteWhere-Tenant': tenant
    }
  })
  http.get(path)
  .then(function (response) {
    store.commit('stopLoading')
    success(response)
  })
  .catch(function (e) {
    store.commit('stopLoading')
    failed(e)
  })
}

/**
 * Perform a REST post call.
 */
export function restAuthPost (store, path, payload, success, failed) {
  store.commit('startLoading')
  var auth = store.getters.authToken
  var tenant = (store.getters.selectedTenant) ? store.getters.selectedTenant.authenticationToken : ''
  var http = axios.create({
    baseURL: BASE_URL,
    headers: {
      Authorization: 'Basic ' + auth,
      'X-SiteWhere-Tenant': tenant
    }
  })
  http.post(path, payload)
  .then(function (response) {
    store.commit('stopLoading')
    success(response)
  })
  .catch(function (e) {
    store.commit('stopLoading')
    failed(e)
  })
}

/**
 * Perform a REST put call.
 */
export function restAuthPut (store, path, payload, success, failed) {
  store.commit('startLoading')
  var auth = store.getters.authToken
  var tenant = (store.getters.selectedTenant) ? store.getters.selectedTenant.authenticationToken : ''
  var http = axios.create({
    baseURL: BASE_URL,
    headers: {
      Authorization: 'Basic ' + auth,
      'X-SiteWhere-Tenant': tenant
    }
  })
  http.put(path, payload)
  .then(function (response) {
    store.commit('stopLoading')
    success(response)
  })
  .catch(function (e) {
    store.commit('stopLoading')
    failed(e)
  })
}

/**
 * Perform a REST delete call.
 */
export function restAuthDelete (store, path, success, failed) {
  store.commit('startLoading')
  var auth = store.getters.authToken
  var tenant = (store.getters.selectedTenant) ? store.getters.selectedTenant.authenticationToken : ''
  var http = axios.create({
    baseURL: BASE_URL,
    headers: {
      Authorization: 'Basic ' + auth,
      'X-SiteWhere-Tenant': tenant
    }
  })
  http.delete(path)
  .then(function (response) {
    store.commit('stopLoading')
    success(response)
  })
  .catch(function (e) {
    store.commit('stopLoading')
    failed(e)
  })
}
