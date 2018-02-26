<template>
  <div>
    <v-card flat>
      <!-- Map selector -->
      <v-card-text class="pt-4 pb-4">
        <v-container fluid class="pt-0 pb-0">
          <v-layout row wrap class="pb-2">
            <v-flex xs3>
              <v-subheader class="pt-3">Map Type</v-subheader>
            </v-flex>
            <v-flex xs9>
              <v-select :items="mapTypes" v-model="mapSelection"
                label="Select Map Type" light single-line auto
                prepend-icon="map" hide-details>
              </v-select>
            </v-flex>
          </v-layout>
          <v-divider class="mt-2"></v-divider>
        </v-container>
      </v-card-text>
      <!-- OpenStreetMap Configuration -->
      <v-card-text class="pt-0 pb-0" v-if="mapSelection === 'openstreetmap'">
        <v-container fluid class="pt-0 pb-0">
          <v-layout row wrap>
            <v-flex xs12>
              <v-text-field class="mt-1" label="Center Latitude" @change="emitPayload"
                v-model="mapLatitude" prepend-icon="place"></v-text-field>
            </v-flex>
            <v-flex xs12>
              <v-text-field class="mt-1" label="Center Longitude" @change="emitPayload"
                v-model="mapLongitude" prepend-icon="place"></v-text-field>
            </v-flex>
            <v-flex xs12>
              <v-text-field class="mt-1" label="Zoom Level" @change="emitPayload"
                v-model="mapZoom" prepend-icon="zoom_out_map"></v-text-field>
            </v-flex>
            <v-flex xs2>
            </v-flex>
            <v-flex xs10>
              <v-btn dark @click.native="onShowMapPanel" class="grey darken-1 mt-0 mb-3">
                <v-icon left dark>place</v-icon>
                Choose Center and Zoom Level on Map
              </v-btn>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <!-- GeoServer Configuration -->
      <v-card-text class="pt-0 pb-0" v-if="mapSelection === 'geoserver'">
        <v-container fluid class="pt-0 pb-0">
          <v-layout row wrap>
            <v-flex xs12>
              <v-text-field class="mt-1" label="GeoServer Base URL" @change="emitPayload"
                v-model="geoBaseUrl" prepend-icon="place"></v-text-field>
            </v-flex>
            <v-flex xs12>
              <v-text-field class="mt-1" label="GeoServer Layer" @change="emitPayload"
                v-model="geoLayer" prepend-icon="place"></v-text-field>
            </v-flex>
            <v-flex xs12>
              <v-text-field class="mt-1" label="Center Latitude" @change="emitPayload"
                v-model="mapLatitude" prepend-icon="place"></v-text-field>
            </v-flex>
            <v-flex xs12>
              <v-text-field class="mt-1" label="Center Longitude" @change="emitPayload"
                v-model="mapLongitude" prepend-icon="place"></v-text-field>
            </v-flex>
            <v-flex xs12>
              <v-text-field class="mt-1" label="Zoom Level" @change="emitPayload"
                v-model="mapZoom" prepend-icon="zoom_out_map"></v-text-field>
            </v-flex>
            <v-flex xs2>
            </v-flex>
            <v-flex xs10>
              <v-btn dark @click.native="onShowMapPanel" class="grey darken-1 mt-0 mb-3">
                <v-icon left dark>place</v-icon>
                Choose Center and Zoom Level on Map
              </v-btn>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
    </v-card>
    <v-card id="map-overlay" v-if="mapOverlayShown">
      <div style="height: 100%">
        <v-map :zoom="mapOverlayZoom" :center="mapOverlayLatLon"
          @l-move="onMapMove" @l-zoom="onMapZoom">
          <v-tilelayer url="http://{s}.tile.osm.org/{z}/{x}/{y}.png"></v-tilelayer>
        </v-map>
      </div>
      <v-tooltip top class="map-overlay-cancel">
        <v-btn icon dark color="grey" slot="activator"
          @click.stop="onCancelMapPanel">
          <v-icon>fa-undo</v-icon>
        </v-btn>
        <span>Cancel</span>
      </v-tooltip>
      <v-tooltip top class="map-overlay-ok">
        <v-btn icon dark color="blue" slot="activator"
          @click.stop="onSubmitMapPanel">
          <v-icon>fa-check</v-icon>
        </v-btn>
        <span>Update</span>
      </v-tooltip>
    </v-card>
  </div>
</template>

