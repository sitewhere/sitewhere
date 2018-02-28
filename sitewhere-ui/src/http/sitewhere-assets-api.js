/**
 * API calls associated with SiteWhere assets.
 */
import {restAuthGet, restAuthPost, restAuthDelete, restAuthPut} from './sitewhere-api'

/**
 * Create an asset.
 */
export function createAsset (axios, payload) {
  return restAuthPost(axios,
    '/assets', payload)
}

/**
 * Get an asset by token.
 */
export function getAsset (axios, token) {
  return restAuthGet(axios, 'assets/' + encodeURIComponent(token))
}

/**
 * Update an existing asset.
 */
export function updateAsset (axios, token, payload) {
  return restAuthPut(axios, 'assets/' + token, payload)
}

/**
 * List assets.
 */
export function listAssets (axios, options, paging) {
  let query = ''
  query += (options.includeAssetType)
    ? '?includeAssetType=true' : '?includeAssetType=false'
  query += (options.assetTypeToken)
    ? '&assetTypeToken=' + options.assetTypeToken : ''
  if (paging) {
    query += '&' + paging
  }
  return restAuthGet(axios, 'assets' + query)
}

/**
 * Delete an existing asset type.
 */
export function deleteAsset (axios, token, force) {
  let query = ''
  if (force) {
    query += '?force=true'
  }
  return restAuthDelete(axios, 'assets/' + token + query)
}
