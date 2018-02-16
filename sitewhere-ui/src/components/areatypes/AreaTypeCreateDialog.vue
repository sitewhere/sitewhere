<template>
  <div>
    <area-type-dialog ref="dialog" title="Create Area Type" width="600"
      resetOnOpen="true" createLabel="Create" cancelLabel="Cancel"
      @payload="onCommit" :areaTypes="areaTypes">
    </area-type-dialog>
    <floating-action-button label="Add Area Type" icon="fa-plus"
      @action="onOpenDialog">
    </floating-action-button>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import AreaTypeDialog from './AreaTypeDialog'
import {_createAreaType} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    AreaTypeDialog,
    FloatingActionButton
  },

  props: ['areaTypes'],

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
      _createAreaType(this.$store, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('areaTypeAdded')
    }
  }
}
</script>

<style scoped>
</style>
