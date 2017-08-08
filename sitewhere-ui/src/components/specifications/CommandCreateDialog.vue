<template>
  <div>
    <command-dialog ref="dialog" title="Create Command" width="600" resetOnOpen="true"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </command-dialog>
    <v-btn fab dark class="add-button red darken-1 elevation-5"
      v-tooltip:top="{ html: 'Add Command' }" @click.native.stop="onOpenDialog">
      <v-icon>add</v-icon>
    </v-btn>
  </div>
</template>

<script>
import CommandDialog from './CommandDialog'
import {_createDeviceCommand} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    CommandDialog
  },

  props: ['specification'],

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
      _createDeviceCommand(this.$store, this.specification.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('commandAdded')
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
