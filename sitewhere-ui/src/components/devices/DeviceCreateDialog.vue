<template>
  <div>
    <device-dialog ref="dialog" title="Create Device" width="700" createLabel="Create"
      cancelLabel="Cancel" @payload="onCommit">
    </device-dialog>
    <floating-action-button label="Add Device" icon="fa-plus"
      @action="onOpenDialog">
    </floating-action-button>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import DeviceDialog from './DeviceDialog'
import {_createDevice} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    DeviceDialog
  },

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
      _createDevice(this.$store, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('deviceAdded')
    }
  }
}
</script>

<style scoped>
.add-button {
  position: fixed;
  bottom: 16px;
  right: 16px;
}
</style>
