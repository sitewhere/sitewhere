<template>
  <div>
    <device-group-element-dialog ref="dialog"
      title="Create Device Group Element" width="700"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </device-group-element-dialog>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import DeviceGroupElementDialog from './DeviceGroupElementDialog'
import {_addDeviceGroupElement} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    DeviceGroupElementDialog
  },

  props: ['token'],

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
      _addDeviceGroupElement(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('elementAdded')
    }
  }
}
</script>

<style scoped>
</style>
