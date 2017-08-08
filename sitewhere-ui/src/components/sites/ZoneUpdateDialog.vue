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
import {_getZone, _updateZone} from '../../http/sitewhere-api-wrapper'

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
      var component = this
      _getZone(this.$store, this.token)
        .then(function (response) {
          component.onZoneLoaded(response)
        }).catch(function (e) {
        })
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
      var component = this
      _updateZone(this.$store, this.zone.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('zoneUpdated')
    }
  }
}
</script>

<style scoped>
</style>
