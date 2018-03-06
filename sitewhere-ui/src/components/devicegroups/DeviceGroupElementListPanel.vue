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
            <td v-if="props.item.device" width="40%"
              :title="props.item.device.token"
              :class="elementClassFor(props.item)">
              <v-icon class="grey--text text--darken-2 type-icon">
                fa-microchip
              </v-icon>
              {{ props.item.device.token }}
            </td>
            <td v-else width="40%" :title="props.item.groupId"
              :class="elementClassFor(props.item)">
              <v-icon class="grey--text text--darken-2 type-icon">
                view_module
              </v-icon>
              {{ props.item.groupId }}
            </td>
            <td v-if="props.item.device" width="40%">
              {{ props.item.device.deviceType.name }}
            </td>
            <td v-else width="40%">
              {{ props.item.deviceGroup.name }}
            </td>
            <td width="10%" :title="props.item.roles">
              {{ props.item.roles.join(', ') }}
            </td>
            <td width="10%" title="">
              <v-tooltip left>
                <v-btn class="ma-0" icon slot="activator"
                  @click.stop="showDeleteDialog(props.item)">
                  <v-icon class="grey--text">delete</v-icon>
                </v-btn>
                <span>Delete</span>
              </v-tooltip>
            </td>
          </template>
        </v-data-table>
      </v-flex>
    </v-layout>
    <pager :pageSizes="pageSizes" :results="results" @pagingUpdated="updatePaging"></pager>
    <device-group-element-delete-dialog ref="delete"
      :groupToken="token" @elementDeleted="refresh">
    </device-group-element-delete-dialog>
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
    elementClassFor: function (element) {
      return (element.device) ? '' : 'group-element'
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
    },
    // Show dialog for deleting element.
    showDeleteDialog: function (element) {
      console.log(element)
      this.$refs['delete'].elementId = element.id
      this.$refs['delete'].showDeleteDialog()
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
