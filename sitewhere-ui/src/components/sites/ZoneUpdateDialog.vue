<template>
  <span>
    <zone-dialog :site='site' :zone="zone" style="display: none;" title="Update Zone" width="600"
      createLabel="Update" cancelLabel="Cancel" mode='update' @payload="onCommit">
    </zone-dialog>
    <v-btn class="ma-0" icon v-tooltip:top="{ html: 'Update Zone' }"
      @click.native.stop="onLoadZone">
      <v-icon class="grey--text">edit</v-icon>
    </v-btn>
  </span>
</template>

<script>
import ZoneDialog from './ZoneDialog'
import {getZone, updateZone} from '../../http/sitewhere-api'

export default {

  data: () => ({
    zone: null
  }),

  components: {
    ZoneDialog
  },

  props: ['site', 'token'],

  methods: {
    // Load zone information
    onLoadZone: function () {
      getZone(this.$store, this.token, this.onZoneLoaded, this.onFailed)
    },

    // Called after successful zone load.
    onZoneLoaded: function (response) {
      this.$data.zone = response.data
      this.onOpenDialog()
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      this.$children[0].reset()
      this.$children[0].openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      console.log(payload)
      updateZone(this.$store, this.zone.token, payload, this.onCommitted, this.onFailed)
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('zoneUpdated')
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
