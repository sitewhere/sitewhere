<template>
  <div v-if="device">
    <v-app>
      <device-detail-header :device="device" class="mb-3">
      </device-detail-header>
      <v-tabs class="elevation-2" dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
          <v-tabs-item key="assignments" href="#assignments">
            Assignment History
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="assignments" id="assignments">
          <device-assignment-history :device="device"></device-assignment-history>
        </v-tabs-content>
      </v-tabs>
    </v-app>
    <v-speed-dial v-model="fab" direction="top" :hover="true"
      class="action-chooser-fab"
      transition="slide-y-reverse-transition">
      <v-btn slot="activator" class="red darken-1 elevation-5" dark
        fab hover>
        <v-icon fa style="margin-top: -10px;" class="fa-2x">bolt</v-icon>
      </v-btn>
      <v-btn fab dark small class="blue darken-3 elevation-5"
         v-tooltip:left="{ html: 'Update Device' }"
          @click.native="onUpdateDevice">
        <v-icon fa style="margin-top: -3px;">edit</v-icon>
      </v-btn>
      <v-btn fab dark small class="red darken-3 elevation-5"
         v-tooltip:left="{ html: 'Delete Device' }"
          @click.native="onDeleteDevice">
        <v-icon fa style="margin-top: -3px;">remove</v-icon>
      </v-btn>
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
.action-chooser-fab {
  position: absolute;
  bottom: 16px;
  right: 16px;
  z-index: 1000;
}
</style>
