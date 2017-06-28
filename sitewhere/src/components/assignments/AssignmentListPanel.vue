<template>
  <v-card hover class="white">
    <v-card-row :style="styleForStatus" class="assn-root">
      <asset-mini-panel class="assn-asset" :assignment="assignment"></asset-mini-panel>
      <div class="assn-separator1"></div>
      <device-mini-panel class="assn-device" :assignment="assignment"></device-mini-panel>
      <div class="assn-separator2"></div>

      <div class="assn-assigned-label">Assigned:</div>
      <div class="assn-assigned-value">
        {{ formatDate(assignment.activeDate) }}
      </div>
      <div class="assn-released-label">Released:</div>
      <div class="assn-released-value">
        {{ formatDate(assignment.releasedDate) }}
      </div>
      <div class="assn-status-label">Status:</div>
    </v-card-row>
  </v-card>
</template>

<script>
import AssetMiniPanel from './AssetMiniPanel'
import DeviceMiniPanel from './DeviceMiniPanel'

export default {

  data: () => ({
  }),

  components: {
    AssetMiniPanel,
    DeviceMiniPanel
  },

  props: ['assignment'],

  computed: {
    styleForStatus: function () {
      return {
        'background-color': this.backgroundColor,
        'border': '1px solid ' + this.borderColor,
        'border-top': '5px solid ' + this.headerColor
      }
    },
    // Get background color for panel.
    backgroundColor: function () {
      if (this.assignment.status === 'Active') {
        return '#f5fff5'
      } else if (this.assignment.status === 'Missing') {
        return '#fff5f5'
      } else {
        return '#f0f0f0'
      }
    },
    // Get border color for panel.
    borderColor: function () {
      if (this.assignment.status === 'Active') {
        return '#99cc99'
      } else if (this.assignment.status === 'Missing') {
        return '#cc9999'
      } else {
        return '#dcdcdc'
      }
    },
    // Get header color for panel.
    headerColor: function () {
      if (this.assignment.status === 'Active') {
        return '#007700'
      } else if (this.assignment.status === 'Missing') {
        return '#dc0000'
      } else {
        return '#333333'
      }
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
    // Format date.
    formatDate: function (date) {
      if (!date) {
        return 'N/A'
      }
      return this.$moment(date).format('YYYY-MM-DD H:mm:ss')
    }
  }
}
</script>

<style scoped>
.assn-root {
  position: relative;
  min-height: 90px;
  overflow-x: hidden;
}
.assn-asset {
  position: absolute;
  top: 0px;
  left: 0px;
  width: 100%
}
.assn-device {
  position: absolute;
  top: 0px;
  left: 310px;
  width: 100%
}
.assn-assigned-label {
  position: absolute;
  top: 6px;
  left: 645px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
}
.assn-assigned-value {
  position: absolute;
  top: 6px;
  left: 725px;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
}
.assn-released-label {
  position: absolute;
  top: 32px;
  left: 645px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
}
.assn-released-value {
  position: absolute;
  top: 32px;
  left: 725px;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
}
.assn-status-label {
  position: absolute;
  top: 58px;
  left: 645px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
}
.assn-separator1 {
  position:absolute;
  width: 10px;
  border-left: 1px solid #ddd;
  top: 10px;
  bottom: 10px;
  left: 300px;
}
.assn-separator2 {
  position:absolute;
  width: 10px;
  border-left: 1px solid #ddd;
  top: 10px;
  bottom: 10px;
  left: 625px;
}
</style>
