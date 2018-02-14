<template>
  <span>
    <zone-dialog :site='site' style="display: none;" title="Create Zone" width="600"
      createLabel="Create" cancelLabel="Cancel" mode='create' @payload="onCommit">
    </zone-dialog>
    <v-tooltip top>
      <v-btn fab dark class="add-button red darken-1 elevation-5"
        @click.stop="onOpenDialog" slot="activator">
        <v-icon>fa-plus</v-icon>
      </v-btn>
      <span>Add Zone</span>
    </v-tooltip>
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
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('zoneAdded')
    }
  }
}
</script>

<style scoped>
.add-button {
  position: fixed;
  right: 16px;
  bottom: 16px;
}
</style>
