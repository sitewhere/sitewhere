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
    token: null,
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
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  methods: {
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

    // Called after device is deleted.
    onDeviceDeleted: function () {
      Utils.routeTo(this, '/devices')
    }
  }
}
</script>

<style scoped>
</style>
