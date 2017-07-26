<template>
  <div>
    <div v-if="specification">
      <v-card-text>
        {{ chosenText }}
      </v-card-text>
      <v-list two-line>
        <v-list-tile avatar :key="specification.token">
          <v-list-tile-avatar>
            <img :src="specification.assetImageUrl"></v-list-tile-avatar>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title v-html="specification.name"></v-list-tile-title>
            <v-list-tile-sub-title v-html="specification.asset.description">
            </v-list-tile-sub-title>
          </v-list-tile-content>
          <v-list-tile-action>
            <v-btn icon ripple @click.native.stop="onSpecificationRemoved">
              <v-icon class="grey--text">remove_circle</v-icon>
            </v-btn>
          </v-list-tile-action>
        </v-list-tile>
      </v-list>
    </div>
    <div v-else>
      <v-card-text>
        {{ notChosenText }}
      </v-card-text>
      <v-list v-if="specifications" class="specification-list" two-line>
        <template v-for="specification in specifications">
          <v-list-tile avatar :key="specification.token"
            @click.native.stop="onSpecificationChosen(specification)">
            <v-list-tile-avatar>
              <img :src="specification.assetImageUrl"></v-list-tile-avatar>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-html="specification.name"></v-list-tile-title>
              <v-list-tile-sub-title v-html="specification.asset.description">
              </v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </div>
  </div>
</template>

<script>
import {_listDeviceSpecifications} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    specification: null,
    specifications: []
  }),

  props: ['selected', 'chosenText', 'notChosenText'],

  // Initially load list of all sites.
  created: function () {
    var component = this
    _listDeviceSpecifications(this.$store, false, true)
      .then(function (response) {
        component.specifications = response.data.results
        if (component.selected) {
          component.onSpecificationChosen(component.selected)
        }
      }).catch(function (e) {
      })
  },

  watch: {
    selected: function (value) {
      if (value) {
        this.onSpecificationChosen(value)
      } else {
        this.onSpecificationRemoved()
      }
    }
  },

  methods: {
    // Called when a specification is chosen.
    onSpecificationChosen: function (specification) {
      this.$data.specification = specification
      this.$emit('specificationUpdated', specification)
    },

    // Allow another specification to be chosen.
    onSpecificationRemoved: function () {
      this.$data.specification = null
      this.$emit('specificationUpdated', null)
    }
  }
}
</script>

<style scoped>
.specification-list {
  max-height: 300px;
  overflow-y: auto;
}
</style>
