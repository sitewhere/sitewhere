<template>
  <span>
    <delete-dialog ref="dialog" title="Delete Schedule" width="400"
      :error="error" @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this schedule?
      </v-card-text>
    </delete-dialog>
    <v-btn dark icon small class="grey pa-0 ma-0"
      v-tooltip:top="{ html: 'Delete' }"
      @click.native.stop="showDeleteDialog">
      <v-icon fa>remove</v-icon>
    </v-btn>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteSchedule} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  props: ['token'],

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
      var component = this
      _deleteSchedule(this.$store, this.token, true)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('scheduleDeleted')
    }
  }
}
</script>

<style scoped>
</style>
