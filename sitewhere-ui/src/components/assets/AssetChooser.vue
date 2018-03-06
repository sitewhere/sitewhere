<template>
  <div v-if="asset">
    <v-card-text class="subheading">
      {{ chosenText }}
    </v-card-text>
    <v-list two-line>
      <v-list-tile avatar :key="asset.token">
        <v-list-tile-avatar>
          <img :src="asset.imageUrl"></v-list-tile-avatar>
        </v-list-tile-avatar>
        <v-list-tile-content>
          <v-list-tile-title v-html="asset.name"></v-list-tile-title>
          <v-list-tile-sub-title v-html="asset.token">
          </v-list-tile-sub-title>
        </v-list-tile-content>
        <v-list-tile-action>
          <v-btn icon ripple @click.stop="onAssetRemoved(true)">
            <v-icon class="grey--text">remove_circle</v-icon>
          </v-btn>
        </v-list-tile-action>
      </v-list-tile>
    </v-list>
  </div>
  <div v-else>
    <v-card-text class="subheading">
      {{ notChosenText }}
    </v-card-text>
    <v-list v-if="assets" class="asset-list" two-line>
      <template v-for="asset in assets">
        <v-list-tile avatar :key="asset.token"
          @click.stop="onAssetChosen(asset, true)">
          <v-list-tile-avatar>
            <img :src="asset.imageUrl"></v-list-tile-avatar>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title v-html="asset.name"></v-list-tile-title>
            <v-list-tile-sub-title v-html="asset.token">
            </v-list-tile-sub-title>
          </v-list-tile-content>
        </v-list-tile>
      </template>
    </v-list>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Lodash from 'lodash'
import {_listAssets} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    asset: null,
    assets: []
  }),

  props: ['selected', 'selectedToken', 'chosenText', 'notChosenText'],

  // Initially load list of all assets.
  created: function () {
    var component = this

    // Search options.
    let options = {}
    options.includeAssetType = true

    let paging = Utils.pagingForAllResults()
    _listAssets(this.$store, options, paging)
      .then(function (response) {
        component.assets = response.data.results
        if (component.selected) {
          component.onAssetChosen(component.selected)
        }
      }).catch(function (e) {
      })
  },

  watch: {
    selected: function (value) {
      if (value) {
        this.onAssetChosen(value, false)
      } else {
        this.onAssetRemoved(false)
      }
    },
    selectedToken: function (value) {
      let asset = Lodash.find(this.assets, { 'token': value })
      if (asset) {
        this.onAssetChosen(asset, false)
      } else {
        this.onAssetRemoved(false)
      }
    }
  },

  methods: {
    // Called when an asset is chosen.
    onAssetChosen: function (asset, emit) {
      this.$data.asset = asset
      if (emit) {
        this.$emit('assetUpdated', asset)
      }
    },

    // Allow another asset to be chosen.
    onAssetRemoved: function (emit) {
      this.$data.asset = null
      if (emit) {
        this.$emit('assetUpdated', null)
      }
    }
  }
}
</script>

<style scoped>
.asset-list {
  border: 1px solid #eee;
  max-height: 300px;
  overflow-y: auto;
}
</style>
