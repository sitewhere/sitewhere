/**
 * API calls associated with SiteWhere tenants.
 */
import {
  restAuthGet,
  restAuthPost,
  restAuthPut,
  restAuthDelete
} from './sitewhere-api'

/**
 * Create a tenant.
 */
export function createTenant (axios, payload) {
  return restAuthPost(axios, '/tenants', payload)
}

/**
 * Get a tenant by tenant id.
 */
export function getTenant (axios, tenantId, includeRuntimeInfo) {
  let query = ''
  query += (includeRuntimeInfo)
    ? '?includeRuntimeInfo=true' : '?includeRuntimeInfo=false'
  return restAuthGet(axios, 'tenants/' + tenantId + query)
}

/**
 * Update an existing tenant.
 */
export function updateTenant (axios, id, payload) {
  return restAuthPut(axios, 'tenants/' + id, payload)
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
 * Delete tenant.
 */
export function deleteTenant (axios, tenantId, force) {
  let query = ''
  query += (force)
    ? '?force=true' : '?force=false'
  return restAuthDelete(axios, 'tenants/' + tenantId + query)
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
