<template>
  <div>
    <area-dialog ref="dialog" title="Create Area" width="600"
      resetOnOpen="true" createLabel="Create" cancelLabel="Cancel"
      @payload="onCommit" :parentArea="parentArea">
    </area-dialog>
    <floating-action-button label="Add Area" icon="fa-plus"
      @action="onOpenDialog">
    </floating-action-button>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import AreaDialog from './AreaDialog'
import {_createArea} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    AreaDialog,
    FloatingActionButton
  },

  props: ['parentArea'],

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
      var component = this
      _createArea(this.$store, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('areaAdded')
    }
  }
}
</script>

<style scoped>
</style>
