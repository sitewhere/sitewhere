<template>
  <div>
    <device-dialog ref="dialog" title="Edit Device" width="700"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </device-dialog>
  </div>
</template>

<script>
import DeviceDialog from './DeviceDialog'
import {_getDevice, _updateDevice} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    DeviceDialog
  },

  props: ['hardwareId'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getDevice(this.$store, this.hardwareId)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Called after data is loaded.
    onLoaded: function (response) {
      this.getDialogComponent().load(response.data)
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateDevice(this.$store, this.hardwareId, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('deviceUpdated')
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
