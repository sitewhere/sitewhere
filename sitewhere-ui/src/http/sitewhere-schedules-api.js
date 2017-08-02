/**
 * API calls associated with SiteWhere schedules.
 */
import {restAuthGet, restAuthPost, restAuthPut, restAuthDelete} from './sitewhere-api'

/**
 * Create a new site.
 */
export function createSchedule (axios, payload) {
  return restAuthPost(axios, 'schedules', payload)
}

/**
 * Get a schedule by unique token.
 */
export function getSchedule (axios, token) {
  return restAuthGet(axios, 'schedules/' + token)
}

/**
 * Update an existing schedule.
 */
export function updateSchedule (axios, token, payload) {
  return restAuthPut(axios, 'schedules/' + token, payload)
}

/**
 * Delete an existing schedule.
 */
export function deleteSchedule (axios, token, force) {
  let query = ''
  if (force) {
    query += '?force=true'
  }
  return restAuthDelete(axios, 'schedules/' + token + query)
}

/**
 * List schedules.
 */
export function listSchedules (axios, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'schedules' + query)
}
