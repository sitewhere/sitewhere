<template>
  <div>
    <asset-type-dialog ref="dialog" title="Edit Asset Type" width="600"
      resetOnOpen="true" createLabel="Update" cancelLabel="Cancel"
      @payload="onCommit">
    </asset-type-dialog>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import AssetTypeDialog from './AssetTypeDialog'
import {_getAssetType, _updateAssetType} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    AssetTypeDialog
  },

  props: ['token'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getAssetType(this.$store, this.token)
        .then(function (response) {
          component.onDataLoaded(response)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onDataLoaded: function (response) {
      this.getDialogComponent().load(response.data)
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateAssetType(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('assetTypeUpdated')
    }
  }
}
</script>

<style scoped>
</style>
