<template>
  <navigation-page v-if="asset" icon="fa-car" :title="asset.name">
    <div slot="content">
      <asset-detail-header v-if="asset" :asset="asset"
        @assetDeleted="onAssetDeleted"
        @assetUpdated="onAssetUpdated">
      </asset-detail-header>
      <v-tabs v-model="active">
        <v-tabs-bar dark color="primary">
          <v-tabs-item key="assignments" href="#assignments">
            Assignments
          </v-tabs-item>
          <v-tabs-slider></v-tabs-slider>
        </v-tabs-bar>
        <v-tabs-items>
          <v-tabs-content key="assignments" id="assignments">
            <v-card></v-card>
          </v-tabs-content>
        </v-tabs-items>
      </v-tabs>
    </div>
  </navigation-page>
</template>

<script>
import NavigationPage from '../common/NavigationPage'
import Pager from '../common/Pager'
import AssetDetailHeader from './AssetDetailHeader'
import {
  _getAsset
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    asset: null,
    active: null
  }),

  components: {
    NavigationPage,
    Pager,
    AssetDetailHeader
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
    // Display asset with the given token.
    display: function (token) {
      this.$data.token = token
      this.refresh()
    },
    // Called to refresh area data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load asset information.
      _getAsset(this.$store, token)
        .then(function (response) {
          component.onDataLoaded(response.data)
        }).catch(function (e) {
        })
    },
    // Called after data is loaded.
    onDataLoaded: function (asset) {
      this.$data.asset = asset
      var section = {
        id: 'assets',
        title: 'Assets',
        icon: 'fa-car',
        route: '/admin/assets/' + asset.token,
        longTitle: 'Manage Asset: ' + asset.name
      }
      this.$store.commit('currentSection', section)
    },
    // Called after asset is deleted.
    onAssetDeleted: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.token + '/assets')
      }
    },
    // Called after asset is updated.
    onAssetUpdated: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
