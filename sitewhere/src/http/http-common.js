import axios from 'axios'

/**
 * Base URL for SiteWhere server.
 */
export const BASE_URL = `http://localhost:9090/sitewhere/api/`

/**
 * Perform a REST get call.
 */
export function restAuthGet (store, path, success, failed) {
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
    success(response)
  })
  .catch(function (e) {
    failed(e)
  })
}
