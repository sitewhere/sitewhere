/**
 * API calls associated with SiteWhere tenants.
 */
import {restAuthGet, restAuthPost, restAuthPut} from './sitewhere-api'

/**
 * Create a tenant.
 */
export function createTenant (axios, payload) {
  return restAuthPost(axios, '/tenants', payload)
}

/**
 * Get a tenant by tenant id.
 */
export function getTenant (axios, tenantId) {
  return restAuthGet(axios, 'tenants/' + tenantId)
}

/**
 * Update an existing tenant.
 */
export function updateTenant (axios, id, payload) {
  return restAuthPut(axios, '/tenants/' + id, payload)
}

/**
 * List tenants.
 */
export function listTenants (axios, textSearch, authUserId, includeRuntimeInfo,
  paging) {
  let query = ''
  query += (includeRuntimeInfo)
    ? '?includeRuntimeInfo=true' : '?includeRuntimeInfo=false'
  if (textSearch) {
    query += '&textSearch=' + textSearch
  }
  if (authUserId) {
    query += '&authUserId=' + authUserId
  }
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'tenants' + query)
}

/**
 * Get list of available tenant templates.
 */
export function getTenantTemplates (axios) {
  return restAuthGet(axios, 'tenants/templates')
}

/**
 * Get current configuration for tenant.
 */
export function getTenantConfiguration (axios, tenantId) {
  return restAuthGet(axios, 'tenants/' + tenantId +
    '/engine/configuration/json')
}

/**
 * Get configuration model for tenant.
 */
export function getTenantConfigurationModel (axios) {
  return restAuthGet(axios, 'tenants/configuration/model')
}

/**
 * Get configuration roles for tenant.
 */
export function getTenantConfigurationRoles (axios) {
  return restAuthGet(axios, 'tenants/configuration/roles')
}
