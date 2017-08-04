<template>
  <div>
    <v-layout row wrap v-if="users">
      <v-flex xs12>
        <no-results-panel v-if="users.length === 0"
          text="No Users Found">
        </no-results-panel>
        <v-data-table v-if="users.length > 0" class="elevation-2 pa-0"
          :headers="headers" :items="users"
          :hide-actions="true" no-data-text="No Users Found">
          <template slot="items" scope="props">
            <td width="10%" :title="props.item.username">
              {{ props.item.username }}
            </td>
            <td width="12%" :title="props.item.firstName">
              {{ props.item.firstName }}
            </td>
            <td width="15%" :title="props.item.lastName">
              {{ props.item.lastName }}
            </td>
            <td width="15%" :title="props.item.status">
              {{ props.item.status }}
            </td>
            <td width="10%"
              :title="utils.formatDate(props.item.lastLogin)">
              {{ utils.formatDate(props.item.lastLogin) }}
            </td>
            <td width="20%"
              :title="utils.formatDate(props.item.createdDate)">
              {{ utils.formatDate(props.item.createdDate) }}
            </td>
            <td width="20%"
              :title="utils.formatDate(props.item.updatedDate)">
              {{ utils.formatDate(props.item.updatedDate) }}
            </td>
            <td width="5%">
            </td>
          </template>
        </v-data-table>
      </v-flex>
    </v-layout>
    <pager :pageSizes="pageSizes" :results="results"
      @pagingUpdated="updatePaging">
    </pager>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import {_listUsers} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    users: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'User Name',
        value: 'username'
      }, {
        align: 'left',
        sortable: false,
        text: 'First Name',
        value: 'firstname'
      }, {
        align: 'left',
        sortable: false,
        text: 'Last Name',
        value: 'lastname'
      }, {
        align: 'left',
        sortable: false,
        text: 'Status',
        value: 'status'
      }, {
        align: 'left',
        sortable: false,
        text: 'Last Login',
        value: 'last'
      }, {
        align: 'left',
        sortable: false,
        text: 'Created',
        value: 'created'
      }, {
        align: 'left',
        sortable: false,
        text: 'Updated',
        value: 'updated'
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
      _listUsers(this.$store, paging)
        .then(function (response) {
          component.results = response.data
          component.users = response.data.results
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
