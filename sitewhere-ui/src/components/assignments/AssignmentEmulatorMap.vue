<template>
  <div style="position: relative;">
    <map-with-zone-overlay-panel :site="site" :height="height"
      mode="readOnly" visible="true" @mapReady="onMapReady">
    </map-with-zone-overlay-panel>
    <div class="loc-overlay" v-if="addLocationMode">
      <span class="loc-overlay-text">Click Map to Add Location Event</span>
    </div>
  </div>
</template>

<script>
import L from 'leaflet'
import MapWithZoneOverlayPanel from '../areas/MapWithZoneOverlayPanel'
import {_getArea, _listLocationsForAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    site: null,
    map: null,
    locations: null,
    lastLocation: null,
    locationsLayer: null,
    addLocationMode: false
  }),

  props: ['assignment', 'height'],

  components: {
    MapWithZoneOverlayPanel
  },

  // Only load site after map is mounted.
  created: function () {
    // Load site information for map.
    var component = this
    _getArea(this.$store, this.assignment.site.token)
      .then(function (response) {
        component.onSiteLoaded(response.data)
      }).catch(function (e) {
      })
  },

  methods: {
    // Called after data is loaded.
    onSiteLoaded: function (site) {
      var component = this

      // Temporary hack for map issue.
      setTimeout(function () {
        component.$data.site = site
      }, 100)
    },

    // Called when map is available.
    onMapReady: function (map) {
      var component = this
      this.$data.map = map
      this.refreshLocations()
      map.on('click', function (e) {
        component.onMapClicked(e)
      })
    },

    // Refresh list of locations.
    refreshLocations: function () {
      // Load list of locations for assignment.
      var component = this
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

      // Gather markers and lat/long values.
      let latLngs = []
      let markers = []
      for (var locIndex = 0; locIndex < results.length; locIndex++) {
        let location = results[locIndex]
        markers.push(L.marker([location.latitude, location.longitude]))
        let latLng = new L.LatLng(location.latitude, location.longitude)
        latLngs.push(latLng)
        lastLocation = latLng
      }

      // Clear layer if it already exists.
      let map = this.$data.map
      let layer = this.$data.locationsLayer
      if (layer) {
        layer.remove()
      }

      // Create layer for markers and line.
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
      this.$data.locationsLayer = group

      this.$data.lastLocation = lastLocation
      if (lastLocation) {
        map.setView(lastLocation)
      }
    },

    // Called when map is clicked.
    onMapClicked: function (e) {
      this.exitAddLocationMode()
      this.$emit('location', e)
    },

    // Enter mode where next click adds a location.
    enterAddLocationMode: function () {
      this.$data.addLocationMode = true
    },

    // Exit mode where next click adds a location.
    exitAddLocationMode: function () {
      this.$data.addLocationMode = false
    },

    // Pan to the last recorded location.
    panToLastLocation: function () {
      if (this.$data.lastLocation) {
        this.$data.map.panTo(this.$data.lastLocation)
      }
    }
  }
}
</script>

<style scoped>
.loc-overlay {
  position: absolute;
  top: 0px;
  left: 0px;
  right: 0px;
  background-color: #fff;
  opacity: 0.8;
  padding: 10px 20px;
  z-index: 500;
  text-align: center;
}
.loc-overlay-text {
  font-size: 28px;
}
</style>
