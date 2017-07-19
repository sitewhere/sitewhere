<template>
  <map-with-zone-overlay-panel :site="site" :height="height"
    mode="readOnly" visible="true" @mapReady="onMapReady">
  </map-with-zone-overlay-panel>
</template>

<script>
import L from 'leaflet'
import MapWithZoneOverlayPanel from '../sites/MapWithZoneOverlayPanel'
import {_getSite, _listLocationsForAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    site: null,
    map: null,
    locations: null,
    lastLocation: null
  }),

  props: ['assignment', 'height'],

  components: {
    MapWithZoneOverlayPanel
  },

  created: function () {
    // Load site information for map.
    var component = this
    _getSite(this.$store, this.assignment.siteToken)
      .then(function (response) {
        component.onSiteLoaded(response.data)
      }).catch(function (e) {
      })
  },

  methods: {
    // Called after data is loaded.
    onSiteLoaded: function (site) {
      this.$data.site = site
    },

    // Called when map is available.
    onMapReady: function (map) {
      this.$data.map = map
      this.refreshLocations()
    },

    // Refresh list of locations.
    refreshLocations: function () {
      var component = this

      // Load list of locations for assignment.
      _listLocationsForAssignment(this.$store, this.assignment.token)
        .then(function (response) {
          component.onLocationsLoaded(response.data.results)
        }).catch(function (e) {
        })
    },

    // Called after locations are loaded for assignment.
    onLocationsLoaded: function (locations) {
      this.$data.locations = locations
      this.addLocationsLayer()
    },

    // Add layer that contains recent locations.
    addLocationsLayer: function () {
      let lastLocation = null

      // Add newest last.
      let results = this.$data.locations
      results.reverse()

      // Add a marker for each location.
      let latLngs = []
      let markers = []

      for (var locIndex = 0; locIndex < results.length; locIndex++) {
        let location = results[locIndex]
        markers.push(L.marker([location.latitude, location.longitude]))
        let latLng = new L.LatLng(location.latitude, location.longitude)
        latLngs.push(latLng)
        lastLocation = latLng
      }

      let map = this.$data.map
      let group = L.featureGroup(markers).addTo(map)
      if (latLngs.length > 0) {
        let line = L.polyline(latLngs, {
          stroke: true,
          color: '#005599',
          weight: 5,
          opacity: 0.5
        })
        group.addLayer(line)
      }

      if (lastLocation) {
        map.panTo(lastLocation)
      }
      this.$data.lastLocation = lastLocation
    }
  }
}
</script>

<style scoped>
</style>
