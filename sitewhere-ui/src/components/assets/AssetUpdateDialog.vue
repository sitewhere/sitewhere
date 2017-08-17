<template>
  <div>
    <asset-dialog ref="dialog" title="Edit Asset" width="600"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit"
      :category="category">
    </asset-dialog>
    <v-btn icon v-tooltip:left="{ html: 'Edit Asset' }"
      @click.native.stop="onOpenDialog">
      <v-icon class="grey--text text--lighten-2">edit</v-icon>
    </v-btn>
  </div>
</template>

<script>
import AssetDialog from './AssetDialog'
import {
  _getAssetById,
  _updatePersonAsset,
  _updateHardwareAsset,
  _updateLocationAsset
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    AssetDialog
  },

  props: ['category', 'asset'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getAssetById(this.$store, this.category.id, this.asset.id)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (response) {
      this.$data.asset = response.data
      this.getDialogComponent().load(response.data)
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      let type = this.category.assetType
      if (type === 'Device' || type === 'Hardware') {
        _updateHardwareAsset(
          this.$store, this.category.id, this.asset.id, payload)
          .then(function (response) {
            component.onCommitted(response)
          }).catch(function (e) {
          })
      } else if (type === 'Person') {
        _updatePersonAsset(
          this.$store, this.category.id, this.asset.id, payload)
          .then(function (response) {
            component.onCommitted(response)
          }).catch(function (e) {
          })
      } else if (type === 'Person') {
        _updateLocationAsset(
          this.$store, this.category.id, this.asset.id, payload)
          .then(function (response) {
            component.onCommitted(response)
          }).catch(function (e) {
          })
      }
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
