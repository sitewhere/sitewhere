<template>
  <base-dialog title="Update Device Filters" width="800"
    :visible="dialogVisible" createLabel="Update Filter" cancelLabel="Cancel"
    :error="error" @createClicked="onFilterUpdateClicked"
    @cancelClicked="onCancelClicked">
    <v-tabs v-model="active">
      <v-tabs-bar dark color="primary">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="area" href="#area">
          Area
        </v-tabs-item>
        <v-tabs-item key="deviceType" href="#deviceType">
          Device Type
        </v-tabs-item>
        <v-tabs-item key="group" href="#group">
          Device Group
        </v-tabs-item>
        <v-tabs-item key="created" href="#created">
          Created Date
        </v-tabs-item>
        <slot name="tabitem"></slot>
      </v-tabs-bar>
      <v-tabs-items>
        <v-tabs-content key="area" id="area">
          <v-card flat>
            <v-card-text>
              <area-chooser :chosenText="areaChosenText"
                :notChosenText="areaNotChosenText"
                :selected="areaFilter"
                @areaUpdated="onAreaUpdated">
              </area-chooser>
            </v-card-text>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="deviceType" id="deviceType">
          <v-card flat>
            <v-card-text>
              <device-type-chooser :chosenText="deviceTypeChosenText"
                :notChosenText="deviceTypeNotChosenText"
                :selected="deviceTypeFilter"
                @deviceTypeUpdated="onDeviceTypeUpdated">
              </device-type-chooser>
            </v-card-text>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="group" id="group">
          <v-card flat>
            <v-card-text>
              <device-group-chooser :chosenText="groupChosenText"
                :notChosenText="groupNotChosenText"
                :selected="deviceGroupFilter"
                @groupUpdated="onGroupUpdated">
              </device-group-chooser>
            </v-card-text>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="created" id="created">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12>
                    <v-select :items="createdDateRanges" v-model="createdDateFilter"
                      label="Created Date" prepend-icon="insert_invitation"></v-select>
                  </v-flex>
                  <v-flex xs12 mb-2 pb-2>
                    <v-divider></v-divider>
                  </v-flex>
                  <v-flex xs12>
                    <v-card flat>
                      <v-card-text v-if="createdDateFilter === 'all'">
                        Include all devices in search without consideration
                        of created date.
                      </v-card-text>
                      <v-card-text v-if="createdDateFilter === 'hour'">
                        Include only devices created within the last hour.
                      </v-card-text>
                      <v-card-text v-if="createdDateFilter === 'day'">
                        Include only devices created within the last day.
                      </v-card-text>
                      <v-card-text v-if="createdDateFilter === 'week'">
                        Include only devices created within the last week.
                      </v-card-text>
                      <v-card-text v-if="createdDateFilter === 'after'">
                        <date-time-picker
                          label="Devices created after this date/time"
                          :v-model="createdAfter"
                          @input="onCreatedAfterUpdated">
                        </date-time-picker>
                      </v-card-text>
                    </v-card>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
          </v-card>
        </v-tabs-content>
      </v-tabs-items>
    </v-tabs>
  </base-dialog>
</template>

<script>
import BaseDialog from '../common/BaseDialog'
import AreaChooser from '../areas/AreaChooser'
import DeviceTypeChooser from '../devicetypes/DeviceTypeChooser'
import DeviceGroupChooser from '../devicegroups/DeviceGroupChooser'
import DateTimePicker from '../common/DateTimePicker'

export default {

  data: () => ({
    active: null,
    menu: null,
    areaFilter: null,
    deviceTypeFilter: null,
    deviceGroupFilter: null,
    createdDateFilter: 'all',
    createdAfter: null,
    areaChosenText: 'Search results will be limited to devices assigned to the area below.',
    areaNotChosenText: 'Choose an area from the list below to limit search results to devices assigned to the given area.',
    deviceTypeChosenText: 'Search results will be limited to devices implementing the device type below.',
    deviceTypeNotChosenText: 'Choose a device type from the list below to limit search results to devices implementing the device type.',
    groupChosenText: 'Search results will be limited to devices in the device group below.',
    groupNotChosenText: 'Choose a device group from the list below to limit search results to devices in that group.',
    createdDateRanges: [
      {
        'text': 'Devices created at any time',
        'value': 'all'
      }, {
        'text': 'Devices created in the last hour',
        'value': 'hour'
      }, {
        'text': 'Devices created in the last day',
        'value': 'day'
      }, {
        'text': 'Devices created in the last week',
        'value': 'week'
      }, {
        'text': 'Devices created after a given date',
        'value': 'after'
      }
    ],
    dialogVisible: false,
    error: null
  }),

  components: {
    BaseDialog,
    AreaChooser,
    DeviceTypeChooser,
    DeviceGroupChooser,
    DateTimePicker
  },

  props: ['filter'],

  watch: {
    filter: function (value) {
      this.load(value)
    }
  },

  methods: {
    // Called when area filter is updated.
    onAreaUpdated: function (area) {
      this.$data.areaFilter = area
    },

    // Called when deviceType filter is updated.
    onDeviceTypeUpdated: function (deviceType) {
      this.$data.deviceTypeFilter = deviceType
    },

    // Called when device group filter is updated.
    onGroupUpdated: function (group) {
      this.$data.deviceGroupFilter = group
    },

    // Called when 'created after' date is updated.
    onCreatedAfterUpdated: function (date) {
      console.log(date)
    },

    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.areaFilter = this.$data.areaFilter
      payload.deviceTypeFilter = this.$data.deviceTypeFilter
      payload.deviceGroupFilter = this.$data.deviceGroupFilter
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.active = 'area'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()
      if (payload) {
        this.$data.areaFilter = payload.areaFilter
        this.$data.deviceTypeFilter = payload.deviceTypeFilter
        this.$data.deviceGroupFilter = payload.deviceGroupFilter
      }
    },

    // Called to open the dialog.
    openDialog: function () {
      this.reset()
      this.$data.dialogVisible = true
    },

    // Called to open the dialog.
    closeDialog: function () {
      this.$data.dialogVisible = false
    },

    // Called to show an error message.
    showError: function (error) {
      this.$data.error = error
    },

    // Called after filter update button is clicked.
    onFilterUpdateClicked: function (e) {
      var payload = this.generatePayload()
      this.$emit('filter', payload)
      this.closeDialog()
    },

    // Called after cancel button is clicked.
    onCancelClicked: function (e) {
      this.$data.dialogVisible = false
    }
  }
}
</script>

<style scoped>
</style>
