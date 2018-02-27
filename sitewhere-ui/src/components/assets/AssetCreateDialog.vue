<template>
  <div>
    <asset-dialog ref="dialog" title="Create Asset" width="600"
      resetOnOpen="true" createLabel="Create" cancelLabel="Cancel"
      @payload="onCommit">
    </asset-dialog>
    <floating-action-button label="Add Asset" icon="fa-plus"
      @action="onOpenDialog">
    </floating-action-button>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import AssetDialog from './AssetDialog'
import {_createAsset} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    AssetDialog,
    FloatingActionButton
  },

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      this.getDialogComponent().reset()
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _createAsset(this.$store, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('assetAdded')
    }
  }
}
</script>

<style scoped>
</style>
