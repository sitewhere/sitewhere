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

  props: ['token'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },
    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this

      let options = {}
      options.includeDeviceType = true

      _getDevice(this.$store, this.token, options)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
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
      _updateDevice(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },
    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('deviceUpdated')
    }
  }
}
</script>

<style scoped>
</style>
