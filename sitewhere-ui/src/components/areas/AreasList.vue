<template>
  <span>
    <area-list-filter-bar ref="filter"
      @parentAreaUpdated="onParentAreaUpdated">
    </area-list-filter-bar>
    <v-container fluid grid-list-md v-if="areas">
      <v-layout row wrap>
         <v-flex xs6 v-for="(area, index) in areas" :key="area.token">
          <area-list-entry :area="area" :parentArea="parentArea"
            @viewAreaData="onViewAreaData" @viewSubAreas="onViewSubAreas">
          </area-list-entry>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" @pagingUpdated="updatePaging"></pager>
    <area-create-dialog @areaAdded="onAreaAdded" :parentArea="parentArea"/>
  </span>
</template>

<script>
import Pager from '../common/Pager'
import AreaListFilterBar from './AreaListFilterBar'
import AreaListEntry from './AreaListEntry'
import AreaCreateDialog from './AreaCreateDialog'
import {_listAreas} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    areas: null,
    parentArea: null
  }),

  components: {
    Pager,
    AreaListFilterBar,
    AreaListEntry,
    AreaCreateDialog
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },
    // Refresh list of areas.
    refresh: function () {
      var paging = this.$data.paging.query
      var component = this
      _listAreas(this.$store, !this.parentArea, this.parentArea,
        true, false, false, paging)
        .then(function (response) {
          component.results = response.data
          component.areas = response.data.results
        }).catch(function (e) {
        })
    },
    // Called to view data for an area.
    onViewAreaData: function (area) {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.id + '/areas/' + area.token)
      }
    },
    // Called to view sub areas.
    onViewSubAreas: function (area) {
      this.$refs['filter'].pushArea(area)
      this.onParentAreaUpdated(area)
    },
    // Called when a new area is added.
    onAreaAdded: function () {
      this.refresh()
    },
    // Called when parent area is updated by filter bar.
    onParentAreaUpdated: function (area) {
      this.$data.parentArea = area ? area.token : null
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
