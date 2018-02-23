import {restAuthGet, restAuthPost, restAuthPut, restAuthDelete} from './sitewhere-api'

/**
 * Create a new asset type.
 */
export function createAssetType (axios, assetType) {
  return restAuthPost(axios, 'assettypes/', assetType)
}

/**
 * Get an asset type by unique token.
 */
export function getAssetType (axios, assetTypeToken) {
  return restAuthGet(axios, 'assettypes/' + assetTypeToken)
}

/**
 * Update an existing asset type.
 */
export function updateAssetType (axios, assetTypeToken, payload) {
  return restAuthPut(axios, 'assettypes/' + assetTypeToken, payload)
}

/**
 * List asset types.
 */
export function listAssetTypes (axios, options, paging) {
  let query = ''
  if (paging) {
    query += '?' + paging
  }
  return restAuthGet(axios, 'assettypes' + query)
}

/**
 * Delete an existing asset type.
 */
export function deleteAssetType (axios, assetTypeToken, force) {
  let query = ''
  if (force) {
    query += '?force=true'
  }
  return restAuthDelete(axios, 'assettypes/' + assetTypeToken + query)
}
