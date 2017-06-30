<template>
  <div :style="{ 'height': height, 'width': '100%' }">
    <v-map :zoom="13" :center="[47.413220, -1.219482]" ref="map">
      <v-marker :lat-lng="[33.7490, -84.3880]"></v-marker>
    </v-map>
  </div>
</template>

<script>
import {listZonesForSite} from '../../http/sitewhere-api'
import L from 'leaflet'
import D from 'leaflet-draw' // eslint-disable-line no-unused-vars

export default {

  data: () => ({
    editControl: null
  }),

  props: ['site', 'height', 'visible', 'borderColor',
    'fillColor', 'fillOpacity', 'mode'],

  watch: {
    visible: function () {
      this.onMapReady()
    }
  },

  methods: {
    // Access the Leaflet map directly.
    getMap: function () {
      return this.$refs.map.mapObject
    },

    // Invalid map size to force redraw.
    invalidateMap: function () {
      this.getMap().invalidateSize()
    },

    // Called when map is ready.
    onMapReady: function () {
      // this.resetMap()
      this.configureMapTiles()
      this.zoomAndCenterSite()
      this.loadZoneLayers()
      if (this.mode === 'create') {
        this.enableMapDrawing()
      }
      this.$emit('mapReady', this.getMap())
    },

    // Clear layers from map.
    resetMap: function () {
      var map = this.getMap()
      map.eachLayer(function (layer) {
        map.removeLayer(layer)
      })
      var edit = this.$data.editControl
      if (edit) {
        map.removeControl(edit)
      }
    },

    // Configure tile layer based on site preferences.
    configureMapTiles: function () {
      // MapQuest tiles no longer available. Use OSM.
      var site = this.site
      var meta = site.map.metadata
      if ((site.map.type === 'openstreetmap') || (site.map.type === 'mapquest')) {
        var osmUrl = 'http://{s}.tile.osm.org/{z}/{x}/{y}.png'
        var osm = new L.TileLayer(osmUrl, {
          maxZoom: 20
        })
        osm.addTo(this.getMap())
      } else if (site.map.type === 'geoserver') {
        var gsBaseUrl = meta.geoserverBaseUrl || 'http://localhost:8080/geoserver/'
        var gsRelativeUrl = 'geoserver/gwc/service/gmaps?layers='
        var gsLayerName = meta.geoserverLayerName || 'tiger:tiger_roads'
        var gsParams = '&zoom={z}&x={x}&y={y}&format=image/png'
        var gsUrl = gsBaseUrl + gsRelativeUrl + gsLayerName + gsParams
        var geoserver = new L.TileLayer(gsUrl, {
          maxZoom: 18
        })
        geoserver.addTo(this.getMap())
      }
    },

    // Zoom and center based on site preferences.
    zoomAndCenterSite: function () {
      var meta = this.site.map.metadata
      var latitude = meta.centerLatitude || 33.7490
      var longitude = meta.centerLongitude || -84.3880
      var zoomLevel = meta.zoomLevel || 10
      this.getMap().setView([ latitude, longitude ], zoomLevel)
    },

    // Load layers for site zones.
    loadZoneLayers: function () {
      // Asyncronously load zones and add layer to map.
      var component = this
      var site = this.site
      var query = {
        page: 1,
        pageSize: 0
      }
      if (site) {
        listZonesForSite(this.$store, site.token, query,
          function (response) {
            component.addZonesToFeatureGroup(response.data.results)
            component.$store.commit('error', null)
          }, function (e) {
            component.$store.commit('error', e)
          }
        )
      }
    },

    // Add zone layers to a feature group.
    addZonesToFeatureGroup: function (zones) {
      var featureGroup = new L.FeatureGroup()
      this.getMap().addLayer(featureGroup)

      // Add newest last.
      zones.reverse()

      var tokenToSkip = this.$data.tokenToSkip
      for (var zoneIndex = 0; zoneIndex < zones.length; zoneIndex++) {
        var zone = zones[zoneIndex]
        if (zone.token !== tokenToSkip) {
          var polygon = this.createPolygonForZone(zone)
          featureGroup.addLayer(polygon)
        }
      }
    },

    // Create polygon that represents one zone.
    createPolygonForZone: function (zone) {
      var coords = zone.coordinates
      var latLngs = []
      for (var coordIndex = 0; coordIndex < coords.length; coordIndex++) {
        var coordinate = coords[coordIndex]
        latLngs.push(new L.LatLng(coordinate.latitude, coordinate.longitude))
      }
      var polygon = new L.Polygon(latLngs, {
        'color': zone.borderColor,
        'opacity': 1,
        'weight': 3,
        'fillColor': zone.fillColor,
        'fillOpacity': zone.opacity,
        'clickable': false
      })
      return polygon
    },

    /** Enables drawing features on map */
    enableMapDrawing: function () {
      var options = {
        position: 'topright',
        draw: {
          polyline: false,
          circle: false,
          marker: false,
          polygon: {
            shapeOptions: {
              color: this.borderColor,
              opacity: 1,
              fillColor: this.fillColor,
              fillOpacity: this.fillAlpha
            }
          },
          rectangle: {
            shapeOptions: {
              color: this.borderColor,
              opacity: 1,
              fillColor: this.fillColor,
              fillOpacity: this.fillAlpha
            }
          }
        },
        edit: false
      }

      var drawControl = new L.Control.Draw(options)
      this.getMap().addControl(drawControl)
      this.$data.editControl = drawControl
    }
  }
}
</script>

<style scoped>
</style>
