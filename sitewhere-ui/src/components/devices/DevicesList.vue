<template>
  <div>
    <device-list-filter-bar @filter="onFilterUpdated">
    </device-list-filter-bar>
    <v-container fluid grid-list-md  v-if="devices">
      <v-layout row wrap>
         <v-flex xs6 v-for="(device, index) in devices" :key="device.hardwareId">
          <device-list-panel :device="device" @assigned="refresh"
            @deviceOpened="onOpenDevice">
          </device-list-panel>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" :pageSizes="pageSizes"
      @pagingUpdated="updatePaging">
    </pager>
    <device-create-dialog @deviceAdded="onDeviceAdded"/>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import DeviceListPanel from './DeviceListPanel'
import DeviceListFilterBar from './DeviceListFilterBar'
import DeviceCreateDialog from './DeviceCreateDialog'
import {_listFilteredDevices} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    devices: null,
    filter: null,
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
    DeviceListFilterBar,
    DeviceCreateDialog
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh list of sites.
    refresh: function () {
      let paging = this.$data.paging.query
      let filter = this.$data.filter
      let component = this
      let criteria = filter || {}
      _listFilteredDevices(this.$store, criteria.site, criteria.specification,
        false, false, true, true, paging)
        .then(function (response) {
          component.results = response.data
          component.devices = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when filter criteria are updated.
    onFilterUpdated: function (filter) {
      this.$data.filter = filter
      this.refresh()
    },

    // Called when a new device is added.
    onDeviceAdded: function () {
      this.refresh()
    },

    onDateUpdated: function (value) {
      console.log('date emitted ' + value)
    },

    // Called to open detail page for device.
    onOpenDevice: function (hardwareId) {
      Utils.routeTo(this, '/devices/' + hardwareId)
    }
  }
}
</script>

<style scoped>
</style>
