/**
 * API calls associated with SiteWhere assets.
 */
import {restAuthGet, restAuthPost, restAuthDelete, restAuthPut} from './sitewhere-api'

/**
 * Get asset modules.
 */
export function getAssetModules (axios, type) {
  let query = ''
  if (type) {
    query += '?assetType=' + type
  }
  return restAuthGet(axios, 'assets/modules' + query)
}

/**
 * Create an asset category.
 */
export function createAssetCategory (axios, payload) {
  return restAuthPost(axios, '/assets/categories', payload)
}

/**
 * Update an existing asset category.
 */
export function updateAssetCategory (axios, categoryId, payload) {
  return restAuthPut(axios, '/assets/categories/' + categoryId, payload)
}

/**
 * List asset categories.
 */
export function listAssetCategories (axios, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'assets/categories' + query)
}

/**
 * Get asset category by id.
 */
export function getAssetCategory (axios, id) {
  return restAuthGet(axios, 'assets/categories/' + id)
}

/**
 * Delete asset category.
 */
export function deleteAssetCategory (axios, categoryId) {
  return restAuthDelete(axios, 'assets/categories/' + categoryId)
}

/**
 * Create a hardware asset.
 */
export function createHardwareAsset (axios, categoryId, payload) {
  return restAuthPost(axios,
    '/assets/categories/' + categoryId + '/hardware', payload)
}

/**
 * Create a person asset.
 */
export function createPersonAsset (axios, categoryId, payload) {
  return restAuthPost(axios,
    '/assets/categories/' + categoryId + '/persons', payload)
}

/**
 * Create a location asset.
 */
export function createLocationAsset (axios, categoryId, payload) {
  return restAuthPost(axios,
    '/assets/categories/' + categoryId + '/locations', payload)
}

/**
 * List assets for a category.
 */
export function listCategoryAssets (axios, categoryId, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios,
    'assets/categories/' + categoryId + '/assets' + query)
}

/**
 * Search module assets using the given criteria.
 */
export function searchAssets (axios, moduleId, criteria) {
  let query = ''
  if (criteria) {
    query += '?criteria=' + criteria
  }
  return restAuthGet(axios, 'assets/modules/' + moduleId + '/assets' + query)
}

/**
 * Given an asset module get an asset by id.
 */
export function getAssetById (axios, moduleId, assetId) {
  return restAuthGet(axios, 'assets/modules/' + moduleId + '/assets/' + assetId)
}

/**
 * Update a hardware asset.
 */
export function updateHardwareAsset (axios, categoryId, assetId, payload) {
  return restAuthPut(axios,
    'assets/categories/' + categoryId + '/hardware/' + assetId, payload)
}

/**
 * Update a person asset.
 */
export function updatePersonAsset (axios, categoryId, assetId, payload) {
  return restAuthPut(axios,
    'assets/categories/' + categoryId + '/persons/' + assetId, payload)
}

/**
 * Update a person asset.
 */
export function updateLocationAsset (axios, categoryId, assetId, payload) {
  return restAuthPut(axios,
    'assets/categories/' + categoryId + '/locations/' + assetId, payload)
}

/**
 * Delete asset.
 */
export function deleteAsset (axios, categoryId, assetId) {
  return restAuthDelete(axios,
    'assets/categories/' + categoryId + '/assets/' + assetId)
}
