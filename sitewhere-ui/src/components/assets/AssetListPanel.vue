<template>
  <v-card hover class="white">
    <v-card-text class="asset">
      <div class="asset-image"
        :style="backgroundImageStyle(asset.imageUrl)"></div>
      <span class="asset-name title">
        {{ asset.name }}
      </span>
      <span v-if="isHardwareOrDevice" class="asset-identifier">
        {{ asset.sku }}
      </span>
      <span v-if="isPerson" class="asset-identifier">
        {{ asset.userName }}
      </span>
      <span v-if="isLocation" class="asset-identifier">
        Latitude: {{ asset.latitude }}
      </span>
      <span v-if="isHardwareOrDevice" class="asset-description">
        {{ asset.description }}
      </span>
      <span v-if="isPerson" class="asset-description">
        {{ asset.emailAddress }}
      </span>
      <span v-if="isLocation" class="asset-description">
        Longitude: {{ asset.longitude }}
      </span>
      <asset-update-dialog class="asset-edit mr-2"
        :category="category" :asset="asset" @assetUpdated="refresh">
      </asset-update-dialog>
      <asset-delete-dialog class="asset-delete"
        :category="category" :asset="asset" @assetDeleted="refresh">
      </asset-delete-dialog>
    </v-card-text>
  </v-card>
</template>

<script>
import AssetDeleteDialog from './AssetDeleteDialog'
import AssetUpdateDialog from './AssetUpdateDialog'

export default {

  data: function () {
    return {
      charWidth: 40
    }
  },

  components: {
    AssetDeleteDialog,
    AssetUpdateDialog
  },

  props: ['category', 'asset'],

  computed: {
    isHardwareOrDevice: function () {
      return (this.asset.type === 'Device') || (this.asset.type === 'Hardware')
    },
    isPerson: function () {
      return (this.asset.type === 'Person')
    },
    isLocation: function () {
      return (this.asset.type === 'Location')
    }
  },

  methods: {
    // Create background image style.
    backgroundImageStyle: function (image) {
      return {
        'background-image': 'url(' + image + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%'
      }
    },
    // Fire event to have parent refresh content.
    refresh: function () {
      this.$emit('refresh')
    }
  }
}
</script>

<style scoped>
.asset {
  position: relative;
  min-height: 100px;
  overflow-x: hidden;
}
.asset-image {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 90px;
  background-color: #fff;
  border-right: 1px solid #eee;
}
.asset-name {
  position: absolute;
  top: 6px;
  left: 100px;
  right: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.asset-identifier {
  position: absolute;
  top: 37px;
  left: 100px;
  right: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.asset-description {
  position: absolute;
  top: 65px;
  left: 100px;
  right: 70px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.asset-edit {
  position: absolute;
  bottom: 3px;
  right: 20px;
}
.asset-delete {
  position: absolute;
  bottom: 3px;
  right: 0px;
}
</style>
