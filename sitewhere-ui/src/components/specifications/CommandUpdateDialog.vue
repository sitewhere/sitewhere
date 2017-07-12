<template>
  <span>
    <command-dialog title="Edit Device Command" width="600" resetOnOpen="true"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </command-dialog>
    <v-btn icon v-tooltip:top="{ html: 'Edit Command' }"
      @click.native.stop="onOpenDialog">
      <v-icon class="grey--text">edit</v-icon>
    </v-btn>
  </span>
</template>

<script>
import CommandDialog from './CommandDialog'
import {_getDeviceCommand, _updateDeviceCommand} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    CommandDialog
  },

  props: ['token'],

  methods: {
    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getDeviceCommand(this.$store, this.token)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Called after data is loaded.
    onLoaded: function (response) {
      this.$children[0].load(response.data)
      this.$children[0].openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateDeviceCommand(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('commandUpdated')
    },

    // Handle failed commit.
    onFailed: function (error) {
      this.$children[0].showError(error)
    }
  }
}
</script>

<style scoped>
</style>
