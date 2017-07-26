/**
 * API calls associated with SiteWhere device groups.
 */
import {restAuthGet} from './sitewhere-api'

/**
 * List sites.
 */
export function listDeviceGroups (axios, role, includeDeleted, paging) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  if (role) {
    query += '&role=' + role
  }
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devicegroups' + query)
}
