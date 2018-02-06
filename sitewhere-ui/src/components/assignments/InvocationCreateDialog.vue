<template>
  <div>
    <invocation-dialog ref="dialog" title="Invoke Device Command" width="600"
      resetOnOpen="true" createLabel="Invoke" cancelLabel="Cancel"
      :specificationToken="specificationToken" @payload="onCommit"
      @scheduleUpdated="onScheduleUpdated">
    </invocation-dialog>
    <v-btn fab dark class="add-button red darken-1 elevation-5"
      v-tooltip:top="{ html: 'Invoke Command' }" @click.native.stop="onOpenDialog">
      <v-icon>flash_on</v-icon>
    </v-btn>
  </div>
</template>

<script>
import InvocationDialog from './InvocationDialog'
import {
  _createCommandInvocationForAssignment,
  _scheduleCommandInvocation
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    schedule: null
  }),

  props: ['token', 'specificationToken'],

  components: {
    InvocationDialog
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
        _scheduleCommandInvocation(this.$store, this.token, this.schedule,
          payload)
          .then(function (response) {
            component.onCommitted(response)
          }).catch(function (e) {
          })
      } else {
        _createCommandInvocationForAssignment(this.$store, this.token, payload)
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
.add-button {
  position: fixed;
  bottom: 16px;
  right: 16px;
}
</style>
