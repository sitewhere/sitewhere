<template>
  <v-card flat>
    <v-card-text>
      <map-with-zone-overlay-panel :site='site' :zone="zone" :visible='visible'
        :mode='mode' :height='height' :borderColor="zoneBorder"
        :fillColor="zoneFill" :fillOpacity="zoneOpacity"
        @zoneAdded="onZoneAdded">
      </map-with-zone-overlay-panel>
    </v-card-text>
    <v-card-text>
      <v-container fluid class="pt-0 pb-0 mr-2">
        <v-layout row wrap>
          <v-flex xs8>
            <v-text-field hide-details label="Zone name" v-model="zoneName"></v-text-field>
          </v-flex>
          <v-flex xs2 pa-2>
            <v-tooltip top>
              <color-picker text="Border" v-model="zoneBorder">
              </color-picker>
              <span>Border Color</span>
            </v-tooltip>
          </v-flex>
          <v-flex xs2 pa-2>
            <v-tooltip top>
              <color-picker text="Fill" v-model="zoneFill"
                @opacityChanged="onFillOpacityUpdated">
              </color-picker>
              <span>Fill Color</span>
            </v-tooltip>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card-text>
  </v-card>
</template>

<script>
import MapWithZoneOverlayPanel from './MapWithZoneOverlayPanel'
import ColorPicker from '../common/ColorPicker'

export default {

  data: () => ({
    zoneName: null,
    zoneCoordinates: null,
    zoneBorder: '#333333',
    zoneFill: '#dc0000',
    zoneOpacity: 1
  }),

  components: {
    MapWithZoneOverlayPanel,
    ColorPicker
  },

  props: ['site', 'zone', 'height', 'visible', 'mode'],

  watch: {
    zone: function (zone) {
      if (zone) {
        this.$data.zoneName = zone.name
        this.$data.zoneCoordinates =
          this.buildLeafletCoordinates(zone.coordinates)
        this.$data.zoneBorder = zone.borderColor
        this.$data.zoneFill = zone.fillColor
        this.$data.zoneOpacity = zone.opacity
      } else {
        this.$data.zoneName = null
        this.$data.zoneCoordinates = null
      }
    },

    zoneBorder: function (value) {
      this.emitZoneIfReady()
    },

    zoneFill: function (value) {
      this.emitZoneIfReady()
    },

    zoneName: function (name) {
      this.emitZoneIfReady()
    }
  },

  methods: {
    // Convert zone coordinates to Leaflet format.
    buildLeafletCoordinates: function (input) {
      var converted = []
      for (var i = 0; i < input.length; i++) {
        var coord = input[i]
        converted.push({ 'lat': coord.latitude, 'lng': coord.longitude })
      }
      return converted
    },

    // Called when a new zone is added.
    onZoneAdded: function (val) {
      console.log(val)
      this.$data.zoneCoordinates = val
      this.emitZoneIfReady()
    },

    // Called when zone fill opacity is chosen.
    onFillOpacityUpdated: function (val) {
      this.$data.zoneOpacity = val
      this.emitZoneIfReady()
    },

    // Emit updates to zone as it changes.
    emitZoneIfReady: function () {
      var latlons = this.$data.zoneCoordinates
      if (latlons) {
        var zone = {
          'name': this.$data.zoneName,
          'borderColor': this.$data.zoneBorder,
          'fillColor': this.$data.zoneFill,
          'opacity': this.$data.zoneOpacity
        }
        var coords = []
        for (var index = 0; index < latlons.length; index++) {
          coords.push({
            'latitude': latlons[index].lat,
            'longitude': latlons[index].lng,
            'elevation': 0
          })
        }
        zone.coordinates = coords

        this.$emit('zoneUpdated', zone)
      }
    }
  }
}
</script>

<style scoped>
</style>
