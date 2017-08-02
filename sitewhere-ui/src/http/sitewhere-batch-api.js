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
export function listBatchOperations (axios, token, includeDeleted,
  paging) {
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
    query += '&' + paging
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
export function createBatchCommandByCriteria (axios, payload) {
  return restAuthPost(axios, '/batch/command/criteria', payload)
}

/**
 * Schedule a batch command invocation based on criteria.
 */
export function scheduleBatchCommandByCriteria (axios, schedule, payload) {
  return restAuthPost(axios, '/command/criteria/schedules/' + schedule, payload)
}
