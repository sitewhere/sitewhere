<template>
  <div>
    <v-layout row wrap v-if="assignments">
      <v-flex xs12>
        <no-results-panel v-if="assignments.length === 0"
          text="No Assignments Found for Device">
        </no-results-panel>
        <assignment-list-panel :assignment="assignment"
          v-for="(assignment, index) in assignments" :key="assignment.token"
          @click.native="onOpenAssignment(assignment.token)"
          @refresh="refresh"
          class="ma-2">
        </assignment-list-panel>
      </v-flex>
    </v-layout>
    <pager :results="results" @pagingUpdated="updatePaging"></pager>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import AssignmentListPanel from '../assignments/AssignmentListPanel'
import {_listDeviceAssignmentHistory} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    assignments: null
  }),

  props: ['device'],

  components: {
    Pager,
    NoResultsPanel,
    AssignmentListPanel
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh list of assignments.
    refresh: function () {
      var component = this
      var token = this.device.token
      var paging = this.$data.paging.query

      // Set search options.
      let options = {}
      options.includeAsset = true
      options.includeDevice = true
      options.includeSite = true

      _listDeviceAssignmentHistory(this.$store, token, options, paging)
        .then(function (response) {
          component.results = response.data
          component.assignments = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when page number is updated.
    onPageUpdated: function (pageNumber) {
      this.$data.pager.page = pageNumber
      this.refresh()
    },

    // Called to open detail page for assignment.
    onOpenAssignment: function (token) {
      Utils.routeTo(this, '/assignments/' + token)
    }
  }
}
</script>

<style scoped>
</style>
