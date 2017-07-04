<template>
  <div>
    <v-layout row wrap v-if="assignments">
      <v-flex xs12>
        <assignment-list-panel :assignment="assignment"
          v-for="(assignment, index) in assignments" :key="assignment.token"
          @click="onOpenAssignment(assignment.token)"
          @refresh="refresh"
          class="ma-3">
        </assignment-list-panel>
      </v-flex>
    </v-layout>
    <pager :results="results" @pagingUpdated="updatePaging"></pager>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import AssignmentListPanel from '../assignments/AssignmentListPanel'
import {listAssignmentsForSite} from '../../http/sitewhere-api'

export default {

  data: () => ({
    results: null,
    paging: null,
    assignments: null
  }),

  props: ['siteToken'],

  components: {
    Pager,
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
      var siteToken = this.siteToken
      var query = this.$data.paging.query
      listAssignmentsForSite(this.$store, siteToken, query,
        function (response) {
          component.results = response.data
          component.assignments = response.data.results
          component.$store.commit('error', null)
        }, function (e) {
          component.$store.commit('error', e)
        }
      )
    },

    // Called when page number is updated.
    onPageUpdated: function (pageNumber) {
      this.$data.pager.page = pageNumber
      this.refresh()
    },

    // Called to open detail page for assignment.
    onOpenAssignment: function (token) {
      console.log('Open assignment')
    }
  }
}
</script>

<style scoped>
</style>
