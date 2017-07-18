<template>
  <div v-if="assignment">
    <v-app>
      <assignment-detail-header :assignment="assignment" class="mb-3">
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
          <assignment-invocation-events :token="assignment.token"></assignment-invocation-events>
        </v-tabs-content>
      </v-tabs>
    </v-app>
  </div>
</template>

<script>
import AssignmentDetailHeader from './AssignmentDetailHeader'
import AssignmentLocationEvents from './AssignmentLocationEvents'
import AssignmentMeasurementEvents from './AssignmentMeasurementEvents'
import AssignmentAlertEvents from './AssignmentAlertEvents'
import AssignmentInvocationEvents from './AssignmentInvocationEvents'

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
    AssignmentInvocationEvents
  },

  created: function () {
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  methods: {
    // Called to refresh site data.
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

    // Called after site data is loaded.
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
    }
  }
}
</script>

<style scoped>
</style>
