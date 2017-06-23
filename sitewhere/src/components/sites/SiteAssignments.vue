<template>
  <div>
    <v-container v-if="assignments">
      <v-layout row wrap>
        <v-flex xs12>
          <assignment-list-panel :assignment="assignment"
            v-for="(assignment, index) in assignments" :key="assignment.token"
            @click="onOpenAssignment(assignment.token)"
            @assignmentDeleted="onAssignmentDeleted"
            class="mb-3">
          </assignment-list-panel>
        </v-flex>
        <v-pagination @input="onPageUpdated" v-bind:length.number="pageCount"
          v-model="pager.page" total-visible="5">
        </v-pagination>
      </v-layout>
    </v-container>
    <div v-else-if="error">
      <v-container>
        <v-layout row wrap>
          <v-flex xs12>
            <v-card raised class="grey lighten-4 white--text mt-3">
              <v-card-row>
                <v-alert error v-bind:value="true" style="width: 100%">
                  {{error}}
                </v-alert>
              </v-card-row>
            </v-card>
          </v-flex>
        </v-layout>
      </v-container>
    </div>
  </div>
</template>

<script>
import AssignmentListPanel from '../assignments/AssignmentListPanel'
import {restAuthGet} from '../../http/http-common'

export default {

  data: () => ({
    assignments: null,
    pager: {
      page: 1,
      pageSize: 20,
      total: 0
    },
    error: null
  }),

  props: ['siteToken'],

  computed: {
    pageCount: function () {
      var total = this.$data.pager.total
      var size = this.$data.pager.pageSize
      var mod = (total % size)
      var count = (total / size)
      count += (mod > 0) ? 1 : 0
      return count
    }
  },

  components: {
    AssignmentListPanel
  },

  methods: {
    // Refresh list of assignments.
    refresh: function () {
      var component = this
      var siteToken = this.siteToken
      var pager = this.$data.pager
      restAuthGet(this.$store,
        'sites/' + siteToken + '/assignments' +
        '?includeDevice=true&includeAsset=true&' +
        'page=' + pager.page + '&pageSize=' + pager.pageSize,
        function (response) {
          console.log(response)
          component.$data.assignments = response.data.results
          component.$data.pager.total = response.data.numResults
        }, function (e) {
          console.log(e)
          component.$data.error = e
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
  },

  created: function () {
    this.refresh()
  }
}
</script>

<style scoped>
</style>
