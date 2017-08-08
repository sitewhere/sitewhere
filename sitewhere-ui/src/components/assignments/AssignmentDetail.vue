<template>
  <div v-if="assignment">
    <v-app>
      <assignment-detail-header @emulatorOpened="onEmulatorOpened"
        @assignmentDeleted="onAssignmentDeleted"
        :assignment="assignment" class="mb-3">
      </assignment-detail-header>
      <v-tabs class="elevation-2" dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
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
      </v-tabs>
      <invocation-create-dialog v-if="active === 'invocations'"
        :token="assignment.token" @invocationAdded="onInvocationAdded"
        :specificationToken="assignment.device.specificationToken"/>
    </v-app>
  </div>
</template>

<script>
import AssignmentDetailHeader from './AssignmentDetailHeader'
import AssignmentLocationEvents from './AssignmentLocationEvents'
import AssignmentMeasurementEvents from './AssignmentMeasurementEvents'
import AssignmentAlertEvents from './AssignmentAlertEvents'
import AssignmentInvocationEvents from './AssignmentInvocationEvents'
import AssignmentResponseEvents from './AssignmentResponseEvents'
import InvocationCreateDialog from './InvocationCreateDialog'

import {_getDeviceAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    assignment: null,
    active: null
  }),

  components: {
    AssignmentDetailHeader,
    AssignmentLocationEvents,
    AssignmentMeasurementEvents,
    AssignmentAlertEvents,
    AssignmentInvocationEvents,
    AssignmentResponseEvents,
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

    // Called after assignment has been deleted.
    onAssignmentDeleted: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.id + '/sites/' +
          this.$data.assignment.siteToken)
      }
    },

    // Called when emulator is opened.
    onEmulatorOpened: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.id + '/assignments/' +
          this.$data.token + '/emulator')
      }
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
