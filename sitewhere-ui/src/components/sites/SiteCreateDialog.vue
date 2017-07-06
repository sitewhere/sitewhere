<template>
  <div>
    <site-dialog title="Create Site" width="600" resetOnOpen="true"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </site-dialog>
    <v-btn fab dark class="add-button red darken-1 elevation-5"
      v-tooltip:top="{ html: 'Add Site' }" @click.native.stop="onOpenDialog">
      <v-icon>add</v-icon>
    </v-btn>
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
    // Send event to open dialog.
    onOpenDialog: function () {
      this.$children[0].reset()
      this.$children[0].openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      _createSite(this.$store, payload)
        .then(function (response) {
          this.onCommitted(response)
        }).catch(function (e) {
          this.onFailed(e)
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('siteAdded')
    },

    // Handle failed commit.
    onFailed: function (error) {
      this.$children[0].showError(error)
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
