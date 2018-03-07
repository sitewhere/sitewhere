<template>
  <batch-command-dialog ref="dialog" title="Invoke Batch Command" width="600"
    resetOnOpen="true" createLabel="Invoke" cancelLabel="Cancel"
    :deviceTypeToken="deviceTypeToken" @payload="onCommit"
    @scheduleUpdated="onScheduleUpdated">
  </batch-command-dialog>
</template>

<script>
import BatchCommandDialog from './BatchCommandDialog'
import {
  _createBatchCommandByCriteria,
  _scheduleBatchCommandByCriteria
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    schedule: null
  }),

  props: ['deviceTypeToken'],

  components: {
    BatchCommandDialog
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
    // Called if schedule selection is updated.
    onScheduleUpdated: function (schedule) {
      this.$data.schedule = schedule
    },
    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      if (this.schedule) {
        _scheduleBatchCommandByCriteria(this.$store, payload)
          .then(function (response) {
            component.onCommitted(response)
          }).catch(function (e) {
          })
      } else {
        _createBatchCommandByCriteria(this.$store, payload)
          .then(function (response) {
            component.onCommitted(response)
          }).catch(function (e) {
          })
      }
    },
    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('invocationAdded')
    }
  }
}
</script>

<style scoped>
</style>
