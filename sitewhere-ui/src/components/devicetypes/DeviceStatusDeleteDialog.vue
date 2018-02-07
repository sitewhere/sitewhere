<template>
  <span>
    <delete-dialog title="Delete Device Status" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this device status?
      </v-card-text>
    </delete-dialog>
    <v-tooltip top>
      <v-btn icon @click.stop="showDeleteDialog" slot="activator">
        <v-icon class="grey--text">fa-times</v-icon>
      </v-btn>
      <span>Delete Status</span>
    </v-tooltip>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteDeviceStatus} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  props: ['deviceType', 'code'],

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
      _deleteDeviceStatus(this.$store, this.deviceType.token, this.code)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('statusDeleted')
    }
  }
}
</script>

<style scoped>
</style>
