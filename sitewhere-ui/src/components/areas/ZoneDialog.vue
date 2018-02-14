<template>
  <span>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
      <v-tabs dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider></v-tabs-slider>
          <v-tabs-item key="settings" href="#settings">
            Zone Settings
          </v-tabs-item>
          <v-tabs-item key="metadata" href="#metadata">
            Metadata
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="settings" id="settings">
          <zone-map-panel height="400px" :site='site' :zone="zone"
            :visible="dialogVisible" :mode='mode' @zoneUpdated="onZoneUpdated">
          </zone-map-panel>
        </v-tabs-content>
        <v-tabs-content key="metadata" id="metadata">
          <metadata-panel :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
        </v-tabs-content>
      </v-tabs>
    </base-dialog>
  </span>
</template>

<script>
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'
import ZoneMapPanel from './ZoneMapPanel'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    zone: null,
    updatedZone: null,
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel,
    ZoneMapPanel
  },

  props: ['site', 'title', 'width', 'createLabel', 'cancelLabel', 'mode'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = Object.assign({}, this.$data.updatedZone)

      var metadata = {}
      var flat = this.$data.metadata
      for (var i = 0; i < flat.length; i++) {
        metadata[flat[i].name] = flat[i].value
      }
      payload.metadata = metadata

      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.metadata = []
      this.$data.active = 'settings'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()
      this.$data.zone = payload

      if (payload) {
        var meta = payload.metadata
        var flat = []
        if (meta) {
          for (var key in meta) {
            if (meta.hasOwnProperty(key)) {
              flat.push({name: key, value: meta[key]})
            }
          }
        }
        this.$data.metadata = flat
      }
    },

    // Called to open the dialog.
    openDialog: function () {
      this.$data.dialogVisible = true
    },

    // Called to open the dialog.
    closeDialog: function () {
      this.$data.dialogVisible = false
    },

    // Called to show an error message.
    showError: function (error) {
      this.$data.error = error
    },

    // Called as zone is updated.
    onZoneUpdated: function (zone) {
      this.$data.updatedZone = zone
    },

    // Called after create button is clicked.
    onCreateClicked: function (e) {
      var payload = this.generatePayload()
      this.$emit('payload', payload)
    },

    // Called after cancel button is clicked.
    onCancelClicked: function (e) {
      this.$data.dialogVisible = false
    },

    // Called when a metadata entry has been deleted.
    onMetadataDeleted: function (name) {
      var metadata = this.$data.metadata
      for (var i = 0; i < metadata.length; i++) {
        if (metadata[i].name === name) {
          metadata.splice(i, 1)
        }
      }
    },

    // Called when a metadata entry has been added.
    onMetadataAdded: function (entry) {
      var metadata = this.$data.metadata
      metadata.push(entry)
    }
  }
}
</script>

<style scoped>
</style>
