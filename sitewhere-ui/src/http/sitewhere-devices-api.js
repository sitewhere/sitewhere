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
export function listFilteredDevices (axios, specification, site, includeDeleted,
  excludeAssigned, includeSpecification, includeAssignment, paging) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  query += (excludeAssigned)
    ? '&excludeAssigned=true' : '&excludeAssigned=false'
  query += (includeSpecification)
    ? '&includeSpecification=true' : '&includeSpecification=false'
  query += (includeAssignment)
    ? '&includeAssignment=true' : '&includeAssignment=false'
  query += (specification) ? '?specification=' + specification : ''
  query += (site) ? '?site=' + site : ''
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'devices' + query)
}
