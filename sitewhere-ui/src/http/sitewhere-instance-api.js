/**
 * API calls associated with SiteWhere instance.
 */
import {
  restAuthGet,
  restAuthPost
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

/**
 * Get global microservice configuration based on identifier.
 */
export function getGlobalConfiguration (axios, identifier) {
  return restAuthGet(axios, 'instance/microservice/' + identifier +
    '/configuration')
}
/**
 * Get tenant microservice configuration based on identifier.
 */
export function getTenantConfiguration (axios, tenantId, identifier) {
  return restAuthGet(axios, 'instance/microservice/' + identifier +
    '/configuration/' + tenantId)
}

/**
 * Update global microservice configuration based on identifier.
 */
export function updateGlobalConfiguration (axios, identifier, config) {
  return restAuthPost(axios, 'instance/microservice/' + identifier +
    '/configuration', config)
}

/**
 * Update tenant microservice configuration based on identifier.
 */
export function updateTenantConfiguration (axios, tenantId, identifier, config) {
  return restAuthPost(axios, 'instance/microservice/' + identifier +
    '/configuration/' + tenantId, config)
}
