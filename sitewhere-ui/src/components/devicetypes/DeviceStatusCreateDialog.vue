<template>
  <span>
    <device-status-dialog ref="dialog" :deviceType="deviceType"
      title="Create Device Status" width="600"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </device-status-dialog>
    <floating-action-button label="Add Status" icon="fa-plus"
      @action="onOpenDialog">
    </floating-action-button>
  </span>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import DeviceStatusDialog from './DeviceStatusDialog'
import {_createDeviceStatus} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    DeviceStatusDialog
  },

  props: ['deviceType'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      this.getDialogComponent().reset()
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _createDeviceStatus(this.$store, this.deviceType.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('statusAdded')
    }
  }
}
</script>

<style scoped>
</style>
