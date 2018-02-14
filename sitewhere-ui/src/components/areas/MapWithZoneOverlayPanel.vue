<template>
  <div :style="{ 'height': height, 'width': '100%' }">
    <v-map :zoom="13" :center="[47.413220, -1.219482]" ref="map">
    </v-map>
  </div>
</template>

<script>
import {_listZonesForArea} from '../../http/sitewhere-api-wrapper'
import L from 'leaflet'
import D from 'leaflet-draw' // eslint-disable-line no-unused-vars

export default {

  data: () => ({
    editControl: null,
    newZoneLayer: null
  }),

  props: ['area', 'zone', 'height', 'visible', 'borderColor',
    'fillColor', 'fillOpacity', 'mode'],

  watch: {
    area: function () {
      this.onMapReady()
      this.invalidateMap()
    },
    visible: function () {
      this.onMapReady()
      this.invalidateMap()
    },
    borderColor: function (val) {
      var newZoneLayer = this.getNewZoneLayer()
      if (newZoneLayer) {
        newZoneLayer.setStyle({ color: val })
      }
      this.updateEditControl()
    },
    fillColor: function (val) {
      var newZoneLayer = this.getNewZoneLayer()
      if (newZoneLayer) {
        newZoneLayer.setStyle({ fillColor: val })
      }
      this.updateEditControl()
    },
    fillOpacity: function (val) {
      var newZoneLayer = this.getNewZoneLayer()
      if (newZoneLayer) {
        newZoneLayer.setStyle({ fillOpacity: val })
      }
      this.updateEditControl()
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
      this.resetMap()
      this.configureMapTiles()
      this.zoomAndCenterSite()
      this.loadZoneLayers()
      if (this.mode === 'create') {
        this.enableMapDrawing()
      } else if (this.mode === 'update') {
        this.enableMapEditing()
      }
      this.$emit('mapReady', this.getMap())
    },

    // Get layer that contains new zone.
    getNewZoneLayer: function () {
      return this.$data.newZoneLayer
    },

    // Get the edit control.
    updateEditControl: function () {
      var edit = this.$data.editControl
      if (edit) {
        edit.setDrawingOptions(this.getDrawOptions())
      }
    },

    // Clear layers from map.
    resetMap: function () {
      var component = this
      var map = this.getMap()

      // Remove layers.
      map.eachLayer(function (layer) {
        map.removeLayer(layer)
      })

      // Remove edit control.
      var edit = this.$data.editControl
      if (edit) {
        map.removeControl(edit)
      }

      // Remove and add event handlers.
      map.off('draw:drawstart').on('draw:drawstart', function (e) {
        component.removeNewZoneLayer()
      })
      map.off('draw:created').on('draw:created', function (e) {
        component.addNewZoneLayer(e)
      })
    },

    // Add new zone layer.
    addNewZoneLayer: function (e) {
      var zcNewZoneLayer = e.layer
      this.getMap().addLayer(zcNewZoneLayer)
      this.$data.newZoneLayer = zcNewZoneLayer
      this.$emit('zoneAdded', zcNewZoneLayer._latlngs[0])
    },

    // Remove existing new zone layer.
    removeNewZoneLayer: function () {
      var newZoneLayer = this.getNewZoneLayer()
      if (newZoneLayer) {
        this.getMap().removeLayer(newZoneLayer)
        this.$data.newZoneLayer = null
      }
    },

    // Configure tile layer based on area preferences.
    configureMapTiles: function () {
      // MapQuest tiles no longer available. Use OSM.
      let area = this.area
      let meta = area.map.metadata
      if ((area.map.type === 'openstreetmap') || (area.map.type === 'mapquest')) {
        var osmUrl = 'http://{s}.tile.osm.org/{z}/{x}/{y}.png'
        var osm = new L.TileLayer(osmUrl, {
          maxZoom: 20
        })
        osm.addTo(this.getMap())
      } else if (area.map.type === 'geoserver') {
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

    // Zoom and center based on area preferences.
    zoomAndCenterSite: function () {
      let meta = this.area.map.metadata
      let latitude = meta.centerLatitude || 33.7490
      let longitude = meta.centerLongitude || -84.3880
      let zoomLevel = meta.zoomLevel || 10
      this.getMap().setView([ latitude, longitude ], zoomLevel)
    },

    // Load layers for area zones.
    loadZoneLayers: function () {
      // Asyncronously load zones and add layer to map.
      var component = this
      var area = this.area
      if (area) {
        _listZonesForArea(this.$store, area.token)
          .then(function (response) {
            component.addZonesToFeatureGroup(response.data.results)
          }).catch(function (e) {
          })
      }
    },

    // Add zone layers to a feature group.
    addZonesToFeatureGroup: function (zones) {
      var featureGroup = new L.FeatureGroup()
      this.getMap().addLayer(featureGroup)

      // Add newest last.
      zones.reverse()

      var tokenToSkip = (this.zone) ? this.zone.token : null
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

    // Get drawing options based on UI settings.
    getDrawOptions: function () {
      return {
        polyline: false,
        circle: false,
        marker: false,
        polygon: {
          shapeOptions: {
            color: this.borderColor,
            opacity: 1,
            fillColor: this.fillColor,
            fillOpacity: this.fillOpacity
          }
        },
        rectangle: {
          shapeOptions: {
            color: this.borderColor,
            opacity: 1,
            fillColor: this.fillColor,
            fillOpacity: this.fillOpacity
          }
        }
      }
    },

    /** Enables drawing features on map */
    enableMapDrawing: function () {
      var options = {
        position: 'topright',
        edit: false
      }
      options.draw = this.getDrawOptions()

      var drawControl = new L.Control.Draw(options)
      this.getMap().addControl(drawControl)
      this.$data.editControl = drawControl
    },

    // Enables editing features on map.
    enableMapEditing: function () {
      if (this.zone) {
        var editFeatures = new L.FeatureGroup()
        var editZone = this.createPolygonForZone(this.zone)
        editFeatures.addLayer(editZone)
        this.getMap().addLayer(editFeatures)
        editFeatures.bringToFront()

        var options = {
          position: 'topright',
          draw: false,
          edit: {
            featureGroup: editFeatures,
            remove: false
          }
        }

        var drawControl = new L.Control.Draw(options)
        this.getMap().addControl(drawControl)
        this.$data.editControl = drawControl

        var bounds = editZone.getBounds()
        this.getMap().fitBounds(bounds, {
          padding: [ 0, 0 ]
        })
      } else {
        console.log('no zone to edit!')
      }
    }
  }
}
</script>

<style scoped>
</style>
