<template>
  <zone-dialog ref="dialog" :site='site' style="display: none;" title="Update Zone" width="600"
    createLabel="Update" cancelLabel="Cancel" mode='update' @payload="onCommit">
  </zone-dialog>
</template>

<script>
import ZoneDialog from './ZoneDialog'
import {_getZone, _updateZone} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    ZoneDialog
  },

  props: ['site', 'token'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Load zone information
    onOpenDialog: function () {
      var component = this
      _getZone(this.$store, this.token)
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
      _updateZone(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('updated')
    }
  }
}
</script>

<style scoped>
</style>
