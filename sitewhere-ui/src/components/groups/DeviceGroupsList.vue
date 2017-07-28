<template>
  <div>
    <v-container fluid grid-list-md  v-if="groups">
      <v-layout row wrap>
         <v-flex xs6 v-for="(group, index) in groups" :key="group.token">
          <device-group-list-panel :group="group" class="mb-1"
            @click.native="onOpenGroup(group)">
          </device-group-list-panel>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" :pageSizes="pageSizes"
      @pagingUpdated="updatePaging">
    </pager>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import DeviceGroupListPanel from './DeviceGroupListPanel'
import {_listDeviceGroups} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    groups: null,
    filter: null,
    pageSizes: [
      {
        text: '20',
        value: 20
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
    DeviceGroupListPanel
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh list of sites.
    refresh: function () {
      let paging = this.$data.paging.query
      let component = this
      _listDeviceGroups(this.$store, null, false, paging)
        .then(function (response) {
          component.results = response.data
          component.groups = response.data.results
        }).catch(function (e) {
        })
    },

    // Called to open detail page for group.
    onOpenGroup: function (group) {
      Utils.routeTo(this, '/groups/' + group.token)
    }
  }
}
</script>

<style scoped>
</style>
