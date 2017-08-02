<template>
  <span>
    <delete-dialog ref="dialog" title="Delete Device Group Element" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this group element?
      </v-card-text>
    </delete-dialog>
    <v-btn class="ma-0" icon v-tooltip:top="{ html: 'Delete' }"
      @click.native.stop="showDeleteDialog">
      <v-icon class="grey--text">delete</v-icon>
    </v-btn>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteDeviceGroupElement} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  props: ['token', 'element'],

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
      _deleteDeviceGroupElement(this.$store, this.token,
        this.element.type, this.element.elementId)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('elementDeleted')
    },

    // Handle failed delete.
    onFailed: function (error) {
      this.getDialogComponent().showError(error)
    }
  }
}
</script>

<style scoped>
</style>
