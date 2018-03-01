<template>
  <div>
    <navigation-page v-if="asset" icon="fa-car" :title="asset.name">
      <div slot="content">
        <asset-detail-header v-if="asset" :asset="asset"
          @assetDeleted="onAssetDeleted"
          @assetUpdated="onAssetUpdated">
        </asset-detail-header>
        <v-tabs v-model="active">
          <v-tabs-bar dark color="primary">
            <v-tabs-item key="assignments" href="#assignments">
              Assignments
            </v-tabs-item>
            <v-tabs-slider></v-tabs-slider>
          </v-tabs-bar>
          <v-tabs-items>
            <v-tabs-content key="assignments" id="assignments">
              <v-layout row wrap v-if="assignments">
                <v-flex xs12>
                  <assignment-list-panel :assignment="assignment"
                    v-for="(assignment, index) in assignments"
                    :key="assignment.token"
                    @assignmentOpened="onOpenAssignment"
                    class="ma-2">
                  </assignment-list-panel>
                </v-flex>
              </v-layout>
              <pager :results="results" @pagingUpdated="updatePaging">
                <no-results-panel slot="noresults"
                  text="No Assignments Found for Asset">
                </no-results-panel>
              </pager>
            </v-tabs-content>
          </v-tabs-items>
        </v-tabs>
      </div>
      <div slot="actions">
        <navigation-action-button icon="fa-edit" tooltip="Edit Device"
          @action="onEdit">
        </navigation-action-button>
        <navigation-action-button icon="fa-times" tooltip="Delete Device"
          @action="onDelete">
        </navigation-action-button>
      </div>
    </navigation-page>
    <asset-update-dialog ref="edit"
      :token="asset.token" @asseUpdated="onAssetUpdated">
    </asset-update-dialog>
    <asset-delete-dialog ref="delete"
      :token="asset.token" @assetDeleted="onAssetDeleted">
    </asset-delete-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import NavigationActionButton from '../common/NavigationActionButton'
import Pager from '../common/Pager'
import NoResultsPanel from '../common/NoResultsPanel'
import AssetDetailHeader from './AssetDetailHeader'
import AssignmentListPanel from '../assignments/AssignmentListPanel'
import AssetDeleteDialog from './AssetDeleteDialog'
import AssetUpdateDialog from './AssetUpdateDialog'
import {
  _getAsset,
  _listDeviceAssignments
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    asset: null,
    assignments: null,
    paging: null,
    results: null,
    active: null
  }),

  components: {
    NavigationPage,
    NavigationActionButton,
    Pager,
    NoResultsPanel,
    AssetDetailHeader,
    AssignmentListPanel,
    AssetDeleteDialog,
    AssetUpdateDialog
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
      this.refreshAssignments()
    },
    // Display asset with the given token.
    display: function (token) {
      this.$data.token = token
      this.refresh()
    },
    // Called to refresh area data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load asset information.
      _getAsset(this.$store, token)
        .then(function (response) {
          component.onDataLoaded(response.data)
        }).catch(function (e) {
        })
    },
    // Refresh list of assignments for asset.
    refreshAssignments: function () {
      var component = this
      var paging = this.$data.paging.query

      // Query for assets with this asset type.
      let options = {}
      options.assetToken = this.$data.token
      options.includeDevice = true
      options.includeAsset = true

      _listDeviceAssignments(this.$store, options, paging)
        .then(function (response) {
          component.results = response.data
          component.$data.assignments = response.data.results
        }).catch(function (e) {
        })
    },
    // Called after data is loaded.
    onDataLoaded: function (asset) {
      this.$data.asset = asset
      var section = {
        id: 'assets',
        title: 'Assets',
        icon: 'fa-car',
        route: '/admin/assets/' + asset.token,
        longTitle: 'Manage Asset: ' + asset.name
      }
      this.$store.commit('currentSection', section)
    },
    // Called to open edit dialog.
    onEdit: function () {
      this.$refs['edit'].onOpenDialog()
    },
    // Called when asset is updated.
    onAssetUpdated: function () {
      this.refresh()
    },
    onDelete: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when asset is deleted.
    onAssetDeleted: function () {
      Utils.routeTo(this, '/assets')
    },
    // Called to open detail page for assignment.
    onOpenAssignment: function (assignment) {
      Utils.routeTo(this, '/assignments/' +
        encodeURIComponent(assignment.token))
    }
  }
}
</script>

<style scoped>
</style>
