<template>
  <batch-command-dialog ref="dialog" title="Invoke Batch Command" width="600"
    resetOnOpen="true" createLabel="Invoke" cancelLabel="Cancel"
    :filter="filter" @payload="onCommit" @scheduleUpdated="onScheduleUpdated">
  </batch-command-dialog>
</template>

<script>
import BatchCommandDialog from './BatchCommandDialog'
import {
  _createBatchCommandByCriteria
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    scheduleToken: null
  }),

  props: ['filter'],

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
      this.$data.scheduleToken = null
      this.getDialogComponent().reset()
      this.getDialogComponent().openDialog()
    },
    // Called if schedule token is updated.
    onScheduleUpdated: function (scheduleToken) {
      this.$data.scheduleToken = scheduleToken
    },
    // Handle payload commit.
    onCommit: function (payload) {
      var component = this

      let options = {}
      options.scheduleToken = this.$data.scheduleToken

      _createBatchCommandByCriteria(this.$store, options, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },
    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('batchCommandCreated')
    }
  }
}
</script>

<style scoped>
</style>
