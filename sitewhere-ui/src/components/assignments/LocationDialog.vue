<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs dark v-model="active">
      <v-tabs-bar slot="activators">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="details" href="#details">
          Location Details
        </v-tabs-item>
        <slot name="tabitem"></slot>
        <v-tabs-item key="metadata" href="#metadata">
          Metadata
        </v-tabs-item>
      </v-tabs-bar>
      <slot name="tabcontent"></slot>
      <v-tabs-content key="details" id="details">
        <v-card flat>
          <v-card-text>
            <v-container fluid>
              <v-layout row wrap>
                <v-flex xs12>
                  <v-text-field class="mt-1" label="Alternate Id"
                    v-model="locAlternateId" prepend-icon="info"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-text-field required class="mt-1" label="Latitude" type="number"
                    v-model="locLatitude" prepend-icon="language"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-text-field required class="mt-1" label="Longitude" type="number"
                    v-model="locLongitude" prepend-icon="language"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-text-field class="mt-1" label="Elevation" type="number"
                    v-model="locElevation" prepend-icon="flight_takeoff"></v-text-field>
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>
        </v-card>
      </v-tabs-content>
      <v-tabs-content key="metadata" id="metadata">
        <metadata-panel :metadata="metadata"
          @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
      </v-tabs-content>
    </v-tabs>
  </base-dialog>
</template>

<script>
import utils from '../common/utils'
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'

export default {

  data: () => ({
    active: null,
    menu: null,
    dialogVisible: false,
    locAlternateId: null,
    locLatitude: null,
    locLongitude: null,
    locElevation: null,
    locEventDate: new Date(),
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.alternateId = this.$data.locAlternateId
      payload.latitude = this.$data.locLatitude
      payload.longitude = this.$data.locLongitude
      payload.elevation = this.$data.locElevation
      payload.metadata = utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.locAlternateId = null
      this.$data.locLatitude = null
      this.$data.locLongitude = null
      this.$data.locElevation = null
      this.$data.metadata = []
      this.$data.active = 'details'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.locAlternateId = payload.alternateId
        this.$data.locLatitude = payload.latitude
        this.$data.locLongitude = payload.longitude
        this.$data.locElevation = payload.elevation
        this.$data.metadata = utils.metadataToArray(payload.metadata)
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
