<template>
  <span>
    <device-slot-dialog ref="dialog" style="display: none;"
      title="Create Device Slot" width="600" createLabel="Create"
      cancelLabel="Cancel" mode='create' @payload="onCommit">
    </device-slot-dialog>
    <v-btn class="ma-0" dark icon v-tooltip:left="{ html: 'Add Device Slot' }"
      @click.native.stop="onOpenDialog">
      <v-icon class="black--text">add</v-icon>
    </v-btn>
  </span>
</template>

<script>
import lodash from 'lodash'
import DeviceSlotDialog from './DeviceSlotDialog'

export default {

  data: () => ({
  }),

  components: {
    DeviceSlotDialog
  },

  props: ['deviceUnit'],

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
      let match = lodash.find(this.deviceUnit.deviceSlots, {'path': payload.path})
      if (match) {
        this.getDialogComponent().showError({
          'message': 'A slot with that path already exists.'
        })
      } else {
        this.onCommitted(payload)
      }
    },

    // Handle successful commit.
    onCommitted: function (payload) {
      this.getDialogComponent().closeDialog()
      this.$emit('deviceSlotAdded', payload)
    }
  }
}
</script>

<style scoped>
</style>
