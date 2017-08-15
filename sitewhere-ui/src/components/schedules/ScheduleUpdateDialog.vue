<template>
  <span>
    <schedule-dialog ref="dialog" title="Edit Schedule" width="700"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </schedule-dialog>
  </span>
</template>

<script>
import ScheduleDialog from './ScheduleDialog'
import {_getSchedule, _updateSchedule} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    ScheduleDialog
  },

  props: ['token'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getSchedule(this.$store, this.token)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (response) {
      this.getDialogComponent().load(response.data)
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateSchedule(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('edited')
    }
  }
}
</script>

<style scoped>
</style>
