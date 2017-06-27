<template>
  <div>
    <v-app v-if="sites">
      <site-list-entry :site="site" v-for="(site, index) in sites" :key="site.token"
        @click="onOpenSite(site.token)" @siteDeleted="onSiteDeleted" class="mb-3">
      </site-list-entry>
    </v-app>
    <pager :results="results" @pagingUpdated="updatePaging"></pager>
    <site-create-dialog @siteAdded="onSiteAdded"/>
  </div>
</template>

<script>
import Pager from '../common/Pager'
import SiteListEntry from './SiteListEntry'
import SiteCreateDialog from './SiteCreateDialog'
import {restAuthGet} from '../../http/http-common'

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
      var query = this.$data.paging.query
      var component = this
      restAuthGet(this.$store,
        'sites?' + query,
        function (response) {
          component.results = response.data
          component.sites = response.data.results
          component.$emit('error', null)
        }, function (e) {
          component.$emit('error', e)
        }
      )
    },

    // Called when a new site is added.
    onSiteAdded: function () {
      this.refresh()
    },

    // Called when a site is deleted.
    onSiteDeleted: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
