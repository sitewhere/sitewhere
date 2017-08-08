<template>
  <div>
    <specification-dialog ref="dialog" title="Create Device Specification"
      width="600" resetOnOpen="true" createLabel="Create" cancelLabel="Cancel"
      @payload="onCommit">
    </specification-dialog>
    <floating-action-button label="Add Specification" icon="add"
      @action="onOpenDialog">
    </floating-action-button>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import SpecificationDialog from './SpecificationDialog'
import {_createDeviceSpecification} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    SpecificationDialog
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
      _createDeviceSpecification(this.$store, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('specificationAdded')
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
