<template>
  <div>
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
              <v-container fluid grid-list-md v-if="areas">
                <v-layout row wrap>
                  <v-flex xs6 v-for="(area, index) in areas" :key="area.token">
                    <area-list-entry :area="area" @openArea="onOpenArea">
                    </area-list-entry>
                 </v-flex>
                </v-layout>
              </v-container>
              <pager :results="results" @pagingUpdated="updatePaging">
                <no-results-panel slot="noresults"
                  text="No Areas of This Type Found">
                </no-results-panel>
              </pager>
            </v-tabs-content>
          </v-tabs-items>
        </v-tabs>
      </div>
      <div slot="actions">
        <navigation-action-button icon="fa-edit" tooltip="Edit Area Type"
          @action="onEdit">
        </navigation-action-button>
        <navigation-action-button icon="fa-times" tooltip="Delete Area Type"
          @action="onDelete">
        </navigation-action-button>
      </div>
    </navigation-page>
    <area-type-update-dialog ref="edit" :token="areaType.token"
      :areaTypes="areaTypes" @areaTypeUpdated="onAreaTypeUpdated">
    </area-type-update-dialog>
    <area-type-delete-dialog ref="delete" :token="areaType.token"
      @areaTypeDeleted="onAreaTypeDeleted">
    </area-type-delete-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import NavigationPage from '../common/NavigationPage'
import NavigationActionButton from '../common/NavigationActionButton'
import AreaTypeDetailHeader from './AreaTypeDetailHeader'
import AreaTypeDeleteDialog from './AreaTypeDeleteDialog'
import AreaTypeUpdateDialog from './AreaTypeUpdateDialog'
import AreaListEntry from '../areas/AreaListEntry'
import {
  _getAreaType,
  _listAreaTypes,
  _listAreas
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    areaType: null,
    areaTypes: [],
    results: null,
    areas: [],
    paging: null,
    active: null
  }),

  components: {
    Pager,
    NoResultsPanel,
    NavigationPage,
    NavigationActionButton,
    AreaTypeDetailHeader,
    AreaListEntry,
    AreaTypeDeleteDialog,
    AreaTypeUpdateDialog
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
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refreshAreas()
    },
    // Display area with the given token.
    display: function (token) {
      this.$data.token = token
      this.refresh()
    },
    // Called to refresh area data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load area information.
      _getAreaType(this.$store, token)
        .then(function (response) {
          component.onDataLoaded(response.data)
        }).catch(function (e) {
        })
      _listAreaTypes(this.$store, false, 'page=1&pageSize=0')
        .then(function (response) {
          component.$data.areaTypes = response.data.results
        }).catch(function (e) {
        })

      this.refreshAreas()
    },
    refreshAreas: function () {
      var component = this

      // Search options.
      let options = {}
      options.rootOnly = false
      options.areaTypeToken = this.$data.token
      options.includeAreaType = false
      options.includeAssignments = false
      options.includeZones = false

      _listAreas(this.$store, options, this.$data.paging)
        .then(function (response) {
          component.results = response.data
          component.areas = response.data.results
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
    // Called to open area type edit dialog.
    onEdit: function () {
      this.$refs['edit'].onOpenDialog()
    },
    // Called when area type is updated.
    onAreaTypeUpdated: function () {
      this.refresh()
    },
    onDelete: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when area type is deleted.
    onAreaTypeDeleted: function () {
      Utils.routeTo(this, '/areatypes')
    },
    // Called to open an area.
    onOpenArea: function (area) {
      Utils.routeTo(this, '/areas/' + area.token)
    }
  }
}
</script>

<style scoped>
</style>
