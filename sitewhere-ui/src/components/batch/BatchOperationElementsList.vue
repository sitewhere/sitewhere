<template>
  <div>
    <v-layout row wrap v-if="elements">
      <v-flex xs12>
        <no-results-panel v-if="elements.length === 0"
          text="No Batch Operation Elements Found">
        </no-results-panel>
        <v-data-table v-if="elements.length > 0" class="elevation-2 pa-0"
          :headers="headers" :items="elements" :hide-actions="true"
          no-data-text="No Batch Operation Elements Found">
          <template slot="items" slot-scope="props">
            <td width="40%" :title="props.item.hardwareId">
              <a href="javascript: void(0)"
                @click="onOpenDevice(props.item.hardwareId)">
                {{ props.item.hardwareId }}
              </a>
            </td>
            <td width="20%" :title="props.item.processingStatus">
              {{ props.item.processingStatus }}
            </td>
            <td width="20%" style="white-space: nowrap"
              :title="utils.formatDate(props.item.processedDate)">
              {{ utils.formatDate(props.item.processedDate) }}
            </td>
            <td width="20%" style="white-space: nowrap"
              :title="props.item.metadata.invocation">
              {{ props.item.metadata.invocation }}
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
import {_listBatchOperationElements} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    elements: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'Hardware Id',
        value: 'hwid'
      }, {
        align: 'left',
        sortable: false,
        text: 'Status',
        value: 'status'
      }, {
        align: 'left',
        sortable: false,
        text: 'Processed Date',
        value: 'processed'
      }, {
        align: 'left',
        sortable: false,
        text: 'Invocation Event',
        value: 'invocation'
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

  props: ['token'],

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
      _listBatchOperationElements(this.$store, this.token, paging)
        .then(function (response) {
          component.results = response.data
          component.elements = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when page number is updated.
    onPageUpdated: function (pageNumber) {
      this.$data.pager.page = pageNumber
      this.refresh()
    },

    // Open device for the given hardware id.
    onOpenDevice: function (hardwareId) {
      Utils.routeToDevice(this, hardwareId)
    }
  }
}
</script>

<style scoped>
</style>
