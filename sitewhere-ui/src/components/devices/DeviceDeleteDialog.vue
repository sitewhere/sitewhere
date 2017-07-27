<template>
  <span>
    <delete-dialog ref="dialog" title="Delete Device" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this device?
      </v-card-text>
    </delete-dialog>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteDevice} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  props: ['hardwareId'],

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
      _deleteDevice(this.$store, this.hardwareId, true)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('deviceDeleted')
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
