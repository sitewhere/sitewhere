<template>
  <div>
    <device-list-filter-bar>
    </device-list-filter-bar>
    <v-container fluid grid-list-md  v-if="devices">
      <v-layout row wrap>
         <v-flex xs6 v-for="(device, index) in devices" :key="device.hardwareId">
          <device-list-panel :device="device" class="mb-1">
          </device-list-panel>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" :pageSizes="pageSizes"
      @pagingUpdated="updatePaging">
    </pager>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import DeviceListPanel from './DeviceListPanel'
import DeviceListFilterBar from './DeviceListFilterBar'
import {_listDevices} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    devices: null,
    pageSizes: [
      {
        text: '20',
        value: 20
      }, {
        text: '50',
        value: 50
      }, {
        text: '100',
        value: 100
      }
    ],
    dateField: new Date()
  }),

  components: {
    Pager,
    DeviceListPanel,
    DeviceListFilterBar
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh list of sites.
    refresh: function () {
      var paging = this.$data.paging.query
      var component = this
      _listDevices(this.$store, true, true, paging)
        .then(function (response) {
          component.results = response.data
          component.devices = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when a new device is added.
    onDeviceAdded: function () {
      this.refresh()
    },

    onDateUpdated: function (value) {
      console.log('date emitted ' + value)
    }
  }
}
</script>

<style scoped>
</style>
