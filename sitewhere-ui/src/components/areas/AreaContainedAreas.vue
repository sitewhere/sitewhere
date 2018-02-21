<template>
  <div>
    <v-container fluid grid-list-md v-if="areas">
      <v-layout row wrap>
        <v-flex xs6 v-for="(area, index) in areas" :key="area.token">
          <area-list-entry :area="area" @openArea="onOpenArea">
          </area-list-entry>
       </v-flex>
      </v-layout>
    </v-container>
    <area-create-dialog @areaAdded="refresh" :parentArea="area"/>
    <pager :results="results" @pagingUpdated="updatePaging">
      <no-results-panel slot="noresults"
        text="No Contained Areas Found">
      </no-results-panel>
    </pager>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import AreaListEntry from './AreaListEntry'
import AreaCreateDialog from './AreaCreateDialog'
import {_listAreas} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    areas: null
  }),

  props: ['area'],

  components: {
    Pager,
    NoResultsPanel,
    AreaListEntry,
    AreaCreateDialog
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
      var areaToken = this.area.token
      var paging = this.$data.paging.query

      // Search options.
      let options = {}
      options.rootOnly = false
      options.parentAreaToken = areaToken
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

    // Called when page number is updated.
    onPageUpdated: function (pageNumber) {
      this.$data.pager.page = pageNumber
      this.refresh()
    },

    // Called to open an area.
    onOpenArea: function (area) {
      Utils.routeTo(this, '/areas/' + area.token)
    }
  }
}
</script>

<style scoped>
</style>
