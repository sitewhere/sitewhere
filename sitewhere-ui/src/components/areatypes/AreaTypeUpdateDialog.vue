<template>
  <div>
    <area-type-dialog ref="dialog" title="Edit Area Type" width="600"
      resetOnOpen="true" createLabel="Update" cancelLabel="Cancel"
      @payload="onCommit" :areaTypes="areaTypes">
    </area-type-dialog>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import AreaTypeDialog from './AreaTypeDialog'
import {_getAreaType, _updateAreaType} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    AreaTypeDialog
  },

  props: ['token', 'areaTypes'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getAreaType(this.$store, this.token)
        .then(function (response) {
          component.onDataLoaded(response)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onDataLoaded: function (response) {
      this.getDialogComponent().load(response.data)
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateAreaType(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('areaTypeUpdated')
    }
  }
}
</script>

<style scoped>
</style>
