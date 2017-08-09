<template>
  <div>
    <v-container fluid grid-list-md  v-if="specs">
      <v-layout row wrap>
         <v-flex xs6 v-for="(spec, index) in specs" :key="spec.token">
          <specification-list-entry :specification="spec">
          </specification-list-entry>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" @pagingUpdated="updatePaging"></pager>
    <specification-create-dialog @specificationAdded="onSpecificationAdded"/>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import SpecificationListEntry from './SpecificationListEntry'
import SpecificationCreateDialog from './SpecificationCreateDialog'
import {_listDeviceSpecifications} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    specs: null
  }),

  components: {
    Pager,
    SpecificationListEntry,
    SpecificationCreateDialog
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh list of sites.
    refresh: function () {
      var paging = this.$data.paging.query
      var component = this
      _listDeviceSpecifications(this.$store, false, true, paging)
        .then(function (response) {
          component.results = response.data
          component.specs = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when a new specification is added.
    onSpecificationAdded: function () {
      this.refresh()
    },

    // Called when a specification is deleted.
    onSpecificationDeleted: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
