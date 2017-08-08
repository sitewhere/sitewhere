<template>
  <v-card hover class="white">
    <v-card-text :style="styleForStatus" class="device-root">
      <div class="device-image"
        :style="backgroundImageStyle(device.specification.assetImageUrl)"></div>
      <div class="device-hardware-id">
        {{ ellipsis(device.hardwareId, charWidth) }}
      </div>
      <div class="device-specification">
        {{ ellipsis(device.specification.name, charWidth) }}
      </div>
      <div class="device-comments">
        {{ ellipsis(device.comments, charWidth)  }}
      </div>
      <div v-if="device.assignment" class="device-asset"
        :style="backgroundImageStyle(device.assignment.assetImageUrl)"></div>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import Style from '../common/Style'

export default {

  data: function () {
    return {
      charWidth: 40
    }
  },

  components: {
  },

  props: ['device'],

  computed: {
    styleForStatus: function () {
      return Style.styleForAssignmentStatus(this.device.assignment)
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
    },

    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    },

    // Shortened string with ellipsis.
    ellipsis: function (val, max) {
      return Utils.ellipsis(val, max)
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
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 90px;
  background-color: #fff;
  border-right: 1px solid #eee;
}
.device-specification {
  position: absolute;
  top: 6px;
  left: 100px;
  font-size: 18px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
  overflow-x: hidden;
}
.device-hardware-id {
  position: absolute;
  top: 37px;
  left: 100px;
  font-size: 14px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
  overflow-x: hidden;
}
.device-comments {
  position: absolute;
  top: 65px;
  left: 100px;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
  overflow-x: hidden;
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
</style>
