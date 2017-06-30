<template>
  <v-card flat>
    <v-card-row>
      <map-with-zone-overlay-panel :site='site' :visible='visible'
        :mode='mode' :height='height' @mapReady='onMapReady'>
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
              @colorChanged="onFillColorUpdated"
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
    map: null,
    zoneName: null,
    zoneBorder: '#333333',
    zoneFill: '#dc0000',
    zoneOpacity: 1
  }),

  components: {
    MapWithZoneOverlayPanel,
    ColorPicker
  },

  props: ['site', 'zone', 'height', 'visible', 'mode'],

  methods: {
    onMapReady: function (map) {
      this.$data.map = map
    },
    // Called when zone border color is chosen.
    onBorderColorUpdated: function (val) {
      console.log('border ' + val)
    },

    // Called when zone fill color is chosen.
    onFillColorUpdated: function (val) {
      console.log('fill ' + val)
    }
  }
}
</script>

<style scoped>
</style>
