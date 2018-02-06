<template>
  <div>
    <site-dialog ref="dialog" title="Create Site" width="600" resetOnOpen="true"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </site-dialog>
    <floating-action-button label="Add Site" icon="fa-plus"
      @action="onOpenDialog">
    </floating-action-button>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import SiteDialog from './SiteDialog'
import {_createSite} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    SiteDialog,
    FloatingActionButton
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
