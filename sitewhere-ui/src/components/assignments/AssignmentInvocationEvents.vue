<template>
  <div>
    <v-layout row wrap v-if="invocations">
      <v-flex xs12>
        <no-results-panel v-if="invocations.length === 0"
          text="No Command Invocation Events Found for Assignment">
        </no-results-panel>
        <v-data-table v-if="invocations.length > 0" class="elevation-2 pa-0"
          :headers="headers" :items="invocations" :hide-actions="true"
          no-data-text="No Command Invocations Found for Assignment"
          :total-items="0">
          <template slot="items" slot-scope="props">
            <td width="20%" :title="props.item.command.name">
              {{ props.item.command.name }}
            </td>
            <td width="30%" :title="invocationSource(props.item)">
              {{ invocationSource(props.item) }}
            </td>
            <td width="30%" :title="invocationTarget(props.item)">
              {{ invocationTarget(props.item) }}
            </td>
            <td width="10%" style="white-space: nowrap" :title="utils.formatDate(props.item.eventDate)">
              {{ utils.formatDate(props.item.eventDate) }}
            </td>
            <td width="10%" style="white-space: nowrap" :title="utils.formatDate(props.item.receivedDate)">
              {{ utils.formatDate(props.item.receivedDate) }}
            </td>
          </template>
        </v-data-table>
      </v-flex>
    </v-layout>
    <pager :pageSizes="pageSizes" :results="results" @pagingUpdated="updatePaging"></pager>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import {_listCommandInvocationsForAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    invocations: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'Command',
        value: 'command'
      }, {
        align: 'left',
        sortable: false,
        text: 'Source',
        value: 'source'
      }, {
        align: 'left',
        sortable: false,
        text: 'Target',
        value: 'target'
      }, {
        align: 'left',
        sortable: false,
        text: 'Event Date',
        value: 'event'
      }, {
        align: 'left',
        sortable: false,
        text: 'Received Date',
        value: 'received'
      }
    ],
    pageSizes: [
      {
        text: '25',
        value: 25
      }, {
        text: '50',
        value: 50
      }, {
        text: '100',
        value: 100
      }
    ]
  }),

  props: ['token'],

  components: {
    Pager,
    NoResultsPanel
  },

  computed: {
    // Accessor for utility functions.
    utils: function () {
      return Utils
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
      var query = this.$data.paging.query
      _listCommandInvocationsForAssignment(this.$store, this.token, query)
        .then(function (response) {
          component.results = response.data
          component.invocations = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when page number is updated.
    onPageUpdated: function (pageNumber) {
      this.$data.pager.page = pageNumber
      this.refresh()
    },

    // Gets source information for an invocation.
    invocationSource: function (invocation) {
      return invocation.initiator + ' (' + invocation.initiatorId + ')'
    },

    // Gets source information for an invocation.
    invocationTarget: function (invocation) {
      return invocation.target
    }
  }
}
</script>

<style scoped>
</style>
