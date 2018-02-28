<template>
  <div>
    <v-layout row wrap v-if="assignments">
      <v-flex xs12>
        <assignment-list-panel :assignment="assignment"
          v-for="(assignment, index) in assignments" :key="assignment.token"
          @click.native="onOpenAssignment(assignment.token)"
          @refresh="refresh"
          class="ma-2">
        </assignment-list-panel>
      </v-flex>
    </v-layout>
    <pager :results="results" @pagingUpdated="updatePaging">
      <no-results-panel slot="noresults"
        text="No Assignments Found">
      </no-results-panel>
    </pager>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import AssignmentListPanel from '../assignments/AssignmentListPanel'
import {_listAssignmentsForArea} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    assignments: null
  }),

  props: ['area'],

  components: {
    Pager,
    NoResultsPanel,
    AssignmentListPanel
  },

  watch: {
    // Refresh component if area is updated.
    area: function (value) {
      this.refresh()
    }
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
      var areaToken = this.area.token
      var paging = this.$data.paging.query

      let options = {}
      options.includeDevice = true
      options.includeAsset = true

      _listAssignmentsForArea(this.$store, areaToken, options, paging)
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
