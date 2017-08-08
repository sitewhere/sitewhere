<template>
  <span>
    <delete-dialog ref="dialog" title="Delete Asset" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this asset?
      </v-card-text>
    </delete-dialog>
    <v-btn icon v-tooltip:left="{ html: 'Delete Asset' }"
      @click.native.stop="showDeleteDialog">
      <v-icon class="grey--text text--lighten-2">delete</v-icon>
    </v-btn>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteAsset} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  props: ['category', 'asset'],

  components: {
    DeleteDialog
  },

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Show delete dialog.
    showDeleteDialog: function () {
      this.getDialogComponent().openDialog()
    },

    // Perform delete.
    onDeleteConfirmed: function () {
      var component = this
      _deleteAsset(this.$store, this.category.id, this.asset.id)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('assetDeleted')
    }
  }
}
</script>

<style scoped>
</style>
