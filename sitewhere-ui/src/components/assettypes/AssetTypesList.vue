<template>
  <navigation-page icon="fa-cog" title="Asset Types">
    <div v-if="assetTypes" slot="content">
      <v-container fluid grid-list-md>
        <v-layout row wrap>
           <v-flex xs6 v-for="(assetType, index) in assetTypes"
            :key="assetType.token">
            <asset-type-list-entry :assetType="assetType"
              @assetTypeOpened="onOpenAssetType" @assetTypeDeleted="refresh">
            </asset-type-list-entry>
          </v-flex>
        </v-layout>
      </v-container>
      <pager :results="results" @pagingUpdated="updatePaging"></pager>
      <asset-type-create-dialog @assetTypeAdded="onAssetTypeAdded"/>
    </div>
  </navigation-page>
</template>

<script>
import NavigationPage from '../common/NavigationPage'
import Pager from '../common/Pager'
import AssetTypeListEntry from './AssetTypeListEntry'
import AssetTypeCreateDialog from './AssetTypeCreateDialog'
import {_listAssetTypes} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    assetTypes: []
  }),

  components: {
    NavigationPage,
    Pager,
    AssetTypeListEntry,
    AssetTypeCreateDialog
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
      _listAssetTypes(this.$store, options, paging)
        .then(function (response) {
          component.results = response.data
          component.$data.assetTypes = response.data.results
        }).catch(function (e) {
        })
    },

    // Called on open.
    onOpenAssetType: function (token) {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.token + '/assettypes/' + token)
      }
    },

    // Called on add.
    onAssetTypeAdded: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
