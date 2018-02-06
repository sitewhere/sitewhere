<template>
  <div>
    <v-layout row wrap v-if="mxs">
      <v-flex xs12>
        <no-results-panel v-if="mxs.length === 0"
          text="No Measurement Events Found for Assignment">
        </no-results-panel>
        <v-data-table v-if="mxs.length > 0" class="elevation-2 pa-0"
          :headers="headers" :items="mxs" :hide-actions="true"
          :total-items="0">
          <template slot="items" slot-scope="props">
            <td width="50%" :title="props.item.measurementsSummary">
              {{ props.item.measurementsSummary }}
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
import {_listMeasurementsForAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    mxs: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'Measurements',
        value: 'mxs'
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
      _listMeasurementsForAssignment(this.$store, this.token, query)
        .then(function (response) {
          component.results = response.data
          component.mxs = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when page number is updated.
    onPageUpdated: function (pageNumber) {
      this.$data.pager.page = pageNumber
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
