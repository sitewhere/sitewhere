<template>
  <div>
    <navigation-page icon="fa-microchip" title="Manage Devices">
      <div slot="actions">
        <v-tooltip left v-if="filter.deviceType">
          <v-btn slot="activator" color="green darken-2 white--text"
            @click="onBatchCommandInvocation">
            <v-icon left>fa-bolt</v-icon>
            Execute Batch Command
          </v-btn>
          <span>Execute Batch Command</span>
        </v-tooltip>
        <v-tooltip left>
          <v-btn icon slot="activator" @click="onShowFilterCriteria">
            <v-icon>fa-filter</v-icon>
          </v-btn>
          <span>Filter Device List</span>
        </v-tooltip>
      </div>
      <div slot="content">
        <device-list-filter-bar ref="filters" @filter="onFilterUpdated">
        </device-list-filter-bar>
        <v-container fluid grid-list-md  v-if="devices">
          <v-layout row wrap>
             <v-flex xs6 v-for="(device, index) in devices" :key="device.token">
              <device-list-panel :device="device" @assignDevice="onAssignDevice"
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
    </navigation-page>
    <assignment-create-dialog ref="assign"
       @assignmentCreated="onAssignmentCreated">
    </assignment-create-dialog>
    <batch-command-create-dialog ref="batch" :filter="filter">
    </batch-command-create-dialog>
  </div>
</template>

<script>
import NavigationPage from '../common/NavigationPage'
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import DeviceListPanel from './DeviceListPanel'
import DeviceListFilterBar from './DeviceListFilterBar'
import DeviceCreateDialog from './DeviceCreateDialog'
import AssignmentCreateDialog from '../assignments/AssignmentCreateDialog'
import BatchCommandCreateDialog from '../batch/BatchCommandCreateDialog'
import {_listDevices} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    devices: null,
    filter: {},
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
    NavigationPage,
    Pager,
    DeviceListPanel,
    DeviceListFilterBar,
    DeviceCreateDialog,
    AssignmentCreateDialog,
    BatchCommandCreateDialog
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

      let options = {}
      options.area = filter.area
      options.deviceType = filter.deviceType
      options.includeDeviceType = true
      options.includeAssignment = true
      options.includeDeleted = false
      options.excludeAssigned = false

      _listDevices(this.$store, options, paging)
        .then(function (response) {
          component.results = response.data
          component.devices = response.data.results
        }).catch(function (e) {
        })
    },
    // Called to show filter criteria dialog.
    onShowFilterCriteria: function () {
      this.$refs['filters'].showFilterCriteriaDialog()
    },
    // Called when filter criteria are updated.
    onFilterUpdated: function (filter) {
      this.$data.filter = filter
      this.refresh()
    },
    // Open device assignment dialog.
    onAssignDevice: function (device) {
      let assignDialog = this.$refs['assign']
      assignDialog.deviceToken = device.token
      assignDialog.onOpenDialog()
    },
    // Called after new assignment is created.
    onAssignmentCreated: function () {
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
    onOpenDevice: function (device) {
      Utils.routeTo(this, '/devices/' + device.token)
    },
    // Called to invoke a batch command.
    onBatchCommandInvocation: function () {
      this.$refs['batch'].onOpenDialog()
    }
  }
}
</script>

<style scoped>
</style>
