<template>
  <div>
    <scripts-dialog ref="dialog" title="Create Script" width="600" resetOnOpen="true"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </scripts-dialog>
    <v-tooltip top>
      <v-btn dark icon small
        @click.stop="onOpenDialog" slot="activator">
        <v-icon class="green--text tax--darken-2">fa-plus</v-icon>
      </v-btn>
      <span>Create Script</span>
    </v-tooltip>
  </div>
</template>

<script>
import ScriptsDialog from './ScriptsDialog'
import {_createTenantScript} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  props: ['tenantId'],

  components: {
    ScriptsDialog
  },

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
      payload.content = 'This is a test'
      var component = this
      _createTenantScript(this.$store, this.tenantId, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('siteAdded')
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
