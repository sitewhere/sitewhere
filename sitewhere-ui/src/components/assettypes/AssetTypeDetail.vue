<template>
  <div>
    <navigation-page v-if="assetType" icon="fa-cog" :title="assetType.name">
      <div slot="content">
        <asset-type-detail-header v-if="assetType" :assetType="assetType"
          @assetTypeDeleted="onAssetTypeDeleted"
          @assetTypeUpdated="onAssetTypeUpdated">
        </asset-type-detail-header>
        <v-tabs v-model="active">
          <v-tabs-bar dark color="primary">
            <v-tabs-item key="assets" href="#assets">
              Assets
            </v-tabs-item>
            <v-tabs-slider></v-tabs-slider>
          </v-tabs-bar>
          <v-tabs-items>
            <v-tabs-content key="assets" id="assets">
              <v-container fluid grid-list-md v-if="assets">
                <v-layout row wrap>
                  <v-flex xs6 v-for="(asset, index) in assets" :key="asset.token">
                    <asset-list-entry :asset="asset" @assetOpened="onOpenAsset">
                    </asset-list-entry>
                 </v-flex>
                </v-layout>
              </v-container>
              <pager :results="results" @pagingUpdated="updatePaging">
                <no-results-panel slot="noresults"
                  text="No Assets of This Type Found">
                </no-results-panel>
              </pager>
            </v-tabs-content>
          </v-tabs-items>
        </v-tabs>
      </div>
      <div slot="actions">
        <navigation-action-button icon="fa-edit" tooltip="Edit Asset Type"
          @action="onEdit">
        </navigation-action-button>
        <navigation-action-button icon="fa-times" tooltip="Delete Asset Type"
          @action="onDelete">
        </navigation-action-button>
      </div>
    </navigation-page>
    <asset-type-update-dialog ref="update"
      :token="assetType.token" @assetTypeUpdated="onAssetTypeUpdated">
    </asset-type-update-dialog>
    <asset-type-delete-dialog ref="delete"
      :token="assetType.token" @assetTypeDeleted="onAssetTypeDeleted">
    </asset-type-delete-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import NavigationActionButton from '../common/NavigationActionButton'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import AssetTypeDetailHeader from './AssetTypeDetailHeader'
import AssetTypeDeleteDialog from './AssetTypeDeleteDialog'
import AssetTypeUpdateDialog from './AssetTypeUpdateDialog'
import AssetListEntry from '../assets/AssetListEntry'
import {
  _getAssetType,
  _listAssets
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    assetType: null,
    assets: null,
    paging: null,
    results: null,
    active: null
  }),

  components: {
    NavigationPage,
    NavigationActionButton,
    Pager,
    NoResultsPanel,
    AssetTypeDetailHeader,
    AssetListEntry,
    AssetTypeDeleteDialog,
    AssetTypeUpdateDialog
  },

  // Called on initial create.
  created: function () {
    this.display(this.$route.params.token)
  },

  // Called when component is reused.
  beforeRouteUpdate (to, from, next) {
    this.display(to.params.token)
    next()
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refreshAssets()
    },
    // Display asset type with the given token.
    display: function (token) {
      this.$data.token = token
      this.refresh()
    },
    // Called to refresh asset type data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load area information.
      _getAssetType(this.$store, token)
        .then(function (response) {
          component.onDataLoaded(response.data)
        }).catch(function (e) {
        })
    },
    // Refresh list of assets for type.
    refreshAssets: function () {
      var component = this
      var paging = this.$data.paging.query

      // Query for assets with this asset type.
      let options = {}
      options.assetTypeToken = this.$data.token

      _listAssets(this.$store, options, paging)
        .then(function (response) {
          component.results = response.data
          component.$data.assets = response.data.results
        }).catch(function (e) {
        })
    },
    // Called after data is loaded.
    onDataLoaded: function (assetType) {
      this.$data.assetType = assetType
      var section = {
        id: 'assettypes',
        title: 'Asset Types',
        icon: 'fa-cog',
        route: '/admin/assettypes/' + assetType.token,
        longTitle: 'Manage Asset Type: ' + assetType.name
      }
      this.$store.commit('currentSection', section)
    },
    // Called to open edit dialog.
    onEdit: function () {
      this.$refs['update'].onOpenDialog()
    },
    // Called when asset type is updated.
    onAssetTypeUpdated: function () {
      this.refresh()
    },
    onDelete: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when asset type is deleted.
    onAssetTypeDeleted: function () {
      Utils.routeTo(this, '/assettypes')
    },
    // Open clicked asset.
    onOpenAsset: function (asset) {
      Utils.routeTo(this, '/assets/' + asset.token)
    }
  }
}
</script>

<style scoped>
</style>
