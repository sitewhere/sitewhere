<template>
  <span>
    <device-status-dialog ref="dialog" title="Edit Device Status" width="600"
      :specification="specification"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </device-status-dialog>
    <v-btn icon v-tooltip:top="{ html: 'Edit Device Status' }"
      @click.native.stop="onOpenDialog">
      <v-icon class="grey--text">edit</v-icon>
    </v-btn>
  </span>
</template>

<script>
import DeviceStatusDialog from './DeviceStatusDialog'
import {
  _getDeviceStatus,
  _updateDeviceStatus
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    DeviceStatusDialog
  },

  props: ['specification', 'code'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getDeviceStatus(this.$store, this.specification.token, this.code)
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
      _updateDeviceStatus(this.$store, this.specification.token, this.code, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('statusUpdated')
    }
  }
}
</script>

<style scoped>
</style>