<script>
export default {

  data: () => ({
    mapTypes: [
      {
        'text': 'OpenStreetMap',
        'value': 'openstreetmap'
      },
      {
        'text': 'GeoServer Layer',
        'value': 'geoserver'
      }
    ],
    mapSelection: null,
    mapOverlayShown: false,
    mapOverlayZoom: 13,
    mapOverlayLatLon: [47.413220, -1.219482],
    origLatitude: null,
    origLongitude: null,
    origZoom: null,
    mapLatitude: null,
    mapLongitude: null,
    mapZoom: null,
    geoBaseUrl: null,
    geoLayer: null
  }),

  created: function () {
    var maps = this.$data.mapTypes
    this.$data.mapSelection = maps[0].value
  },

  props: ['json'],

  watch: {
    // Load panel from json payload.
    json: function (map) {
      if (map) {
        this.$data.mapOverlayShown = false
        if (map.type) {
          this.$data.mapSelection = map.type
        } else {
          this.$data.mapSelection = 'openstreetmap'
        }
        if (map.metadata) {
          this.$data.mapZoom = parseInt(map.metadata.zoomLevel)
          this.$data.mapLatitude = parseFloat(map.metadata.centerLatitude)
          this.$data.mapLongitude = parseFloat(map.metadata.centerLongitude)
          this.$data.geoBaseUrl = map.metadata.geoserverBaseUrl
          this.$data.geoLayer = map.metadata.geoserverLayerName
        } else {
          this.$data.mapZoom = null
          this.$data.mapLatitude = null
          this.$data.mapLongitude = null
          this.$data.geoBaseUrl = null
          this.$data.geoLayer = null
        }
      }
    },

    // Emit payload when map selection changes.
    mapSelection: function (updated) {
      this.emitPayload()
    }
  },

  methods: {
    // Emits JSON payload for map data.
    emitPayload: function () {
      var map = {}
      var type = this.$data.mapSelection
      map.type = type

      var mapmeta = {}
      mapmeta.zoomLevel = this.$data.mapZoom
      mapmeta.centerLatitude = this.$data.mapLatitude
      mapmeta.centerLongitude = this.$data.mapLongitude

      if (type === 'geoserver') {
        mapmeta.geoserverBaseUrl = this.$data.geoBaseUrl
        mapmeta.geoserverLayerName = this.$data.geoLayer
      }

      map.metadata = mapmeta
      this.$emit('mapConfig', map)
    },

    // Called to display map panel.
    onShowMapPanel: function () {
      var lat = this.$data.mapLatitude
      var lng = this.$data.mapLongitude
      var zoom = this.$data.mapZoom
      this.$data.origLatitude = lat
      this.$data.origLongitude = lng
      this.$data.origZoom = zoom

      // Create defaults if fields not populated.
      lat = lat || 33.7490
      lng = lng || -84.3880
      zoom = zoom || 13

      // Save defaults into form.
      this.$data.mapLatitude = lat
      this.$data.mapLongitude = lng
      this.$data.mapZoom = zoom

      // Push values into overlay.
      this.$data.mapOverlayLatLon = [lat, lng]
      this.$data.mapOverlayZoom = zoom
      this.$data.mapOverlayShown = true
    },

    // Called when overlay map is moved.
    onMapMove: function (e) {
      // This should be debounced!
      this.$data.mapLatitude = e.target.getCenter().lat
      this.$data.mapLongitude = e.target.getCenter().lng
    },

    // Called when overlay map is zoomed.
    onMapZoom: function (e) {
      this.$data.mapZoom = e.target.getZoom()
    },

    // Called to cancel map panel.
    onCancelMapPanel: function () {
      this.$data.mapLatitude = this.$data.origLatitude
      this.$data.mapLongitude = this.$data.origLongitude
      this.$data.mapZoom = this.$data.origZoom
      this.$data.mapOverlayShown = false
    },

    // Called to submit map panel.
    onSubmitMapPanel: function () {
      this.$data.mapOverlayShown = false
      this.emitPayload()
    }
  }
}
</script>

<style scoped>
#map-overlay {
  background-color: #fff;
  position: absolute;
  top: 0px;
  bottom: 0px;
  left: 0px;
  right: 0px;
}

.map-overlay-ok {
  position: absolute;
  bottom: 16px;
  right: 16px;
  z-index: 1000;
}

.map-overlay-cancel {
  position: absolute;
  bottom: 16px;
  right: 64px;
  z-index: 1000;
}
</style>
