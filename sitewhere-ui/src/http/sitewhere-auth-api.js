/**
 * API calls associated with authentication.
 */
import {restAuthGet} from './sitewhere-api'

/**
 * Get new JWT.
 */
export function getJwt (axios) {
  return restAuthGet(axios, 'jwt')
}
