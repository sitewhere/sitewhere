<template>
  <div>
    <v-container fluid grid-list-md  v-if="sites">
      <v-layout row wrap>
         <v-flex xs6 v-for="(site, index) in sites" :key="site.token">
          <site-list-entry :site="site"></site-list-entry>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" @pagingUpdated="updatePaging"></pager>
    <site-create-dialog @siteAdded="onSiteAdded"/>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import SiteListEntry from './SiteListEntry'
import SiteCreateDialog from './SiteCreateDialog'
import {_listSites} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    sites: null
  }),

  components: {
    Pager,
    SiteListEntry,
    SiteCreateDialog
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
      _listSites(this.$store, false, false, paging)
        .then(function (response) {
          component.results = response.data
          component.sites = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when a new site is added.
    onSiteAdded: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
