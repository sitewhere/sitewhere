<template>
  <device-type-dialog title="Edit Device Type" width="600" resetOnOpen="true"
    createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
  </device-type-dialog>
</template>

<script>
import DeviceTypeDialog from './DeviceTypeDialog'
import {
  _getDeviceType,
  _updateDeviceType
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    DeviceTypeDialog
  },

  props: ['token'],

  methods: {
    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getDeviceType(this.$store, this.token)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (response) {
      this.$children[0].load(response.data)
      this.$children[0].openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateDeviceType(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('deviceTypeUpdated')
    }
  }
}
</script>

<style scoped>
</style>
