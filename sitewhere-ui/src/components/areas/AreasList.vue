<template>
  <div>
    <v-container fluid grid-list-md v-if="areas">
      <v-layout row wrap>
         <v-flex xs6 v-for="(area, index) in areas" :key="area.token">
          <area-list-entry :area="area" @areaOpened="onOpenArea">
          </area-list-entry>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" @pagingUpdated="updatePaging"></pager>
    <area-create-dialog @areaAdded="onAreaAdded"/>
  </div>
</template>

<script>
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
      _listAreas(this.$store, false, false, paging)
        .then(function (response) {
          component.results = response.data
          component.areas = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when an area is clicked.
    onOpenArea: function (token) {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.id + '/areas/' + token)
      }
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
