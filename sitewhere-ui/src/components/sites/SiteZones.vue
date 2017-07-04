<template>
  <div>
    <v-layout row wrap v-if="zones">
      <v-flex xs12>
        <v-data-table class="elevation-2 pa-0" :headers="headers" :items="zones"
          :hide-actions="true" no-data-text="No Zones Found for Site">
          <template slot="items" scope="props">
            <td width="30%" :title="props.item.name">
              <span class="zone-name">
                <div class="zone-outer" :style="{'border-color': props.item.borderColor}">
                  <div class="zone-inner"
                    :style="{'background-color': props.item.fillColor, 'opacity': props.item.opacity}">
                  </div>
                </div>{{ props.item.name }}
              </span>
            </td>
            <td width="40%" :title="props.item.token" class="zone-token">
              {{ props.item.token }}
            </td>
            <td width="10%" style="white-space: nowrap" :title="formatDate(props.item.createdDate)">
              {{ formatDate(props.item.createdDate) }}
            </td>
            <td width="10%" style="white-space: nowrap" :title="formatDate(props.item.updatedDate)">
              {{ formatDate(props.item.updatedDate) }}
            </td>
            <td width="1%" style="white-space: nowrap" title="Edit/Delete">
              <zone-delete-dialog :token="props.item.token"
                @zoneDeleted="onZoneDeleted">
              </zone-delete-dialog>
              <zone-update-dialog :site="site" :token="props.item.token"
                @zoneUpdated="onZoneUpdated">
              </zone-update-dialog>
            </td>
          </template>
        </v-data-table>
      </v-flex>
    </v-layout>
    <zone-create-dialog :site="site" @zoneAdded="onZoneAdded"/>
    <pager :pageSizes="pageSizes" :results="results" @pagingUpdated="updatePaging"></pager>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import ZoneCreateDialog from './ZoneCreateDialog'
import ZoneUpdateDialog from './ZoneUpdateDialog'
import ZoneDeleteDialog from './ZoneDeleteDialog'
import {listZonesForSite} from '../../http/sitewhere-api'

export default {

  data: () => ({
    results: null,
    paging: null,
    zones: null,
    headers: [
      {
        left: true,
        sortable: false,
        text: 'Name',
        value: 'name'
      }, {
        left: true,
        sortable: false,
        text: 'Token',
        value: 'token'
      }, {
        left: true,
        sortable: false,
        text: 'Created Date',
        value: 'created'
      }, {
        left: true,
        sortable: false,
        text: 'Updated Date',
        value: 'updated'
      }, {
        left: true,
        sortable: false,
        text: '',
        value: 'edit'
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

  props: ['site'],

  components: {
    Pager,
    ZoneCreateDialog,
    ZoneUpdateDialog,
    ZoneDeleteDialog
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh list of assignments.
    refresh: function () {
      var component = this
      var site = this.site
      var query = this.$data.paging.query
      listZonesForSite(this.$store, site.token, query,
        function (response) {
          component.results = response.data
          component.zones = response.data.results
          component.$store.commit('error', null)
        }, function (e) {
          component.$store.commit('error', e)
        }
      )
    },

    // Called when page number is updated.
    onPageUpdated: function (pageNumber) {
      this.$data.pager.page = pageNumber
      this.refresh()
    },

    // Format date.
    formatDate: function (date) {
      if (!date) {
        return 'N/A'
      }
      return this.$moment(date).format('YYYY-MM-DD H:mm:ss')
    },

    // Called when a zone is added.
    onZoneAdded: function () {
      this.refresh()
    },

    // Called when a zone is deleted.
    onZoneDeleted: function () {
      this.refresh()
    },

    // Called when a zone is updated.
    onZoneUpdated: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
.zone-name {
  display: inline-block;
  white-space: nowrap;
}
.zone-token {
  white-space: nowrap;
}
.zone-outer {
  display: inline-block;
  margin: 0px 7px 0px 0px;
  width: 18px;
  height: 100%;
  border: 2px solid #ccc;
  vertical-align: top;
}
.zone-inner {
  width: 100%;
  height: 15px;
}
</style>
