<template>
  <span>
    <delete-dialog ref="dialog" title="Delete Device Slot" width="400"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this device slot?
      </v-card-text>
    </delete-dialog>
    <v-btn class="ma-0" icon v-tooltip:left="{ html: 'Delete Device Slot' }"
      @click.native.stop="showDeleteDialog">
      <v-icon class="grey--text">delete</v-icon>
    </v-btn>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'

export default {

  data: () => ({
  }),

  props: ['deviceSlot'],

  components: {
    DeleteDialog
  },

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Show delete dialog.
    showDeleteDialog: function () {
      this.getDialogComponent().openDialog()
    },

    // Perform delete.
    onDeleteConfirmed: function () {
      this.onDeleted()
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('deviceSlotDeleted', this.deviceSlot)
    }
  }
}
</script>

<style scoped>
</style>
