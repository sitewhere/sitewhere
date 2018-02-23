<template>
  <navigation-page icon="fa-map" title="Areas">
    <div slot="content">
      <v-container fluid grid-list-md v-if="areas">
        <v-layout row wrap>
           <v-flex xs6 v-for="(area, index) in areas" :key="area.token">
            <area-list-entry :area="area" @openArea="onOpenArea">
            </area-list-entry>
          </v-flex>
        </v-layout>
      </v-container>
      <pager :results="results" @pagingUpdated="updatePaging"></pager>
      <area-create-dialog @areaAdded="onAreaAdded"/>
    </div>
  </navigation-page>
</template>

<script>
import NavigationPage from '../common/NavigationPage'
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import AreaListEntry from './AreaListEntry'
import AreaCreateDialog from './AreaCreateDialog'
import {_listAreas} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    areas: null
  }),

  components: {
    NavigationPage,
    Pager,
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

      // Search options.
      let options = {}
      options.rootOnly = !this.parentArea
      options.parentAreaToken = this.parentArea
      options.includeAreaType = true
      options.includeAssignments = false
      options.includeZones = false

      _listAreas(this.$store, options, paging)
        .then(function (response) {
          component.results = response.data
          component.areas = response.data.results
        }).catch(function (e) {
        })
    },
    // Called to open an area.
    onOpenArea: function (area) {
      Utils.routeTo(this, '/areas/' + area.token)
    },
    // Called when a new area is added.
    onAreaAdded: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
