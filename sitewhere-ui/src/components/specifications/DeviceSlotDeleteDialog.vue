<template>
  <span>
    <delete-dialog title="Delete Device Slot" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this device slot?
      </v-card-text>
    </delete-dialog>
    <v-btn icon v-tooltip:top="{ html: 'Delete Slot' }"
      @click.native.stop="showDeleteDialog">
      <v-icon class="grey--text">delete</v-icon>
    </v-btn>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'

export default {

  data: () => ({
    error: null
  }),

  props: ['deviceSlot'],

  components: {
    DeleteDialog
  },

  methods: {
    // Show delete dialog.
    showDeleteDialog: function () {
      this.$children[0].openDialog()
    },

    // Perform delete.
    onDeleteConfirmed: function () {
      this.onDeleted()
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('slotDeleted')
    }
  }
}
</script>

<style scoped>
</style>
