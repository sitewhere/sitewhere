<template>
  <span>
    <user-dialog ref="dialog" title="Edit User" width="700"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </user-dialog>
    <v-btn dark icon small class="grey pa-0 ma-0"
      v-tooltip:top="{ html: 'Edit' }"
      @click.native.stop="onOpenDialog">
      <v-icon fa>edit</v-icon>
    </v-btn>
  </span>
</template>

<script>
import UserDialog from './UserDialog'
import {_getUser, _updateUser} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    UserDialog
  },

  props: ['username'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getUser(this.$store, this.username)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
          component.onFailed(e)
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
      _updateUser(this.$store, this.username, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('userUpdated')
    },

    // Handle failed commit.
    onFailed: function (error) {
      this.getDialogComponent().showError(error)
    }
  }
}
</script>

<style scoped>
</style>
