<template>
  <span>
    <delete-dialog title="Delete Device Status" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this device status?
      </v-card-text>
    </delete-dialog>
    <v-btn icon v-tooltip:top="{ html: 'Delete Status' }"
      @click.native.stop="showDeleteDialog">
      <v-icon class="grey--text">delete</v-icon>
    </v-btn>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteDeviceStatus} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  props: ['specification', 'code'],

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
      _deleteDeviceStatus(this.$store, this.specification.token, this.code)
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
