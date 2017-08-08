<template>
  <div>
    <asset-category-dialog ref="dialog" title="Edit Asset Category" width="500"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </asset-category-dialog>
  </div>
</template>

<script>
import AssetCategoryDialog from './AssetCategoryDialog'
import {_getAssetCategory, _updateAssetCategory} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    AssetCategoryDialog
  },

  props: ['category'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getAssetCategory(this.$store, this.category.id)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (response) {
      this.getDialogComponent().load(response.data)
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateAssetCategory(this.$store, this.category.id, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('categoryUpdated')
    }
  }
}
</script>

<style scoped>
</style>
