<template>
  <span>
    <delete-dialog ref="dialog" title="Delete Device Unit" width="400"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this device unit?
      </v-card-text>
    </delete-dialog>
    <v-btn class="ma-0" icon v-tooltip:left="{ html: 'Delete Device Unit' }"
      @click.native.stop="showDeleteDialog">
      <v-icon class="white--text">delete</v-icon>
    </v-btn>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'

export default {

  data: () => ({
  }),

  props: ['deviceUnit'],

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
    onDeleted: function () {
      this.getDialogComponent().closeDialog()
      this.$emit('deviceUnitDeleted', this.deviceUnit)
    }
  }
}
</script>

<style scoped>
</style>
