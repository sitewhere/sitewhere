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
        </v-tabs-content>
      </v-tabs>
    </v-app>
  </div>
</template>

<script>
import DeviceDetailHeader from './DeviceDetailHeader'

import {_getDevice} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    hardwareId: null,
    device: null,
    active: null
  }),

  components: {
    DeviceDetailHeader
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
    }
  }
}
</script>

<style scoped>
</style>
