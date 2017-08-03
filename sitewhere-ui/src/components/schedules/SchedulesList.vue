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
            <td width="17%" :title="props.item.name">
              {{ props.item.name }}
            </td>
            <td width="15%" :title="props.item.triggerType">
              {{ props.item.triggerType }}
            </td>
            <td width="35%" :title="props.item.token">
              {{ props.item.token }}
            </td>
            <td width="18%"
              :title="utils.formatDate(props.item.createdDate)">
              {{ utils.formatDate(props.item.createdDate) }}
            </td>
            <td width="15%">
              <v-btn dark icon small class="grey pa-0 ma-0"
                v-tooltip:top="{ html: 'Edit' }"
                @click.native.stop="onEditSchedule(props.item.token)">
                <v-icon fa>edit</v-icon>
              </v-btn>
              <schedule-delete-dialog :token="props.item.token"
                @scheduleDeleted="refresh">
              </schedule-delete-dialog>
            </td>
          </template>
        </v-data-table>
      </v-flex>
    </v-layout>
    <pager :pageSizes="pageSizes" :results="results"
      @pagingUpdated="updatePaging">
    </pager>
    <schedule-create-dialog @scheduleAdded="refresh">
    </schedule-create-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import ScheduleCreateDialog from './ScheduleCreateDialog'
import ScheduleDeleteDialog from './ScheduleDeleteDialog'
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
        text: 'Actions',
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
    NoResultsPanel,
    ScheduleCreateDialog,
    ScheduleDeleteDialog
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
