<template>
  <div>
    <v-app v-if="sites">
      <site-list-entry :site="site" v-for="(site, index) in sites" :key="site.token"
        @click="onOpenSite(site.token)" @siteDeleted="onSiteDeleted" class="mb-3">
      </site-list-entry>
      <pager></pager>
    </v-app>
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
    sites: null,
    error: null
  }),

  components: {
    Pager,
    SiteListEntry,
    SiteCreateDialog
  },

  methods: {
    // Refresh list of sites.
    refresh: function () {
      var component = this
      restAuthGet(this.$store,
        'sites',
        function (response) {
          component.sites = response.data.results
        }, function (e) {
          component.error = e
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
  },

  created: function () {
    this.refresh()
  }
}
</script>

<style scoped>
</style>
