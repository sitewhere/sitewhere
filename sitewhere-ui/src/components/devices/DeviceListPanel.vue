<template>
  <v-card hover class="white">
    <v-card-text :style="styleForDevice()" class="device-root">
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
      <div v-if="isAssociated" class="device-asset"
        :style="backgroundImageStyle(device.assignment.assetImageUrl)"></div>
      <div v-else-if="!device.assignment" class="device-assign-button">
        <v-btn dark icon
          class="blue ml-0"
          v-tooltip:left="{ html: 'Assign Device' }"
          @click.native.stop="onAssignDevice">
          <v-icon fa class="fa-lg">tag</v-icon>
        </v-btn>
      </div>
      <assignment-create-dialog ref="assign" :hardwareId="device.hardwareId"
        @created="onDeviceAssigned"/>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import Style from '../common/Style'
import AssignmentCreateDialog from '../assignments/AssignmentCreateDialog'

export default {

  data: function () {
    return {
      charWidth: 40
    }
  },

  components: {
    AssignmentCreateDialog
  },

  props: ['device'],

  computed: {
    styleForStatus: function () {
      return Style.styleForAssignmentStatus(this.device.assignment)
    },
    isAssociated: function () {
      return this.device.assignment &&
        (this.device.assignment.assignmentType === 'Associated')
    }
  },

  methods: {
    styleForDevice: function () {
      let style = {}
      style['background-color'] = (this.device.assignment ? '#fff' : '#cff')
      style['border'] = '1px solid' + (this.device.assignment ? 'none' : '#6cc')
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

    // Fire event to have parent refresh content.
    refresh: function () {
      this.$emit('refresh')
    },

    // Open device assignment dialog.
    onAssignDevice: function () {
      this.$refs['assign'].onOpenDialog()
    },

    // Fire event to indicate device should be assigned.
    onDeviceAssigned: function () {
      this.$emit('assigned', this.device)
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
  top: 5px;
  left: 5px;
  bottom: 5px;
  width: 90px;
  background-color: #fff;
  border-right: 1px solid #eee;
}
.device-specification {
  position: absolute;
  top: 6px;
  left: 110px;
  font-size: 18px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
  overflow-x: hidden;
}
.device-hardware-id {
  position: absolute;
  top: 40px;
  left: 110px;
  font-size: 14px;
  color: #333;
  font-weight: 700;
  white-space: nowrap;
  overflow-x: hidden;
}
.device-comments {
  position: absolute;
  top: 68px;
  left: 110px;
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
.device-assign-button {
  position: absolute;
  top: 0px;
  right: 0px;
}
</style>
