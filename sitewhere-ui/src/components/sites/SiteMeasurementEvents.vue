<template>
  <div>
    <v-layout row wrap v-if="mxs">
      <v-flex xs12>
        <v-data-table class="elevation-2 pa-0" :headers="headers" :items="mxs"
          :hide-actions="true" no-data-text="No Measurements Found for Site">
          <template slot="items" scope="props">
            <td width="30%" :title="props.item.assetName">
              {{ props.item.assetName }}
            </td>
            <td width="50%" :title="props.item.measurementsSummary">
              {{ props.item.measurementsSummary }}
            </td>
            <td width="10%" style="white-space: nowrap" :title="formatDate(props.item.eventDate)">
              {{ formatDate(props.item.eventDate) }}
            </td>
            <td width="10%" style="white-space: nowrap" :title="formatDate(props.item.receivedDate)">
              {{ formatDate(props.item.receivedDate) }}
            </td>
          </template>
        </v-data-table>
      </v-flex>
    </v-layout>
    <pager :pageSizes="pageSizes" :results="results" @pagingUpdated="updatePaging"></pager>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import {listMeasurementsForSite} from '../../http/sitewhere-api'

export default {

  data: () => ({
    results: null,
    paging: null,
    mxs: null,
    headers: [
      {
        left: true,
        sortable: false,
        text: 'Asset',
        value: 'asset'
      }, {
        left: true,
        sortable: false,
        text: 'Measurements',
        value: 'mxs'
      }, {
        left: true,
        sortable: false,
        text: 'Event Date',
        value: 'event'
      }, {
        left: true,
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

  props: ['siteToken'],

  components: {
    Pager
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
      listMeasurementsForSite(this.$store, siteToken, query,
        function (response) {
          component.results = response.data
          component.mxs = response.data.results
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

    // Format date.
    formatDate: function (date) {
      if (!date) {
        return 'N/A'
      }
      return this.$moment(date).format('YYYY-MM-DD H:mm:ss')
    }
  }
}
</script>

<style scoped>
</style>
