<template>
  <div>
    <navigation-page v-if="assignment" icon="fa-link"
      title="Manage Device Assignment">
      <div slot="content">
        <assignment-detail-header :assignment="assignment">
        </assignment-detail-header>
        <v-tabs v-model="active">
          <v-tabs-bar dark color="primary">
            <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
            <v-tabs-item key="assignments" href="#assignments">
              Locations
            </v-tabs-item>
            <v-tabs-item key="measurements" href="#measurements">
              Measurements
            </v-tabs-item>
            <v-tabs-item key="alerts" href="#alerts">
              Alerts
            </v-tabs-item>
            <v-tabs-item key="invocations" href="#invocations">
              Command Invocations
            </v-tabs-item>
            <v-tabs-item key="responses" href="#responses">
              Command Responses
            </v-tabs-item>
          </v-tabs-bar>
          <v-tabs-items>
            <v-tabs-content key="assignments" id="assignments">
              <assignment-location-events :token="assignment.token"></assignment-location-events>
            </v-tabs-content>
            <v-tabs-content key="measurements" id="measurements">
              <assignment-measurement-events :token="assignment.token"></assignment-measurement-events>
            </v-tabs-content>
            <v-tabs-content key="alerts" id="alerts">
              <assignment-alert-events :token="assignment.token"></assignment-alert-events>
            </v-tabs-content>
            <v-tabs-content key="invocations" id="invocations">
              <assignment-invocation-events ref="invocations" :token="assignment.token"></assignment-invocation-events>
            </v-tabs-content>
            <v-tabs-content key="responses" id="responses">
              <assignment-response-events :token="assignment.token"></assignment-response-events>
            </v-tabs-content>
          </v-tabs-items>
        </v-tabs>
        <invocation-create-dialog v-if="active === 'invocations'"
          :token="assignment.token" @invocationAdded="onInvocationAdded"
          :deviceType="assignment.device.deviceType"/>
      </div>
      <div slot="actions">
        <navigation-action-button icon="fa-crosshairs" tooltip="Device Emulator"
          @action="onOpenEmulator">
        </navigation-action-button>
        <navigation-action-button icon="fa-times" tooltip="Delete Assignment"
          @action="onDelete">
        </navigation-action-button>
      </div>
    </navigation-page>
    <assignment-delete-dialog ref="delete" :token="assignment.token"
      @assignmentDeleted="onAssignmentDeleted">
    </assignment-delete-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import NavigationActionButton from '../common/NavigationActionButton'
import AssignmentDetailHeader from './AssignmentDetailHeader'
import AssignmentLocationEvents from './AssignmentLocationEvents'
import AssignmentMeasurementEvents from './AssignmentMeasurementEvents'
import AssignmentAlertEvents from './AssignmentAlertEvents'
import AssignmentInvocationEvents from './AssignmentInvocationEvents'
import AssignmentResponseEvents from './AssignmentResponseEvents'
import AssignmentDeleteDialog from './AssignmentDeleteDialog'
import InvocationCreateDialog from './InvocationCreateDialog'

import {_getDeviceAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    assignment: null,
    active: null
  }),

  components: {
    NavigationPage,
    NavigationActionButton,
    AssignmentDetailHeader,
    AssignmentLocationEvents,
    AssignmentMeasurementEvents,
    AssignmentAlertEvents,
    AssignmentInvocationEvents,
    AssignmentResponseEvents,
    AssignmentDeleteDialog,
    InvocationCreateDialog
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

      // Load site information.
      _getDeviceAssignment(this.$store, token)
        .then(function (response) {
          component.onAssignmentLoaded(response.data)
        }).catch(function (e) {
        })
    },
    // Called after data is loaded.
    onAssignmentLoaded: function (assignment) {
      this.$data.assignment = assignment
      var section = {
        id: 'assignments',
        title: 'Assignments',
        icon: 'link',
        route: '/admin/assignments/' + assignment.token,
        longTitle: 'Manage Assignment: ' + assignment.token
      }
      this.$store.commit('currentSection', section)
    },
    // Open dialog to delete assignment.
    onDelete: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when site is deleted.
    onAssignmentDeleted: function () {
      Utils.routeTo(this, '/areas')
    },
    // Called when emulator is opened.
    onOpenEmulator: function () {
      Utils.routeTo(this, '/assignments/' + this.$data.token + '/emulator')
    },
    // Called if command invocation is added.
    onInvocationAdded: function () {
      this.$refs['invocations'].refresh()
    }
  }
}
</script>

<style scoped>
</style>
