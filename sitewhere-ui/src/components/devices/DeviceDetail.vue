<template>
  <div>
    <navigation-page v-if="device" icon="fa-microchip" title="Manage Device">
      <div slot="content">
        <device-detail-header :device="device" @deviceDeleted="onDeviceDeleted">
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
      </div>
      <div slot="actions">
        <navigation-action-button icon="fa-edit" tooltip="Edit Device"
          @action="onEdit">
        </navigation-action-button>
        <navigation-action-button icon="fa-times" tooltip="Delete Device"
          @action="onDelete">
        </navigation-action-button>
      </div>
    </navigation-page>
    <device-update-dialog ref="edit" :token="token"
      @deviceUpdated="onDeviceUpdated">
    </device-update-dialog>
    <device-delete-dialog ref="delete" :token="token"
      @deviceDeleted="onDeviceDeleted">
    </device-delete-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import NavigationActionButton from '../common/NavigationActionButton'
import DeviceDetailHeader from './DeviceDetailHeader'
import DeviceAssignmentHistory from './DeviceAssignmentHistory'
import DeviceUpdateDialog from './DeviceUpdateDialog'
import DeviceDeleteDialog from './DeviceDeleteDialog'

import {_getDevice} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    device: null,
    active: null,
    fab: null
  }),

  components: {
    NavigationPage,
    NavigationActionButton,
    DeviceDetailHeader,
    DeviceAssignmentHistory,
    DeviceUpdateDialog,
    DeviceDeleteDialog
  },

  // Called on initial create.
  created: function () {
    this.display(this.$route.params.token)
  },

  // Called when component is reused.
  beforeRouteUpdate (to, from, next) {
    this.display(to.params.token)
    next()
  },

  methods: {
    // Display entity with the given token.
    display: function (token) {
      this.$data.token = token
      this.refresh()
    },
    // Called to refresh data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Set search options.
      let options = {}
      options.includeDeviceType = true
      options.includeAssignment = true
      options.includeAsset = true
      options.includeNested = true

      // Load information.
      _getDevice(this.$store, token, options)
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
        icon: 'fa-microchip',
        route: '/admin/devices/' + device.token,
        longTitle: 'Manage Device: ' + device.token
      }
      this.$store.commit('currentSection', section)
    },
    // Open dialog to edit device.
    onEdit: function () {
      this.$refs['edit'].onOpenDialog()
    },
    // Called after update.
    onDeviceUpdated: function () {
      this.refresh()
    },
    // Open dialog to delete device.
    onDelete: function () {
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
