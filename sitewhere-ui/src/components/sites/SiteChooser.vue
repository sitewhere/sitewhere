<template>
  <div>
    <div v-if="site">
      <v-card-text>
        {{ chosenText }}
      </v-card-text>
      <v-list two-line>
        <v-list-tile avatar :key="site.token">
          <v-list-tile-avatar>
            <img :src="site.imageUrl"></v-list-tile-avatar>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title v-html="site.name"></v-list-tile-title>
            <v-list-tile-sub-title v-html="site.description"></v-list-tile-sub-title>
          </v-list-tile-content>
          <v-list-tile-action>
            <v-btn icon ripple @click.native.stop="onSiteRemoved">
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
      <v-list v-if="sites" class="site-list" two-line>
        <template v-for="site in sites">
          <v-list-tile avatar :key="site.token"
            @click.native.stop="onSiteChosen(site)">
            <v-list-tile-avatar>
              <img :src="site.imageUrl"></v-list-tile-avatar>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-html="site.name"></v-list-tile-title>
              <v-list-tile-sub-title v-html="site.description"></v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
        </template>
      </v-list>
    </div>
  </div>
</template>

<script>
import {_listSites} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    site: null,
    sites: []
  }),

  props: ['selected', 'chosenText', 'notChosenText'],

  // Initially load list of all sites.
  created: function () {
    var component = this
    _listSites(component.$store)
      .then(function (response) {
        component.sites = response.data.results
        if (component.selected) {
          component.onSiteChosen(component.selected)
        }
      }).catch(function (e) {
      })
  },

  watch: {
    selected: function (value) {
      if (value) {
        this.onSiteChosen(value)
      } else {
        this.onSiteRemoved()
      }
    }
  },

  methods: {
    // Called when a site is chosen.
    onSiteChosen: function (site) {
      this.$data.site = site
      this.$emit('siteUpdated', site)
    },

    // Allow another site to be chosen.
    onSiteRemoved: function () {
      this.$data.site = null
      this.$emit('siteUpdated', null)
    }
  }
}
</script>

<style scoped>
.site-list {
  max-height: 300px;
  overflow-y: auto;
}
</style>
