<template>
  <div>
    <navigation-page icon="fa-calendar" title="Manage Schedules">
      <div slot="content">
        <v-layout row wrap v-if="schedules">
          <v-flex xs12>
            <no-results-panel v-if="schedules.length === 0"
              text="No Schedules Found">
            </no-results-panel>
            <v-data-table v-if="schedules.length > 0" class="elevation-2 pa-0"
              :headers="headers" :items="schedules"
              :hide-actions="true" no-data-text="No Schedules Found"
              total-items="0">
              <template slot="items" slot-scope="props">
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
                  <actions-block @edited="refresh" @deleted="refresh">
                    <schedule-update-dialog slot="edit" :token="props.item.token">
                    </schedule-update-dialog>
                    <schedule-delete-dialog slot="delete" :token="props.item.token">
                    </schedule-delete-dialog>
                  </actions-block>
                </td>
              </template>
            </v-data-table>
          </v-flex>
        </v-layout>
        <pager :pageSizes="pageSizes" :results="results"
          @pagingUpdated="updatePaging">
        </pager>
      </div>
    </navigation-page>
    <schedule-create-dialog @scheduleAdded="refresh">
    </schedule-create-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import Pager from '../common/Pager'
import ActionsBlock from '../common/ActionsBlock'
import NoResultsPanel from '../common/NoResultsPanel'
import ScheduleCreateDialog from './ScheduleCreateDialog'
import ScheduleUpdateDialog from './ScheduleUpdateDialog'
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
    NavigationPage,
    Pager,
    ActionsBlock,
    NoResultsPanel,
    ScheduleCreateDialog,
    ScheduleUpdateDialog,
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

      // Schedule filter options.
      let options = {}

      _listSchedules(this.$store, options, paging)
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
