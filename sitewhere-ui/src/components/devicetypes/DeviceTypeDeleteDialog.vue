<template>
  <delete-dialog title="Delete Device Type" width="400" :error="error"
    @delete="onDeleteConfirmed">
    <v-card-text>
      Are you sure you want to delete this device type?
    </v-card-text>
  </delete-dialog>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteDeviceType} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  props: ['token'],

  components: {
    DeleteDialog
  },

  methods: {
    // Show delete dialog.
    showDeleteDialog: function () {
      this.$children[0].openDialog()
    },

    // Perform delete.
    onDeleteConfirmed: function () {
      var component = this
      _deleteDeviceType(this.$store, this.token, true)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('deviceTypeDeleted')
    }
  }
}
</script>

<style scoped>
</style>
