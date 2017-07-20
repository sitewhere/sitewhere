<template>
  <div>
    <alert-dialog ref="dialog" title="Create Alert Event" width="600"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </alert-dialog>
  </div>
</template>

<script>
import AlertDialog from './AlertDialog'
import {_createAlertForAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  props: ['token'],

  components: {
    AlertDialog
  },

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
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
      _createAlertForAssignment(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('alertAdded')
    },

    // Handle failed commit.
    onFailed: function (error) {
      this.getDialogComponent().showError(error)
    }
  }
}
</script>

<style scoped>
</style>
