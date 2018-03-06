<template>
  <div>
    <device-group-dialog ref="dialog" title="Create Device Group" width="700"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </device-group-dialog>
    <floating-action-button label="Add Device Group" icon="fa-plus"
      @action="onOpenDialog">
    </floating-action-button>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import DeviceGroupDialog from './DeviceGroupDialog'
import {_createDeviceGroup} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    DeviceGroupDialog
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
      _createDeviceGroup(this.$store, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('groupAdded')
    }
  }
}
</script>

<style scoped>
</style>
