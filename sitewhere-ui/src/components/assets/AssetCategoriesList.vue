<template>
  <div>
    <v-container fluid grid-list-md  v-if="categories">
      <v-layout row wrap>
         <v-flex xs6 v-for="(category, index) in categories" :key="category.id">
          <asset-category-list-panel :category="category" class="mb-1"
            @categoryOpened="onOpenCategory">
          </asset-category-list-panel>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" :pageSizes="pageSizes"
      @pagingUpdated="updatePaging">
    </pager>
    <asset-category-create-dialog @categoryAdded="refresh">
    </asset-category-create-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import AssetCategoryListPanel from './AssetCategoryListPanel'
import AssetCategoryCreateDialog from './AssetCategoryCreateDialog'
import {_listAssetCategories} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    categories: null,
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
    dateField: new Date()
  }),

  components: {
    Pager,
    AssetCategoryListPanel,
    AssetCategoryCreateDialog
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
      _listAssetCategories(this.$store, paging)
        .then(function (response) {
          component.results = response.data
          component.categories = response.data.results
        }).catch(function (e) {
        })
    },

    // Called when a new category is added.
    onCategoryAdded: function () {
      this.refresh()
    },

    // Called to open detail page for category.
    onOpenCategory: function (category) {
      Utils.routeTo(this, '/assets/categories/' + category.id)
    }
  }
}
</script>

<style scoped>
</style>
