<template>
  <div>
    <asset-dialog ref="dialog" title="Create Asset" width="600"
      :category="category" createLabel="Create" cancelLabel="Cancel"
      @payload="onCommit">
    </asset-dialog>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import AssetDialog from './AssetDialog'
import {
  _createHardwareAsset,
  _createPersonAsset,
  _createLocationAsset
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  props: ['category'],

  components: {
    FloatingActionButton,
    AssetDialog
  },

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Indicates if hardware or device asset category.
    isHardwareOrDevice: function () {
      return (this.category.assetType === 'Device') ||
        (this.category.assetType === 'Hardware')
    },

    // Indicates if person asset category.
    isPerson: function () {
      return (this.category.assetType === 'Person')
    },

    // Indicates if location asset category.
    isLocation: function () {
      return (this.category.assetType === 'Location')
    },

    // Loads the component from a json payload.
    load: function (payload) {
      this.getDialogComponent().load(payload)
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      this.getDialogComponent().reset()
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      if (this.isHardwareOrDevice()) {
        _createHardwareAsset(this.$store, this.category.id, payload)
          .then(function (response) {
            component.onCommitted(response)
          }).catch(function (e) {
          })
      } else if (this.isPerson()) {
        _createPersonAsset(this.$store, this.category.id, payload)
          .then(function (response) {
            component.onCommitted(response)
          }).catch(function (e) {
          })
      } else if (this.isLocation()) {
        _createLocationAsset(this.$store, this.category.id, payload)
          .then(function (response) {
            component.onCommitted(response)
          }).catch(function (e) {
          })
      }
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
