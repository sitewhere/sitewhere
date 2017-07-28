<template>
  <div>
    <v-container fluid grid-list-md  v-if="assets">
      <v-layout row wrap>
         <v-flex xs6 v-for="(asset, index) in assets" :key="asset.id">
          <asset-list-panel :category="category" :asset="asset"
            class="mb-1" @refresh="refresh">
          </asset-list-panel>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" :pageSizes="pageSizes"
      @pagingUpdated="updatePaging">
    </pager>
    <asset-create-dialog ref="create" :category="category"
      @assetAdded="onAssetAdded">
    </asset-create-dialog>
    <asset-category-delete-dialog ref="delete" :category="category"
      @categoryDeleted="onCategoryDeleted">
    </asset-category-delete-dialog>
    <asset-category-update-dialog ref="update" :category="category"
      @categoryUpdated="refresh">
    </asset-category-update-dialog>
    <v-speed-dial v-model="fab" direction="top" hover fixed bottom right
      class="action-chooser-fab"
      transition="slide-y-reverse-transition">
      <v-btn slot="activator" class="red darken-1 elevation-5" dark
        fab hover>
        <v-icon fa style="margin-top: -10px;" class="fa-2x">bolt</v-icon>
      </v-btn>
      <v-btn fab dark small class="blue darken-3 elevation-5"
         v-tooltip:left="{ html: 'Update Asset Category' }"
          @click.native="onUpdateAssetCategory">
        <v-icon fa style="margin-top: -3px;">edit</v-icon>
      </v-btn>
      <v-btn fab dark small class="red darken-3 elevation-5"
         v-tooltip:left="{ html: 'Delete Asset Category' }"
          @click.native="onDeleteAssetCategory">
        <v-icon fa style="margin-top: -3px;">remove</v-icon>
      </v-btn>
      <v-btn fab dark small class="green darken-3 elevation-5"
         v-tooltip:left="{ html: 'Add Asset' }"
          @click.native="onAddAsset">
        <v-icon fa style="margin-top: -3px;">plus</v-icon>
      </v-btn>
    </v-speed-dial>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import AssetListPanel from './AssetListPanel'
import AssetCreateDialog from './AssetCreateDialog'
import AssetCategoryDeleteDialog from './AssetCategoryDeleteDialog'
import AssetCategoryUpdateDialog from './AssetCategoryUpdateDialog'
import {_getAssetCategory, _listCategoryAssets} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    categoryId: null,
    category: null,
    assets: null,
    pageSizes: [
      {
        text: '20',
        value: 20
      }, {
        text: '50',
        value: 50
      }, {
        text: '100',
        value: 100
      }
    ],
    dateField: new Date(),
    fab: null
  }),

  components: {
    Pager,
    AssetListPanel,
    AssetCreateDialog,
    AssetCategoryDeleteDialog,
    AssetCategoryUpdateDialog
  },

  // Store category id which is passed in URL.
  created: function () {
    this.$data.categoryId = this.$route.params.categoryId
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh list of sites.
    refresh: function () {
      let paging = this.$data.paging.query
      let component = this
      // Load information.
      _getAssetCategory(this.$store, this.$data.categoryId)
        .then(function (response) {
          component.onAssetCategoryLoaded(response.data)
        }).catch(function (e) {
        })
      _listCategoryAssets(this.$store, this.$data.categoryId, paging)
        .then(function (response) {
          component.results = response.data
          component.assets = response.data.results
        }).catch(function (e) {
        })
    },

    // Called after asset category is loaded.
    onAssetCategoryLoaded: function (category) {
      this.$data.category = category
      var section = {
        id: 'assets',
        title: 'Asset Category',
        icon: 'local_offer',
        route: '/admin/assets/categories/' + category.id,
        longTitle: 'Manage Asset Category: ' + category.name
      }
      this.$store.commit('currentSection', section)
    },

    // Show dialog on update requested.
    onUpdateAssetCategory: function () {
      this.$refs['update'].onOpenDialog()
    },

    // Show dialog on delete requested.
    onDeleteAssetCategory: function () {
      this.$refs['delete'].showDeleteDialog()
    },

    // Handle successful delete.
    onCategoryDeleted: function () {
      Utils.routeTo(this, '/assets/categories')
    },

    // Called when 'add asset' button is clicked.
    onAddAsset: function () {
      this.$refs['create'].onOpenDialog()
    },

    // Called when an asset is added.
    onAssetAdded: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
