<template>
  <delete-dialog ref="dialog" title="Delete Device" width="400" :error="error"
    @delete="onDeleteConfirmed">
    <v-card-text>
      Are you sure you want to delete this device?
    </v-card-text>
  </delete-dialog>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteDevice} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  props: ['token'],

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
      _deleteDevice(this.$store, this.token, true)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('deviceDeleted')
    }
  }
}
</script>

<style scoped>
</style>
