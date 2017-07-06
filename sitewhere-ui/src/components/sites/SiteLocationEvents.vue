<template>
  <div>
    <v-layout row wrap v-if="locations">
      <v-flex xs12>
        <v-data-table class="elevation-2 pa-0" :headers="headers" :items="locations"
          :hide-actions="true" no-data-text="No Locations Found for Site">
          <template slot="items" scope="props">
            <td width="40%" :title="props.item.assetName">
              {{ props.item.assetName }}
            </td>
            <td width="40%" title="Lat/Lon/Elevation">
              {{ fourDecimalPlaces(props.item.latitude) + ', ' + fourDecimalPlaces(props.item.longitude) + ', ' + fourDecimalPlaces(props.item.elevation) }}
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
import {_listLocationsForSite} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    locations: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'Asset',
        value: 'asset'
      }, {
        align: 'left',
        sortable: false,
        text: 'Latitude/Longitude/Elevation',
        value: 'lle'
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
      var paging = this.$data.paging.query
      _listLocationsForSite(this.$store, siteToken, paging)
        .then(function (response) {
          component.results = response.data
          component.locations = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when page number is updated.
    onPageUpdated: function (pageNumber) {
      this.$data.pager.page = pageNumber
      this.refresh()
    },

    // Rounds to four decimal places
    fourDecimalPlaces: function (val) {
      return Number(Math.round(val + 'e4') + 'e-4').toFixed(4)
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
