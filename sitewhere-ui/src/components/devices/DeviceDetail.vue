<template>
  <div v-if="device">
    <device-detail-header :device="device" class="mb-3">
    </device-detail-header>
    <v-tabs v-model="active">
      <v-tabs-bar dark color="primary">
        <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
        <v-tabs-item key="assignments" href="#assignments">
          Assignment History
        </v-tabs-item>
      </v-tabs-bar>
      <v-tabs-items>
        <v-tabs-content key="assignments" id="assignments">
          <device-assignment-history :device="device"></device-assignment-history>
        </v-tabs-content>
      </v-tabs-items>
    </v-tabs>
    <v-speed-dial v-model="fab" direction="top" hover fixed bottom right
      class="action-chooser-fab"
      transition="slide-y-reverse-transition">
      <v-tooltip left slot="activator">
        <v-btn slot="activator" class="red darken-1 elevation-5" dark
          fab hover>
          <v-icon style="margin-top: -10px;" class="fa-2x">fa-bolt</v-icon>
        </v-btn>
        <span>Device Actions</span>
      </v-tooltip>
      <v-tooltip left>
        <v-btn fab dark small class="blue darken-3 elevation-5"
            @click="onUpdateDevice" slot="activator">
          <v-icon style="margin-top: -3px;">fa-edit</v-icon>
        </v-btn>
        <span>Update Device</span>
      </v-tooltip>
      <v-tooltip left>
        <v-btn fab dark small class="red darken-3 elevation-5"
            @click="onDeleteDevice" slot="activator">
          <v-icon style="margin-top: -3px;">fa-times</v-icon>
        </v-btn>
        <span>Delete Device</span>
      </v-tooltip>
    </v-speed-dial>
    <device-update-dialog ref="update" :hardwareId="hardwareId"
      @deviceUpdated="refresh">
    </device-update-dialog>
    <device-delete-dialog ref="delete" :hardwareId="hardwareId"
      @deviceDeleted="onDeviceDeleted">
    </device-delete-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import DeviceDetailHeader from './DeviceDetailHeader'
import DeviceAssignmentHistory from './DeviceAssignmentHistory'
import DeviceUpdateDialog from './DeviceUpdateDialog'
import DeviceDeleteDialog from './DeviceDeleteDialog'

import {_getDevice} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    hardwareId: null,
    device: null,
    active: null,
    fab: null
  }),

  components: {
    DeviceDetailHeader,
    DeviceAssignmentHistory,
    DeviceUpdateDialog,
    DeviceDeleteDialog
  },

  created: function () {
    this.$data.hardwareId = this.$route.params.hardwareId
    this.refresh()
  },

  methods: {
    // Called to refresh data.
    refresh: function () {
      var hardwareId = this.$data.hardwareId
      var component = this

      // Load information.
      _getDevice(this.$store, hardwareId, true, true, true, true)
        .then(function (response) {
          component.onDeviceLoaded(response.data)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onDeviceLoaded: function (device) {
      this.$data.device = device
      var section = {
        id: 'devices',
        title: 'Devices',
        icon: 'developer_board',
        route: '/admin/devices/' + device.hardwareId,
        longTitle: 'Manage Device: ' + device.hardwareId
      }
      this.$store.commit('currentSection', section)
    },

    // Called when device update is requested.
    onUpdateDevice: function () {
      this.$refs['update'].onOpenDialog()
    },

    // Called when device delete is requested.
    onDeleteDevice: function () {
      this.$refs['delete'].showDeleteDialog()
    },

    // Called after device is deleted.
    onDeviceDeleted: function () {
      Utils.routeTo(this, '/devices')
    }
  }
}
</script>

<style scoped>
</style>
