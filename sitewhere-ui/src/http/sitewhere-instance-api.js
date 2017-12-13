/**
 * API calls associated with SiteWhere instance.
 */
import {
  restAuthGet
} from './sitewhere-api'

/**
 * Get list of all microservices in current topology.
 */
export function getTopology (axios) {
  return restAuthGet(axios, 'instance/topology')
}

/**
 * Get list of global microservices in current topology.
 */
export function getGlobalTopology (axios) {
  return restAuthGet(axios, 'instance/topology/global')
}

/**
 * Get list of tenant microservices in current topology.
 */
export function getTenantTopology (axios) {
  return restAuthGet(axios, 'instance/topology/tenant')
}

/**
 * Get configuration model for a given microservice identifier.
 */
export function getConfigurationModel (axios, identifier) {
  return restAuthGet(axios, 'instance/microservice/' + identifier +
    '/configuration/model')
}
