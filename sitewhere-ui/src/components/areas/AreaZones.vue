<template>
  <div>
    <v-data-table v-if="zones && zones.length > 0" class="elevation-2 pa-0"
      :headers="headers" :items="zones" :hide-actions="true"
      no-data-text="No Zones Found for Area">
      <template slot="items" slot-scope="props">
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
          <actions-block @edited="refresh" @deleted="refresh">
            <zone-update-dialog slot="edit" :area="area"
              :token="props.item.token">
            </zone-update-dialog>
            <zone-delete-dialog slot="delete" :token="props.item.token">
            </zone-delete-dialog>
          </actions-block>
        </td>
      </template>
    </v-data-table>
    <pager :pageSizes="pageSizes" :results="results"
      @pagingUpdated="updatePaging">
      <no-results-panel slot="noresults"
        text="No Zones Found">
      </no-results-panel>
    </pager>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import ActionsBlock from '../common/ActionsBlock'
import NoResultsPanel from '../common/NoResultsPanel'
import ZoneUpdateDialog from './ZoneUpdateDialog'
import ZoneDeleteDialog from './ZoneDeleteDialog'
import {_listZonesForArea} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    zones: null,
    headers: [
      {
        align: 'left',
        sortable: false,
        text: 'Name',
        value: 'name'
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
        text: 'Updated Date',
        value: 'updated'
      }, {
        align: 'left',
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

  props: ['area'],

  components: {
    Pager,
    ActionsBlock,
    NoResultsPanel,
    ZoneUpdateDialog,
    ZoneDeleteDialog
  },

  watch: {
    // Refresh component if area is updated.
    area: function (value) {
      this.refresh()
    }
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
      var area = this.area
      var paging = this.$data.paging.query
      _listZonesForArea(this.$store, area.token, paging)
        .then(function (response) {
          component.results = response.data
          component.zones = response.data.results
        }).catch(function (e) {
        })
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
