<template>
  <div>
    <v-layout row wrap v-if="schedules">
      <v-flex xs12>
        <no-results-panel v-if="schedules.length === 0"
          text="No Schedules Found">
        </no-results-panel>
        <v-data-table v-if="schedules.length > 0" class="elevation-2 pa-0"
          :headers="headers" :items="schedules"
          :hide-actions="true" no-data-text="No Schedules Found">
          <template slot="items" scope="props">
            <td width="25%" :title="props.item.name">
              {{ props.item.name }}
            </td>
            <td width="15%" :title="props.item.triggerType">
              {{ props.item.triggerType }}
            </td>
            <td width="30%" :title="props.item.token">
              {{ props.item.token }}
            </td>
            <td width="25%"
              :title="utils.formatDate(props.item.createdDate)">
              {{ utils.formatDate(props.item.createdDate) }}
            </td>
            <td width="10%">
              <v-btn dark icon small class="green darken-2"
                v-tooltip:top="{ html: 'Edit Schedule' }"
                @click.native.stop="editSchedule(props.item.token)">
                <v-icon fa>edit</v-icon>
              </v-btn>
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
import {_listSchedules} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    schedules: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'Name',
        value: 'name'
      }, {
        align: 'left',
        sortable: false,
        text: 'Type',
        value: 'type'
      }, {
        align: 'left',
        sortable: false,
        text: 'Token',
        value: 'token'
      }, {
        align: 'left',
        sortable: false,
        text: 'Created Date',
        value: 'created'
      }, {
        align: 'left',
        sortable: false,
        text: '',
        value: 'actions'
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

    // Refresh list.
    refresh: function () {
      var component = this
      var paging = this.$data.paging.query
      _listSchedules(this.$store, paging)
        .then(function (response) {
          component.results = response.data
          component.schedules = response.data.results
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
