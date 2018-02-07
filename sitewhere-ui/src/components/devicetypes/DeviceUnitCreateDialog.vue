<template>
  <span>
    <device-unit-dialog ref="dialog" style="display: none;"
      title="Create Device Unit" width="600" createLabel="Create"
      cancelLabel="Cancel" mode='create' @payload="onCommit">
    </device-unit-dialog>
    <v-btn class="ma-0" dark icon v-tooltip:left="{ html: 'Add Nested Device Unit' }"
      @click.native.stop="onOpenDialog">
      <v-icon class="white--text">create_new_folder</v-icon>
    </v-btn>
  </span>
</template>

<script>
import lodash from 'lodash'
import DeviceUnitDialog from './DeviceUnitDialog'

export default {

  data: () => ({
  }),

  props: ['deviceUnit'],

  components: {
    DeviceUnitDialog
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
      let match = lodash.find(this.deviceUnit.deviceUnits, {'path': payload.path})
      if (match) {
        this.getDialogComponent().showError({
          'message': 'A unit with that path already exists.'
        })
      } else {
        this.onCommitted(payload)
      }
    },

    // Handle successful commit.
    onCommitted: function (payload) {
      this.getDialogComponent().closeDialog()
      this.$emit('deviceUnitAdded', payload)
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
