<template>
  <navigation-page icon="fa-car" title="Assets">
    <div v-if="assets" slot="content">
      <v-container fluid grid-list-md>
        <v-layout row wrap>
           <v-flex xs6 v-for="(asset, index) in assets" :key="asset.token">
            <asset-list-entry :asset="asset" @assetOpened="onOpenAsset">
            </asset-list-entry>
          </v-flex>
        </v-layout>
      </v-container>
      <pager :results="results" :pageSizes="pageSizes"
        @pagingUpdated="updatePaging">
      </pager>
      <asset-create-dialog @assetAdded="onAssetAdded"/>
    </div>
  </navigation-page>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import Pager from '../common/Pager'
import AssetListEntry from './AssetListEntry'
import AssetCreateDialog from './AssetCreateDialog'
import {_listAssets} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    assets: [],
    pageSizes: [
      {
        text: '20',
        value: 20
      }, {
        text: '50',
        value: 50
      }, {
        text: '100',
        value: 100
      }
    ]
  }),

  components: {
    NavigationPage,
    Pager,
    AssetListEntry,
    AssetCreateDialog
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh data.
    refresh: function () {
      var paging = this.$data.paging.query
      var component = this
      var options = {}
      _listAssets(this.$store, options, paging)
        .then(function (response) {
          component.results = response.data
          component.$data.assets = response.data.results
        }).catch(function (e) {
        })
    },

    // Called on open.
    onOpenAsset: function (asset) {
      Utils.routeTo(this, '/assets/' + asset.token)
    },

    // Called on add.
    onAssetAdded: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
