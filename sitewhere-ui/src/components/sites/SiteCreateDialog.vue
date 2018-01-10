<template>
  <div>
    <site-dialog ref="dialog" title="Create Site" width="600" resetOnOpen="true"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </site-dialog>
    <v-tooltip top>
      <v-btn fab dark class="add-button red darken-1 elevation-5"
        @click.stop="onOpenDialog" slot="activator">
        <v-icon>fa-plus</v-icon>
      </v-btn>
      <span>Add Site</span>
    </v-tooltip>
  </div>
</template>

<script>
import SiteDialog from './SiteDialog'
import {_createSite} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    SiteDialog
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
      var component = this
      _createSite(this.$store, payload)
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
