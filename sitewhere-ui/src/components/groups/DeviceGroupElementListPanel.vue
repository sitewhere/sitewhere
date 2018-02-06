<template>
  <div>
    <v-layout row wrap v-if="elements">
      <v-flex xs12>
        <no-results-panel v-if="elements.length === 0"
          text="No Elements Found for Group">
        </no-results-panel>
        <v-data-table v-if="elements.length > 0" class="elevation-2 pa-0" :headers="headers" :items="elements"
          :hide-actions="true" no-data-text="No Elements Found for Group">
          <template slot="items" slot-scope="props">
            <td width="40%" :title="props.item.elementId"
              :class="elementClassFor(props.item)">
              <v-icon class="grey--text text--darken-2 type-icon">
                {{ iconFor(props.item) }}
              </v-icon>
              {{ props.item.elementId }}
            </td>
            <td width="40%" v-if="props.item.device && props.item.device.assignment">
              {{ props.item.device.specification.assetName + '(' + props.item.device.assignment.assetName + ')' }}
            </td>
            <td width="40%" v-if="props.item.device && !props.item.device.assignment">
              {{ props.item.device.specification.assetName }}
            </td>
            <td width="40%" v-if="props.item.deviceGroup">
              {{ props.item.deviceGroup.name }}
            </td>
            <td width="10%" :title="props.item.roles">
              {{ props.item.roles.join(', ') }}
            </td>
            <td width="10%" title="">
              <device-group-element-delete-dialog :token="token"
                :element="props.item" @elementDeleted="refresh">
              </device-group-element-delete-dialog>
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
import DeviceGroupElementDeleteDialog from './DeviceGroupElementDeleteDialog'
import {_listDeviceGroupElements} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    elements: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'Element',
        value: 'element'
      }, {
        align: 'left',
        sortable: false,
        text: 'Description',
        value: 'description'
      }, {
        align: 'left',
        sortable: false,
        text: 'Roles',
        value: 'roles'
      }, {
        align: 'left',
        sortable: false,
        text: 'Delete',
        value: 'delete'
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
    NoResultsPanel,
    DeviceGroupElementDeleteDialog
  },

  computed: {
    // Accessor for utility functions.
    utils: function () {
      return Utils
    }
  },

  methods: {
    // Update paging values and run query.
    iconFor: function (element) {
      return (element.type === 'Device') ? 'developer_board' : 'view_module'
    },

    elementClassFor: function (element) {
      return (element.type === 'Device') ? '' : 'group-element'
    },

    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh list of assignments.
    refresh: function () {
      var component = this
      var paging = this.$data.paging.query
      _listDeviceGroupElements(this.$store, this.token, true, paging)
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
    }
  }
}
</script>

<style scoped>
.type-icon {
  font-size: 16px;
}
.group-element {
  font-weight: 700;
}
</style>
