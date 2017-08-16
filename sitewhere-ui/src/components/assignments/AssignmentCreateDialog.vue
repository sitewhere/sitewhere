<template>
  <div>
    <assignment-dialog ref="dialog" title="Create Device Assignment" width="700"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit"
      :hardwareId="hardwareId">
    </assignment-dialog>
  </div>
</template>

<script>
import AssignmentDialog from './AssignmentDialog'
import {_createDeviceAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    AssignmentDialog
  },

  props: ['hardwareId'],

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
      _createDeviceAssignment(this.$store, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('created')
    }
  }
}
</script>

<style scoped>
</style>
