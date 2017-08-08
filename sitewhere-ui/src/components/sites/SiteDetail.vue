<template>
  <div v-if="site">
    <v-app>
      <site-detail-header :site="site"
        @siteDeleted="onSiteDeleted" @siteUpdated="onSiteUpdated" class="mb-3">
      </site-detail-header>
      <v-tabs class="elevation-2" dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
          <v-tabs-item key="assignments" href="#assignments">
            Device Assignments
          </v-tabs-item>
          <v-tabs-item key="locations" href="#locations">
            Locations
          </v-tabs-item>
          <v-tabs-item key="measurements" href="#measurements">
            Measurements
          </v-tabs-item>
          <v-tabs-item key="alerts" href="#alerts">
            Alerts
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
      <zone-create-dialog v-if="active === 'zones'" :site="site" @zoneAdded="onZoneAdded"/>
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
import ZoneCreateDialog from './ZoneCreateDialog'

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
    SiteZones,
    ZoneCreateDialog
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
        this.$router.push('/tenants/' + tenant.id + '/sites')
      }
    },

    // Called after site is updated.
    onSiteUpdated: function () {
      this.refresh()
    },

    // Called when a zone is added.
    onZoneAdded: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
.add-button {
  position: fixed;
  right: 16px;
  bottom: 16px;
}
</style>
