<template>
  <div>
    <div v-if="asset">
      <v-list two-line>
        <v-list-tile avatar :key="asset.id">
          <v-list-tile-avatar>
            <img :src="asset.imageUrl"></v-list-tile-avatar>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title v-html="asset.name"></v-list-tile-title>
            <v-list-tile-sub-title v-html="asset.description"></v-list-tile-sub-title>
          </v-list-tile-content>
          <v-list-tile-action>
            <v-btn icon ripple @click.native.stop="onAssetRemoved">
              <v-icon class="grey--text">remove_circle</v-icon>
            </v-btn>
          </v-list-tile-action>
        </v-list-tile>
      </v-list>
    </div>
    <div v-else>
      <v-text-field label="Enter search criteria" v-model="search"
        append-icon="search">
      </v-text-field>
      <v-list v-if="assets" class="asset-list" two-line>
        <template v-for="asset in assets">
          <v-list-tile avatar :key="asset.id" @click.native.stop="onAssetChosen(asset.id)">
            <v-list-tile-avatar>
              <img :src="asset.imageUrl"></v-list-tile-avatar>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-html="asset.name"></v-list-tile-title>
              <v-list-tile-sub-title v-html="asset.description"></v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </div>
  </div>
</template>

<script>
import debounce from 'lodash/debounce'
import {_searchAssets, _getAssetById} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    asset: null,
    assets: [],
    search: null
  }),

  props: ['assetModuleId', 'assetId'],

  // Add debounce function for issuing asset criteria queries.
  created: function () {
    var component = this
    this.$data.assetSearch = debounce(function (value) {
      _searchAssets(component.$store, component.assetModuleId, value)
        .then(function (response) {
          component.assets = response.data.results
        }).catch(function (e) {
        })
    }, 250)
  },

  watch: {
    // Update asset matches when asset module changes.
    assetModuleId: function (value) {
      var component = this
      component.asset = null
      _searchAssets(this.$store, value, '')
        .then(function (response) {
          component.assets = response.data.results
        }).catch(function (e) {
        })
    },

    // Asset id passed from external.
    assetId: function (value) {
      this.onAssetChosen(value)
    },

    // Asset search value updated.
    search: function (updated) {
      this.onAssetSearchUpdated(updated)
    }
  },

  methods: {
    // Called each time search criteria is updated.
    onAssetSearchUpdated: function (value) {
      this.$data.assetSearch(value)
    },

    // Called when an asset is chosen.
    onAssetChosen: function (assetId) {
      var component = this
      _getAssetById(this.$store, this.assetModuleId, assetId)
        .then(function (response) {
          component.asset = response.data
          component.$emit('assetUpdated', response.data)
        }).catch(function (e) {
        })
    },

    // Allow another asset to be chosen.
    onAssetRemoved: function () {
      this.$data.asset = null
      this.$emit('assetUpdated', null)
    }
  }
}
</script>

<style scoped>
.asset-list {
  max-height: 300px;
  overflow-y: scroll;
}
</style>
