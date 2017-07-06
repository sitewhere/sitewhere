<template>
  <span>
    <zone-dialog :site='site' style="display: none;" title="Create Zone" width="600"
      createLabel="Create" cancelLabel="Cancel" mode='create' @payload="onCommit">
    </zone-dialog>
    <v-btn fab small dark class="add-button red darken-1 elevation-5"
      v-tooltip:bottom="{ html: 'Add Zone' }" @click.native.stop="onOpenDialog">
      <v-icon>add</v-icon>
    </v-btn>
  </span>
</template>

<script>
import ZoneDialog from './ZoneDialog'
import {_createZone} from '../../http/sitewhere-api-wrapper'

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
      var component = this
      _createZone(this.$store, this.site.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
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
  top: 0px;
  right: 16px;
}
</style>
