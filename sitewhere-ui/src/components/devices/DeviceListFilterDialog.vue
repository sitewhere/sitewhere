<template>
  <span>
    <base-dialog title="Update Device Filters" width="800"
      :visible="dialogVisible" createLabel="Update Filter" cancelLabel="Cancel"
      :error="error" @createClicked="onFilterUpdateClicked"
      @cancelClicked="onCancelClicked">
      <v-tabs v-model="active">
        <v-tabs-bar dark color="primary">
          <v-tabs-slider></v-tabs-slider>
          <v-tabs-item key="site" href="#site">
            Site
          </v-tabs-item>
          <v-tabs-item key="specification" href="#specification">
            Specification
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
          <slot name="tabcontent"></slot>
          <v-tabs-content key="site" id="site">
            <v-card flat>
              <v-card-text>
                <site-chooser :chosenText="siteChosenText"
                  :notChosenText="siteNotChosenText"
                  :selected="siteFilter"
                  @siteUpdated="onSiteUpdated">
                </site-chooser>
              </v-card-text>
            </v-card>
          </v-tabs-content>
          <v-tabs-content key="specification" id="specification">
            <v-card flat>
              <v-card-text>
                <device-type-chooser :chosenText="specificationChosenText"
                  :notChosenText="specificationNotChosenText"
                  :selected="specificationFilter"
                  @specificationUpdated="onSpecificationUpdated">
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
    <v-tooltip top>
      <v-btn fab class="white blue--text text--darken-3" @click.stop="openDialog" slot="activator">
        <v-icon>fa-filter</v-icon>
      </v-btn>
      <span>Update Filter</span>
    </v-tooltip>
  </span>
</template>

<script>
import BaseDialog from '../common/BaseDialog'
import SiteChooser from '../sites/SiteChooser'
import DeviceTypeChooser from '../devicetypes/DeviceTypeChooser'
import DeviceGroupChooser from '../groups/DeviceGroupChooser'
import DateTimePicker from '../common/DateTimePicker'

export default {

  data: () => ({
    active: null,
    menu: null,
    siteFilter: null,
    specificationFilter: null,
    deviceGroupFilter: null,
    createdDateFilter: 'all',
    createdAfter: null,
    siteChosenText: 'Search results will be limited to devices assigned to the site below.',
    siteNotChosenText: 'Choose a site from the list below to limit search results to devices assigned to the given site.',
    specificationChosenText: 'Search results will be limited to devices implementing the specification below.',
    specificationNotChosenText: 'Choose a specification from the list below to limit search results to devices implementing the specification.',
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
    SiteChooser,
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
    // Called when site filter is updated.
    onSiteUpdated: function (site) {
      this.$data.siteFilter = site
    },

    // Called when specification filter is updated.
    onSpecificationUpdated: function (specification) {
      this.$data.specificationFilter = specification
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
      payload.siteFilter = this.$data.siteFilter
      payload.specificationFilter = this.$data.specificationFilter
      payload.deviceGroupFilter = this.$data.deviceGroupFilter
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.active = 'site'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()
      if (payload) {
        this.$data.siteFilter = payload.siteFilter
        this.$data.specificationFilter = payload.specificationFilter
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
