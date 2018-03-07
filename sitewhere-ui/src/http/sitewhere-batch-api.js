/**
 * API calls associated with SiteWhere batch operations.
 */
import {restAuthGet, restAuthPost} from './sitewhere-api'

/**
 * Get an assignment by unique token.
 */
export function getBatchOperation (axios, token) {
  return restAuthGet(axios, 'batch/' + token)
}

/**
 * List batch operations.
 */
export function listBatchOperations (axios, token, includeDeleted, paging) {
  let query = ''
  query += (includeDeleted)
    ? '?includeDeleted=true' : '?includeDeleted=false'
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'batch/' + query)
}

/**
 * List batch operation elements.
 */
export function listBatchOperationElements (axios, token, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'batch/' + token + '/elements' + query)
}

/**
 * Create a batch command invocation.
 */
export function createBatchCommandInvocation (axios, payload) {
  return restAuthPost(axios, '/batch/command', payload)
}

/**
 * Create a batch command invocation baesd on criteria.
 */
export function createBatchCommandByCriteria (axios, options, payload) {
  let query = ''
  query += (options.scheduleToken)
    ? '?scheduleToken=' + options.scheduleToken : ''
  return restAuthPost(axios, '/batch/command/criteria' + query, payload)
}
