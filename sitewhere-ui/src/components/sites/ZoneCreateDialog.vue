<template>
  <span>
    <zone-dialog :site='site' style="display: none;" title="Create Zone" width="600"
      createLabel="Create" cancelLabel="Cancel" mode='create' @payload="onCommit">
    </zone-dialog>
    <v-btn floating class="add-button red darken-1 elevation-5"
      v-tooltip:bottom="{ html: 'Add Zone' }" @click.native.stop="onOpenDialog">
      <v-icon light>add</v-icon>
    </v-btn>
  </span>
</template>

<script>
import ZoneDialog from './ZoneDialog'
import {createZone} from '../../http/sitewhere-api'

export default {

  data: () => ({
  }),

  components: {
    ZoneDialog
  },

  props: ['site'],

  methods: {
    // Send event to open dialog.
    onOpenDialog: function () {
      this.$children[0].reset()
      this.$children[0].openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      createZone(this.$store, this.site.token, payload, this.onCommitted, this.onFailed)
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('zoneAdded')
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
  top: -35px;
  right: 16px;
}
</style>
