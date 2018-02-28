<template>
  <div>
    <div v-if="assetType">
      <v-card-text>
        {{ chosenText }}
      </v-card-text>
      <v-list two-line>
        <v-list-tile avatar :key="assetType.token">
          <v-list-tile-avatar>
            <img :src="assetType.imageUrl"></v-list-tile-avatar>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title v-html="assetType.name"></v-list-tile-title>
            <v-list-tile-sub-title v-html="assetType.description">
            </v-list-tile-sub-title>
          </v-list-tile-content>
          <v-list-tile-action>
            <v-btn icon ripple
              @click.stop="onAssetTypeRemoved(true)">
              <v-icon class="grey--text">remove_circle</v-icon>
            </v-btn>
          </v-list-tile-action>
        </v-list-tile>
      </v-list>
    </div>
    <div v-else>
      <v-card-text>
        {{ notChosenText }}
      </v-card-text>
      <v-list v-if="assetTypes" class="asset-type-list" two-line>
        <template v-for="assetType in assetTypes">
          <v-list-tile avatar :key="assetType.token"
            @click.stop="onAssetTypeChosen(assetType, true)">
            <v-list-tile-avatar>
              <img :src="assetType.imageUrl"></v-list-tile-avatar>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-html="assetType.name"></v-list-tile-title>
              <v-list-tile-sub-title v-html="assetType.description">
              </v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </div>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Lodash from 'lodash'
import {_listAssetTypes} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    assetType: null,
    assetTypes: []
  }),

  props: ['selected', 'selectedToken', 'chosenText', 'notChosenText'],

  // Initially load list of all sites.
  created: function () {
    var component = this

    // Search options.
    let options = {}
    let paging = Utils.pagingForAllResults()
    _listAssetTypes(this.$store, options, paging)
      .then(function (response) {
        component.assetTypes = response.data.results
        if (component.selected) {
          component.onAssetTypeChosen(component.selected)
        }
      }).catch(function (e) {
      })
  },

  watch: {
    selected: function (value) {
      if (value) {
        this.onAssetTypeChosen(value, false)
      } else {
        this.onAssetTypeRemoved(false)
      }
    },
    selectedToken: function (value) {
      let assetType = Lodash.find(this.assetTypes, { 'token': value })
      if (assetType) {
        this.onAssetTypeChosen(assetType, false)
      } else {
        this.onAssetTypeRemoved(false)
      }
    }
  },

  methods: {
    // Called when an asset type is chosen.
    onAssetTypeChosen: function (assetType, emit) {
      this.$data.assetType = assetType
      if (emit) {
        this.$emit('assetTypeUpdated', assetType)
      }
    },

    // Allow another asset type to be chosen.
    onAssetTypeRemoved: function (emit) {
      this.$data.assetType = null
      if (emit) {
        this.$emit('assetTypeUpdated', null)
      }
    }
  }
}
</script>

<style scoped>
.asset-type-list {
  max-height: 300px;
  overflow-y: auto;
}
</style>
