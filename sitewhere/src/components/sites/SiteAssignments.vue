<template>
  <div>
    <v-container>
      <v-layout row wrap v-if="assignments">
        <v-flex xs12>
          <assignment-list-panel :assignment="assignment"
            v-for="(assignment, index) in assignments" :key="assignment.token"
            @click="onOpenAssignment(assignment.token)"
            @assignmentDeleted="onAssignmentDeleted"
            class="mb-3">
          </assignment-list-panel>
        </v-flex>
      </v-layout>
      <pager :results="results" @pagingUpdated="updatePaging"></pager>
    </v-container>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import AssignmentListPanel from '../assignments/AssignmentListPanel'
import {restAuthGet} from '../../http/http-common'

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
      restAuthGet(this.$store,
        'sites/' + siteToken + '/assignments?' + query +
        '&includeDevice=true&includeAsset=true',
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
    },

    // Called when an assignment is deleted.
    onAssignmentDeleted: function (token) {
      console.log('Assignment deleted')
    }
  }
}
</script>

<style scoped>
</style>
