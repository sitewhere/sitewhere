<template>
  <div>
    <navigation-page icon="fa-list-alt" title="Manage Batch Operations">
      <div slot="content">
        <v-layout row wrap v-if="operations">
          <v-flex xs12>
            <no-results-panel v-if="operations.length === 0"
              text="No Batch Operations Found">
            </no-results-panel>
            <v-data-table v-if="operations.length > 0" class="elevation-2 pa-0"
              :headers="headers" :items="operations"
              :hide-actions="true" no-data-text="No Batch Operations Found">
              <template slot="items" slot-scope="props">
                <td width="15%" :title="props.item.operationType">
                  {{ props.item.operationType }}
                </td>
                <td width="15%" :title="props.item.processingStatus">
                  {{ props.item.processingStatus }}
                </td>
                <td width="15%" style="white-space: nowrap"
                  :title="utils.formatDate(props.item.createdDate)">
                  {{ utils.formatDate(props.item.createdDate) }}
                </td>
                <td width="20%" style="white-space: nowrap"
                  :title="utils.formatDate(props.item.processingStartedDate)">
                  {{ utils.formatDate(props.item.processingStartedDate) }}
                </td>
                <td width="20%" style="white-space: nowrap"
                  :title="utils.formatDate(props.item.processingEndedDate)">
                  {{ utils.formatDate(props.item.processingEndedDate) }}
                </td>
                <td width="10%" title="View Batch Operation">
                  <v-tooltip left>
                    <v-btn dark icon class="green darken-2" slot="activator"
                      @click.stop="openBatchOperation(props.item.token)">
                      <v-icon small>fa-arrow-right</v-icon>
                    </v-btn>
                    <span>Batch Operation Detail</span>
                  </v-tooltip>
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
  </div>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import {_listBatchOperations} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    operations: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'Operation',
        value: 'operation'
      }, {
        align: 'left',
        sortable: false,
        text: 'Status',
        value: 'status'
      }, {
        align: 'left',
        sortable: false,
        text: 'Created',
        value: 'created'
      }, {
        align: 'left',
        sortable: false,
        text: 'Processing Started',
        value: 'started'
      }, {
        align: 'left',
        sortable: false,
        text: 'Processing Finished',
        value: 'finished'
      }, {
        align: 'left',
        sortable: false,
        text: '',
        value: 'open'
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
      _listBatchOperations(this.$store, paging)
        .then(function (response) {
          component.results = response.data
          component.operations = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when page number is updated.
    onPageUpdated: function (pageNumber) {
      this.$data.pager.page = pageNumber
      this.refresh()
    },

    // Open detail page for batch operation.
    openBatchOperation: function (token) {
      Utils.routeTo(this, '/batch/' + token)
    }
  }
}
</script>

<style scoped>
</style>
