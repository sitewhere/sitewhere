<template>
  <v-card hover class="white">
    <v-card-text @click="onOpenDevice" :style="styleForDevice()"
      class="device-root">
      <div class="device-image"
        :style="backgroundImageStyle(device.deviceType.imageUrl)"></div>
      <div class="device-token ellipsis">
        {{ device.token }}
      </div>
      <div class="device-type ellipsis">
        {{ device.deviceType.name }}
      </div>
      <div class="device-comments ellipsis">
        {{ device.comments }}
      </div>
      <div v-if="hasAssignedAsset" class="device-asset"
        :style="backgroundImageStyle(device.assignment.assetImageUrl)"></div>
      <div v-else-if="!device.assignment" class="device-assign-button">
        <v-tooltip top>
          <v-btn dark icon class="blue ml-0"
            @click.stop="onAssignDevice" slot="activator">
            <v-icon>fa-tag</v-icon>
          </v-btn>
          <span>Assign Device</span>
        </v-tooltip>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import Style from '../common/Style'

export default {

  data: function () {
    return {
    }
  },

  components: {
  },

  props: ['device'],

  computed: {
    styleForStatus: function () {
      return Style.styleForAssignmentStatus(this.device.assignment)
    },
    hasAssignedAsset: function () {
      return this.device.assignment && this.device.assignment.assetId
    }
  },

  methods: {
    styleForDevice: function () {
      let style = {}
      style['background-color'] = (this.device.assignment ? '#fff' : '#cff')
      style['border'] = '1px solid ' + (this.device.assignment ? '#fff' : '#6cc')
      return style
    },
    // Create background image style.
    backgroundImageStyle: function (image) {
      return {
        'background-image': 'url(' + image + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%'
      }
    },

    // Called when a device is clicked.
    onOpenDevice: function () {
      this.$emit('deviceOpened', this.device)
    },

    // Open device assignment dialog.
    onAssignDevice: function () {
      this.$emit('assignDevice', this.device)
    },

    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.device-root {
  position: relative;
  min-height: 100px;
  overflow-x: hidden;
}
.device-image {
  position: absolute;
  top: 5px;
  left: 5px;
  bottom: 5px;
  width: 90px;
  background-color: #fff;
  border-right: 1px solid #eee;
}
.device-type {
  position: absolute;
  top: 6px;
  left: 110px;
  right: 10px;
  font-size: 18px;
  color: #333;
  font-weight: 700;
}
.device-token {
  position: absolute;
  top: 40px;
  left: 110px;
  right: 10px;
  font-size: 14px;
  color: #333;
  font-weight: 700;
}
.device-comments {
  position: absolute;
  top: 68px;
  left: 110px;
  right: 10px;
  font-size: 12px;
  color: #333;
}
.device-asset {
  position: absolute;
  bottom: 0px;
  right: 0px;
  width: 50px;
  height: 50px;
  background-color: #fff;
  border: 1px solid #eee;
}
.device-assign-button {
  position: absolute;
  top: 0px;
  right: 0px;
}
</style>
