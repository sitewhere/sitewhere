<template>
  <span>
    <v-toolbar flat card dark color="primary">
      <v-toolbar-title>User Management</v-toolbar-title>
    </v-toolbar>
    <v-layout row wrap v-if="users">
      <v-flex xs12>
        <no-results-panel v-if="users.length === 0"
          text="No Users Found">
        </no-results-panel>
        <v-data-table v-if="users.length > 0" class="elevation-2 pa-0"
          :headers="headers" :items="users"
          :hide-actions="true" no-data-text="No Users Found">
          <template slot="items" slot-scope="props">
            <td width="5%" :title="props.item.username">
              {{ props.item.username }}
            </td>
            <td width="5%" :title="props.item.firstName">
              {{ props.item.firstName }}
            </td>
            <td width="10%" :title="props.item.lastName">
              {{ props.item.lastName }}
            </td>
            <td width="5%" :title="props.item.status">
              {{ props.item.status }}
            </td>
            <td width="15%"
              :title="utils.formatDate(props.item.createdDate)">
              {{ utils.formatDate(props.item.createdDate) }}
            </td>
            <td width="15%"
              :title="utils.formatDate(props.item.updatedDate)">
              {{ utils.formatDate(props.item.updatedDate) }}
            </td>
            <td width="12%" class="action-buttons">
              <actions-block @edited="refresh" @deleted="refresh">
                <user-update-dialog slot="edit"
                  :username="props.item.username">
                </user-update-dialog>
                <user-delete-dialog slot="delete"
                  :username="props.item.username">
                </user-delete-dialog>
              </actions-block>
            </td>
          </template>
        </v-data-table>
      </v-flex>
    </v-layout>
    <pager :pageSizes="pageSizes" :results="results"
      @pagingUpdated="updatePaging">
    </pager>
    <user-create-dialog @userAdded="refresh">
    </user-create-dialog>
  </span>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import ActionsBlock from '../common/ActionsBlock'
import UserCreateDialog from './UserCreateDialog'
import UserUpdateDialog from './UserUpdateDialog'
import UserDeleteDialog from './UserDeleteDialog'
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
    NoResultsPanel,
    ActionsBlock,
    UserCreateDialog,
    UserUpdateDialog,
    UserDeleteDialog
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
