<template>
  <div v-if="area">
    <v-app>
      <area-detail-header :area="area"
        @areaDeleted="onAreaDeleted" @areaUpdated="onAreaUpdated">
      </area-detail-header>
      <v-tabs v-model="active">
        <v-tabs-bar dark color="primary">
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
    </v-app>
  </div>
</template>

<script>
import AreaDetailHeader from './AreaDetailHeader'
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
    AreaDetailHeader,
    AreaAssignments,
    AreaLocationEvents,
    AreaMeasurementEvents,
    AreaAlertEvents,
    AreaZones,
    ZoneCreateDialog
  },

  created: function () {
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  methods: {
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
        this.$router.push('/tenants/' + tenant.id + '/areas')
      }
    },

    // Called after area is updated.
    onAreaUpdated: function () {
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
