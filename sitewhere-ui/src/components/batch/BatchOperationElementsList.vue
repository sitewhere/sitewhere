<template>
  <div>
    <no-results-panel v-if="elements && elements.length === 0"
      text="No Batch Operation Elements Found">
    </no-results-panel>
    <v-data-table v-if="elements && elements.length > 0" class="elevation-2 pa-0"
      :headers="headers" :items="elements" :hide-actions="true"
      no-data-text="No Batch Operation Elements Found">
      <template slot="items" slot-scope="props">
        <td width="40%" :title="props.item.device.token">
          <a href="javascript: void(0)"
            @click="onOpenDevice(props.item.device.token)">
            {{ props.item.device.token }}
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
    <pager :results="results" @pagingUpdated="updatePaging"></pager>
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

      let options = {}
      options.includeDevice = true

      _listBatchOperationElements(this.$store, this.token, options, paging)
        .then(function (response) {
          component.results = response.data
          component.elements = response.data.results
        }).catch(function (e) {
        })
    },
    // Open device for the given token.
    onOpenDevice: function (deviceToken) {
      Utils.routeToDevice(this, deviceToken)
    }
  }
}
</script>

<style scoped>
</style>
