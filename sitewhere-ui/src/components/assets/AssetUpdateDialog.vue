<template>
  <div>
    <asset-dialog ref="dialog" title="Edit Asset" width="600"
      resetOnOpen="true" createLabel="Update" cancelLabel="Cancel"
      @payload="onCommit">
    </asset-dialog>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import AssetDialog from './AssetDialog'
import {_getAsset, _updateAsset} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    AssetDialog
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
      _getAsset(this.$store, this.token)
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
      _updateAsset(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('assetUpdated')
    }
  }
}
</script>

<style scoped>
</style>
