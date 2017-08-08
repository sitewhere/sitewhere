<template>
  <div>
    <schedule-dialog ref="dialog" title="Create Schedule"
      width="600" resetOnOpen="true" createLabel="Create" cancelLabel="Cancel"
      @payload="onCommit">
    </schedule-dialog>
    <floating-action-button label="Add Schedule" icon="add"
      @action="onOpenDialog">
    </floating-action-button>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import ScheduleDialog from './ScheduleDialog'
import {_createSchedule} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    ScheduleDialog
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
      console.log(payload)
      var component = this
      _createSchedule(this.$store, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('scheduleAdded')
    }
  }
}
</script>

<style scoped>
</style>
