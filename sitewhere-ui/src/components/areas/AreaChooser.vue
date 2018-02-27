<template>
  <div>
    <div v-if="area">
      <v-card-text>
        {{ chosenText }}
      </v-card-text>
      <v-list two-line>
        <v-list-tile avatar :key="area.token">
          <v-list-tile-avatar>
            <img :src="area.imageUrl"></v-list-tile-avatar>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title v-html="area.name"></v-list-tile-title>
            <v-list-tile-sub-title v-html="area.description"></v-list-tile-sub-title>
          </v-list-tile-content>
          <v-list-tile-action>
            <v-btn icon ripple @click.stop="onAreaRemoved(true)">
              <v-icon class="grey--text">fa-times</v-icon>
            </v-btn>
          </v-list-tile-action>
        </v-list-tile>
      </v-list>
    </div>
    <div v-else>
      <v-card-text>
        {{ notChosenText }}
      </v-card-text>
      <v-list v-if="areas" class="area-list" two-line>
        <template v-for="area in areas">
          <v-list-tile avatar :key="area.token"
            @click.stop="onAreaChosen(area, true)">
            <v-list-tile-avatar>
              <img :src="area.imageUrl"></v-list-tile-avatar>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-html="area.name"></v-list-tile-title>
              <v-list-tile-sub-title v-html="area.description"></v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </div>
  </div>
</template>

<script>
import Lodash from 'lodash'
import {_listAreas} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    area: null,
    areas: []
  }),

  props: ['selected', 'selectedToken', 'chosenText', 'notChosenText'],

  // Initially load list of all sites.
  created: function () {
    var component = this
    _listAreas(component.$store, {}, 'page=1&pageSize=0')
      .then(function (response) {
        component.areas = response.data.results
        if (component.selected) {
          component.onAreaChosen(component.selected)
        }
      }).catch(function (e) {
      })
  },

  watch: {
    selected: function (value) {
      if (value) {
        this.onAreaChosen(value, false)
      } else {
        this.onAreaRemoved(false)
      }
    },
    selectedToken: function (value) {
      let site = Lodash.find(this.sites, { 'token': value })
      if (site) {
        this.onAreaChosen(site, false)
      } else {
        this.onAreaRemoved(false)
      }
    }
  },

  methods: {
    // Called whenan area is chosen.
    onAreaChosen: function (area, emit) {
      this.$data.area = area
      if (emit) {
        this.$emit('areaUpdated', area)
      }
    },

    // Allow another area to be chosen.
    onAreaRemoved: function (emit) {
      this.$data.area = null
      if (emit) {
        this.$emit('areaUpdated', null)
      }
    }
  }
}
</script>

<style scoped>
.area-list {
  max-height: 300px;
  overflow-y: auto;
}
</style>
