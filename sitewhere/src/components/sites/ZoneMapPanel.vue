<template>
  <v-card flat>
    <v-card-row>
      <map-with-zone-overlay-panel :site='site' :visible='visible'
        :mode='mode' :height='height' :borderColor="zoneBorder"
        :fillColor="zoneFill" :fillOpacity="zoneOpacity"
        @zoneAdded="onZoneAdded">
      </map-with-zone-overlay-panel>
    </v-card-row>
    <v-card-row>
      <v-container fluid class="pt-0 pb-0 mr-2">
        <v-layout row wrap>
          <v-flex xs8>
            <v-text-field hide-details label="Zone name" v-model="zoneName"></v-text-field>
          </v-flex>
          <v-flex xs2 pa-2>
            <color-picker text="Border" :color="zoneBorder"
              @colorChanged="onBorderColorUpdated"
              v-tooltip:top="{ html: 'Border Color' }"></color-picker>
          </v-flex>
          <v-flex xs2 pa-2>
            <color-picker text="Fill" :color="zoneFill"
              @colorChanged="onFillColorUpdated" @opacityChanged="onFillOpacityUpdated"
              v-tooltip:top="{ html: 'Fill Color' }"></color-picker>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card-row>
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
        this.$data.zoneCoordinates = zone.coordinates
        this.$data.zoneBorder = zone.borderColor
        this.$data.zoneFill = zone.fillColor
        this.$data.zoneOpacity = zone.opacity
      }
    },

    // Update zone name.
    zoneName: function (name) {
      this.emitZoneIfReady()
    }
  },

  methods: {
    // Called when a new zone is added.
    onZoneAdded: function (val) {
      this.$data.zoneCoordinates = val
      this.emitZoneIfReady()
    },

    // Called when zone border color is chosen.
    onBorderColorUpdated: function (val) {
      this.$data.zoneBorder = val
      this.emitZoneIfReady()
    },

    // Called when zone fill color is chosen.
    onFillColorUpdated: function (val) {
      this.$data.zoneFill = val
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
