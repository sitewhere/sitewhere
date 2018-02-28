<template>
  <v-card :hover="!headerMode" class="white">
    <v-card-text @click="onOpenAssignment" :style="styleForStatus" class="assn-root">
      <asset-mini-panel class="assn-asset" :assignment="assignment">
      </asset-mini-panel>
      <div class="assn-separator1"></div>
      <device-mini-panel class="assn-device" :device="assignment.device">
      </device-mini-panel>
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
      <assignment-status-button @click.native.stop="ignore" :assignment="assignment" @statusUpdated="refresh"
        v-if="assignment.status !== 'Released'" class="assn-status-button">
      </assignment-status-button>
      <div class="assn-status-value" v-if="assignment.status === 'Released'">Released</div>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import Style from '../common/Style'
import AssetMiniPanel from './AssetMiniPanel'
import DeviceMiniPanel from '../devices/DeviceMiniPanel'
import AssignmentStatusButton from './AssignmentStatusButton'

export default {

  data: function () {
    return {
    }
  },

  components: {
    AssetMiniPanel,
    DeviceMiniPanel,
    AssignmentStatusButton
  },

  props: ['assignment', 'headerMode'],

  computed: {
    styleForStatus: function () {
      return Style.styleForAssignmentStatus(this.assignment)
    }
  },

  methods: {
    ignore: function () {
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
    // Fire event to have parent refresh content.
    refresh: function () {
      this.$emit('refresh')
    },
    // Called when assignment is clicked.
    onOpenAssignment: function () {
      this.$emit('assignmentOpened', this.assignment)
    },
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
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
  bottom: 0px;
  left: 320px;
  width: 330px;
}
.assn-assigned-label {
  position: absolute;
  top: 6px;
  left: 665px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
}
.assn-assigned-value {
  position: absolute;
  top: 6px;
  left: 745px;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
}
.assn-released-label {
  position: absolute;
  top: 32px;
  left: 665px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
}
.assn-released-value {
  position: absolute;
  top: 32px;
  left: 745px;
  font-size: 12px;
  color: #333;
  white-space: nowrap;
}
.assn-status-label {
  position: absolute;
  top: 58px;
  left: 665px;
  font-size: 12px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
}
.assn-status-value {
  position: absolute;
  top: 58px;
  left: 745px;
  font-size: 12px;
}
.assn-separator1 {
  position:absolute;
  width: 10px;
  border-left: 1px solid #ddd;
  top: 10px;
  bottom: 10px;
  left: 310px;
}
.assn-separator2 {
  position:absolute;
  width: 10px;
  border-left: 1px solid #ddd;
  top: 10px;
  bottom: 10px;
  left: 645px;
}
.assn-status-button {
  position: absolute;
  top: 58px;
  left: 745px;
  height: 20px;
  margin-top: -4px;
}
</style>
