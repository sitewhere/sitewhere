<template>
  <div>
    <v-container fluid grid-list-md  v-if="deviceTypes">
      <v-layout row wrap>
         <v-flex xs6 v-for="(deviceType, index) in deviceTypes"
          :key="deviceType.token">
          <device-type-list-entry :deviceType="deviceType">
          </device-type-list-entry>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" @pagingUpdated="updatePaging"></pager>
    <device-type-create-dialog @deviceTypeAdded="onDeviceTypeAdded"/>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import DeviceTypeListEntry from './DeviceTypeListEntry'
import DeviceTypeCreateDialog from './DeviceTypeCreateDialog'
import {_listDeviceTypes} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    deviceTypes: null
  }),

  components: {
    Pager,
    DeviceTypeListEntry,
    DeviceTypeCreateDialog
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
      _listDeviceTypes(this.$store, false, true, paging)
        .then(function (response) {
          component.results = response.data
          component.deviceTypes = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when a new device type is added.
    onDeviceTypeAdded: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
