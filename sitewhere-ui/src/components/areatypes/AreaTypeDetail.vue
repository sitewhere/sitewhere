<template>
  <navigation-page v-if="areaType" icon="fa-map" :title="areaType.name">
    <div v-if="areaType" slot="content">
      <area-type-detail-header :areaType="areaType" :areaTypes="areaTypes"
        @areaTypeDeleted="onAreaTypeDeleted"
        @areaTypeUpdated="onAreaTypeUpdated">
      </area-type-detail-header>
      <v-tabs v-model="active">
        <v-tabs-bar dark color="primary">
          <v-tabs-item key="areas" href="#areas">
            Areas
          </v-tabs-item>
          <v-tabs-slider></v-tabs-slider>
        </v-tabs-bar>
        <v-tabs-items>
          <v-tabs-content key="areas" id="areas">
            <v-card></v-card>
          </v-tabs-content>
        </v-tabs-items>
      </v-tabs>
    </div>
  </navigation-page>
</template>

<script>
import NavigationPage from '../common/NavigationPage'
import AreaTypeDetailHeader from './AreaTypeDetailHeader'
import {
  _getAreaType,
  _listAreaTypes
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    areaType: null,
    areaTypes: [],
    active: null
  }),

  components: {
    NavigationPage,
    AreaTypeDetailHeader
  },

  // Called on initial create.
  created: function () {
    this.display(this.$route.params.token)
  },

  // Called when component is reused.
  beforeRouteUpdate (to, from, next) {
    this.display(to.params.token)
    next()
  },

  methods: {
    // Display area with the given token.
    display: function (token) {
      this.$data.token = token
      this.refresh()
    },
    // Called to refresh area data.
    refresh: function () {
      var token = this.$data.token
      var component = this
      let paging = 'page=1&pageSize=0'

      // Load area information.
      _getAreaType(this.$store, token)
        .then(function (response) {
          component.onDataLoaded(response.data)
        }).catch(function (e) {
        })
      _listAreaTypes(this.$store, false, paging)
        .then(function (response) {
          component.$data.areaTypes = response.data.results
        }).catch(function (e) {
        })
    },
    // Called after data is loaded.
    onDataLoaded: function (areaType) {
      this.$data.areaType = areaType
      var section = {
        id: 'areatypes',
        title: 'Area Types',
        icon: 'map',
        route: '/admin/areatypes/' + areaType.token,
        longTitle: 'Manage Area Type: ' + areaType.name
      }
      this.$store.commit('currentSection', section)
    },
    // Called after area type is deleted.
    onAreaTypeDeleted: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.id + '/areatypes')
      }
    },
    // Called after area type is updated.
    onAreaTypeUpdated: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
