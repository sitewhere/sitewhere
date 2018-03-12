<template>
  <v-card :style="panelStyle" class="white pa-2 header-panel">
    <v-card-text>
      <span v-if="imageUrl" class="header-image" :style="imageStyle"></span>
      <span v-if="icon" class="header-image">
        <v-icon :style="iconStyle">{{ icon }}</v-icon>
      </span>
      <span class="header-content">
        <slot name="content"></slot>
      </span>
      <authenticated-image class="header-qrcode" :style="qrCodeStyle"
        :url="qrCodeUrl">
      </authenticated-image>
      <span class="options-menu">
        <slot name="options"></slot>
      </span>
    </v-card-text>
  </v-card>
</template>

<script>
import AuthenticatedImage from '../common/AuthenticatedImage'

export default {

  data: () => ({
  }),

  props: ['height', 'imageUrl', 'qrCodeUrl', 'icon'],

  computed: {
    // Style for top-level panel.
    panelStyle: function () {
      return {
        'min-height': this.height
      }
    },
    // Compute style of image.
    imageStyle: function () {
      return {
        'background-color': '#fff',
        'background-image': 'url(' + this.imageUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    },
    // Compute style for icon.
    iconStyle: function () {
      return {
        'height': '180px',
        'width': '180px',
        'font-size': '80px',
        'padding': '35px',
        'border': '1px solid #eee'
      }
    },
    // Compute style for QR code URL.
    qrCodeStyle: function () {
      return {
        'background-color': '#fff',
        'background-image': 'url(' + this.qrCodeUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    }
  },

  components: {
    AuthenticatedImage
  },

  methods: {
  }
}
</script>

<style scoped>
.header-panel {
  min-width: 920px;
  overflow-y: hidden;
}

.header-image {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.header-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
  height: 180px;
}

.header-content {
  position: absolute;
  top: 20px;
  left: 200px;
  right: 200px;
}

.options-menu {
  position: absolute;
  top: 10px;
  right: 190px;
}
</style>
