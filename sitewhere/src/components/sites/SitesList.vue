<template>
  <div>
    <v-app v-if="sites">
      <v-card hover class="site white mb-2 pa-2" v-for="(site, index) in sites" :key="site.token">
        <v-card-row>
          <div class="site-logo"
            v-bind:style="{ 'background': 'url(' + site.imageUrl + ')', 'background-size': 'cover', 'background-repeat': 'no-repeat'}">
          </div>
          <div class="site-name">{{site.name}}</div>
          <div class="site-desc">{{site.description}}</div>
        </v-card-row>
        <v-card-row class="site-actions" actions>
          <v-btn icon v-tooltip:top="{ html: 'Delete Site' }">
            <v-icon class="grey--text">delete</v-icon>
          </v-btn>
        </v-card-row>
      </v-card>
    </v-app>
    <create-site-dialog/>
  </div>
</template>

<script>
import CreateSiteDialog from './CreateSiteDialog'
import {restAuthGet} from '../../http/http-common'

export default {

  data: () => ({
    sites: null,
    error: null,
    site: {
      name: '',
      description: '',
      imageUrl: ''
    }
  }),

  components: {
    CreateSiteDialog
  },

  created: function () {
    var component = this
    restAuthGet(this.$store,
      'sites',
      function (response) {
        component.sites = response.data.results
      }, function (e) {
        component.error = e
      }
    )
  }
}
</script>

<style scoped>
.site {
  min-height: 120px;
}

.site-logo {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 100px;
  height: 100px;
}

.site-name {
  position: absolute;
  top: 6px;
  left: 125px;
  font-size: 20px;
  font-weight: 400;
}

.site-desc {
  position: absolute;
  top: 37px;
  left: 125px;
  font-size: 14px;
  width: 60%;
  max-height: 4.5em;
  overflow-y: hidden;
}

.site-actions {
  position: absolute;
  bottom: 0px;
  left: 0px;
  right: 0px;
}
</style>
