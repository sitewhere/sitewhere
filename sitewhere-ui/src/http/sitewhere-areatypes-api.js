import {restAuthGet, restAuthPost, restAuthPut, restAuthDelete} from './sitewhere-api'

/**
 * Create a new area type.
 */
export function createAreaType (axios, areaType) {
  return restAuthPost(axios, 'areatypes/', areaType)
}

/**
 * Get an area type by unique token.
 */
export function getAreaType (axios, areaTypeToken) {
  return restAuthGet(axios, 'areatypes/' + areaTypeToken)
}

/**
 * Update an existing area type.
 */
export function updateAreaType (axios, areaTypeToken, payload) {
  return restAuthPut(axios, 'areatypes/' + areaTypeToken, payload)
}

/**
 * List area types.
 */
export function listAreaTypes (axios, includeContainedAreaTypes, paging) {
  let query = ''
  query += (includeContainedAreaTypes) ? '?includeContainedAreaTypes=true'
    : '?includeContainedAreaTypes=false'
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'areatypes' + query)
}

/**
 * Delete an existing area type.
 */
export function deleteAreaType (axios, areaTypeToken, force) {
  let query = ''
  if (force) {
    query += '?force=true'
  }
  return restAuthDelete(axios, 'areatypes/' + areaTypeToken + query)
}
