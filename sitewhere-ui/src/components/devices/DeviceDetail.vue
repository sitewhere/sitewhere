<template>
  <navigation-page v-if="device" icon="developer_board" title="Manage Device">
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
  </navigation-page>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import DeviceDetailHeader from './DeviceDetailHeader'
import DeviceAssignmentHistory from './DeviceAssignmentHistory'

import {_getDevice} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    hardwareId: null,
    device: null,
    active: null,
    fab: null
  }),

  components: {
    NavigationPage,
    DeviceDetailHeader,
    DeviceAssignmentHistory
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

    // Called after device is deleted.
    onDeviceDeleted: function () {
      Utils.routeTo(this, '/devices')
    }
  }
}
</script>

<style scoped>
</style>
