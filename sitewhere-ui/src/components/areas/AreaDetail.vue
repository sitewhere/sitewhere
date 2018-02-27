<template>
  <navigation-page v-if="area" icon="fa-map" :title="area.name">
    <div v-if="area.parentArea" slot="actions">
      <v-tooltip left>
        <v-btn icon slot="activator" @click="onUpOneLevel">
          <v-icon>fa-arrow-circle-up</v-icon>
        </v-btn>
        <span>Up One Level</span>
      </v-tooltip>
    </div>
    <div v-if="area" slot="content">
      <area-detail-header :area="area"
        @areaDeleted="onAreaDeleted" @areaUpdated="onAreaUpdated">
      </area-detail-header>
      <v-tabs v-model="active">
        <v-tabs-bar dark color="primary">
          <v-tabs-item key="contained" href="#contained">
            Contained Areas
          </v-tabs-item>
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
          <v-tabs-slider></v-tabs-slider>
        </v-tabs-bar>
        <v-tabs-items>
          <v-tabs-content key="contained" id="contained">
            <area-contained-areas :area="area"></area-contained-areas>
          </v-tabs-content>
          <v-tabs-content key="assignments" id="assignments">
            <area-assignments :area="area"></area-assignments>
          </v-tabs-content>
          <v-tabs-content key="locations" id="locations">
            <area-location-events :area="area"></area-location-events>
          </v-tabs-content>
          <v-tabs-content key="measurements" id="measurements">
            <area-measurement-events :area="area"></area-measurement-events>
          </v-tabs-content>
          <v-tabs-content key="alerts" id="alerts">
            <area-alert-events :area="area"></area-alert-events>
          </v-tabs-content>
          <v-tabs-content key="zones" id="zones">
            <area-zones :area="area"></area-zones>
          </v-tabs-content>
        </v-tabs-items>
      </v-tabs>
      <zone-create-dialog v-if="active === 'zones'" :area="area" @zoneAdded="onZoneAdded"/>
    </div>
  </navigation-page>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import AreaDetailHeader from './AreaDetailHeader'
import AreaContainedAreas from './AreaContainedAreas'
import AreaAssignments from './AreaAssignments'
import AreaLocationEvents from './AreaLocationEvents'
import AreaMeasurementEvents from './AreaMeasurementEvents'
import AreaAlertEvents from './AreaAlertEvents'
import AreaZones from './AreaZones'
import ZoneCreateDialog from './ZoneCreateDialog'

import {_getArea} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    area: null,
    active: null
  }),

  components: {
    Utils,
    NavigationPage,
    AreaDetailHeader,
    AreaContainedAreas,
    AreaAssignments,
    AreaLocationEvents,
    AreaMeasurementEvents,
    AreaAlertEvents,
    AreaZones,
    ZoneCreateDialog
  },

  // Called on initial create.
  created: function () {
    this.display(this.$route.params.token)
  },

  // Called when component is reused.
  beforeRouteUpdate (to, from, next) {
    this.display(to.params.token)
    next()
  },

  methods: {
    // Display area with the given token.
    display: function (token) {
      this.$data.token = token
      this.refresh()
    },
    // Called to refresh area data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load area information.
      _getArea(this.$store, token)
        .then(function (response) {
          component.onAreaLoaded(response.data)
        }).catch(function (e) {
        })
    },
    // Called after ara data is loaded.
    onAreaLoaded: function (area) {
      this.$data.area = area
      var section = {
        id: 'areas',
        title: 'Areas',
        icon: 'map',
        route: '/admin/areas/' + area.token,
        longTitle: 'Manage Area: ' + area.name
      }
      this.$store.commit('currentSection', section)
    },
    // Called after area is deleted.
    onAreaDeleted: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.token + '/areas')
      }
    },
    // Called after area is updated.
    onAreaUpdated: function () {
      this.refresh()
    },
    // Move up one level in the area hierarchy.
    onUpOneLevel: function () {
      Utils.routeTo(this, '/areas/' + this.area.parentArea.token)
    },
    // Called when a zone is added.
    onZoneAdded: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
