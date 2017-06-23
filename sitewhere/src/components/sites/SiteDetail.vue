<template>
  <div v-if="site">
    <v-app>
      <site-list-entry :site="site" class="mb-3"></site-list-entry>
      <v-tabs light v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider></v-tabs-slider>
          <v-tabs-item key="assignments" href="#assignments">
            Device Assignments
          </v-tabs-item>
          <v-tabs-item key="locations" href="#locations">
            Location Events
          </v-tabs-item>
          <v-tabs-item key="measurements" href="#measurements">
            Measurement Events
          </v-tabs-item>
          <v-tabs-item key="alerts" href="#alerts">
            Alert Events
          </v-tabs-item>
          <v-tabs-item key="zones" href="#zones">
            Zones
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="assignments" id="assignments">
          <site-assignments :siteToken="site.token"></site-assignments>
        </v-tabs-content>
        <v-tabs-content key="locations" id="locations">
          <v-card flat>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="measurements" id="measurements">
          <v-card flat>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="alerts" id="alerts">
          <v-card flat>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="zones" id="zones">
          <v-card flat>
          </v-card>
        </v-tabs-content>
      </v-tabs>
    </v-app>
  </div>
  <div v-else-if="error">
    <v-container>
      <v-layout row wrap>
        <v-flex xs6 offset-xs3>
          <v-card raised class="grey lighten-4 white--text mt-3">
            <v-card-row>
              <v-alert error v-bind:value="true" style="width: 100%">
                {{error}}
              </v-alert>
            </v-card-row>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>

<script>
import SiteListEntry from './SiteListEntry'
import SiteAssignments from './SiteAssignments'
import {restAuthGet} from '../../http/http-common'

export default {

  data: () => ({
    token: null,
    site: null,
    active: null,
    error: null
  }),

  components: {
    SiteListEntry,
    SiteAssignments
  },

  created: function () {
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  methods: {
    // Called to refresh site data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load site information.
      restAuthGet(this.$store,
        'sites/' + token,
        function (response) {
          component.onSiteLoaded(response.data)
        }, function (e) {
          console.log(e)
          component.$data.error = e
        }
      )
    },

    // Called after site data is loaded.
    onSiteLoaded: function (site) {
      this.$data.site = site
      var section = {
        id: 'sites',
        title: 'Sites',
        icon: 'map',
        route: '/admin/sites/' + site.token,
        longTitle: 'Manage Site: ' + site.name
      }
      this.$store.commit('currentSection', section)
    }
  }
}
</script>

<style scoped>
</style>
