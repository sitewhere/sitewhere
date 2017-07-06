<template>
  <div v-if="site">
    <v-app>
      <site-detail-header :site="site" @siteDeleted="onSiteDeleted" class="mb-3">
      </site-detail-header>
      <v-tabs class="elevation-2" dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
          <v-tabs-item key="assignments" href="#assignments">
            Device Assignments
          </v-tabs-item>
          <v-tabs-item key="locations" href="#locations">
            Location Events
          </v-tabs-item>
          <v-tabs-item key="measurements" href="#measurements">
            Measurement Events
          </v-tabs-item>
          <v-tabs-item key="alerts" href="#alerts">
            Alert Events
          </v-tabs-item>
          <v-tabs-item key="zones" href="#zones">
            Zones
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="assignments" id="assignments">
          <site-assignments :siteToken="site.token"></site-assignments>
        </v-tabs-content>
        <v-tabs-content key="locations" id="locations">
          <site-location-events :siteToken="site.token"></site-location-events>
        </v-tabs-content>
        <v-tabs-content key="measurements" id="measurements">
          <site-measurement-events :siteToken="site.token"></site-measurement-events>
        </v-tabs-content>
        <v-tabs-content key="alerts" id="alerts">
          <site-alert-events :siteToken="site.token"></site-alert-events>
        </v-tabs-content>
        <v-tabs-content key="zones" id="zones">
          <site-zones :site="site"></site-zones>
        </v-tabs-content>
      </v-tabs>
    </v-app>
  </div>
</template>

<script>
import SiteDetailHeader from './SiteDetailHeader'
import SiteAssignments from './SiteAssignments'
import SiteLocationEvents from './SiteLocationEvents'
import SiteMeasurementEvents from './SiteMeasurementEvents'
import SiteAlertEvents from './SiteAlertEvents'
import SiteZones from './SiteZones'

import {_getSite} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    site: null,
    active: null
  }),

  components: {
    SiteDetailHeader,
    SiteAssignments,
    SiteLocationEvents,
    SiteMeasurementEvents,
    SiteAlertEvents,
    SiteZones
  },

  created: function () {
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  methods: {
    // Called to refresh site data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load site information.
      _getSite(this.$store, token)
        .then(function (response) {
          component.onSiteLoaded(response.data)
        }).catch(function (e) {
        })
    },

    // Called after site data is loaded.
    onSiteLoaded: function (site) {
      this.$data.site = site
      var section = {
        id: 'sites',
        title: 'Sites',
        icon: 'map',
        route: '/admin/sites/' + site.token,
        longTitle: 'Manage Site: ' + site.name
      }
      this.$store.commit('currentSection', section)
    },

    // Called after site is deleted.
    onSiteDeleted: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/admin/' + tenant.id + '/sites')
      }
    }
  }
}
</script>

<style scoped>
</style>
