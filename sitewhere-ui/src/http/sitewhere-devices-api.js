/**
 * API calls associated with SiteWhere device assignments.
 */
import {restAuthGet} from './sitewhere-api'

/**
 * List devices.
 */
export function listDevices (axios, includeSpecification, includeAssignment,
  paging) {
  return listFilteredDevices(axios, null, null, false, false,
    includeSpecification, includeAssignment, paging)
}

/**
 * List devices (with extra filter criteria).
 */
export function listFilteredDevices (axios, site, specification,
  includeDeleted, excludeAssigned, includeSpecification, includeAssignment,
  paging) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  query += (excludeAssigned)
    ? '&excludeAssigned=true' : '&excludeAssigned=false'
  query += (includeSpecification)
    ? '&includeSpecification=true' : '&includeSpecification=false'
  query += (includeAssignment)
    ? '&includeAssignment=true' : '&includeAssignment=false'
  query += (site) ? '&site=' + site : ''
  query += (specification) ? '&specification=' + specification : ''
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devices' + query)
}
