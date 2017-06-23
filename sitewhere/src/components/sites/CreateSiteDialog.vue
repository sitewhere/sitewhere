<template>
  <div>
    <site-dialog title="Create Site" width="600" resetOnOpen="true"
      createLabel="Create" cancelLabel="Cancel" @payload="onCommit">
    </site-dialog>
    <v-btn floating class="add-button red darken-1"
      v-tooltip:top="{ html: 'Add Site' }" @click.native.stop="onOpenDialog">
      <v-icon light>add</v-icon>
    </v-btn>
  </div>
</template>

<script>
import SiteDialog from './SiteDialog'
import {restAuthPost} from '../../http/http-common'

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
      restAuthPost(this.$store, '/sites', payload, this.onCommitted, this.onFailed)
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
  position: absolute;
  bottom: 16px;
  right: 16px;
}
</style>
