<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs dark v-model="active">
      <v-tabs-bar slot="activators">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="details" href="#details">
          Measurement Details
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
                    v-model="mxsAlternateId" prepend-icon="info"></v-text-field>
                </v-flex>
              </v-layout>
            </v-container>
            </v-card-text>
        </v-card>
        <measurements-panel :mxs="mxs"
          @mxDeleted="onMxDeleted" @mxAdded="onMxAdded">
        </measurements-panel>
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
import MeasurementsPanel from './MeasurementsPanel'

export default {

  data: () => ({
    active: null,
    menu: null,
    dialogVisible: false,
    mxsAlternateId: null,
    mxs: [],
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    MeasurementsPanel,
    MetadataPanel
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.alternateId = this.$data.mxsAlternateId
      payload.measurements = utils.arrayToMetadata(this.$data.mxs)
      payload.metadata = utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.mxsAlternateId = null
      this.$data.mxs = []
      this.$data.metadata = []
      this.$data.active = 'details'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.mxsAlternateId = payload.alternateId
        this.$data.mxs = utils.metadataToArray(payload.measurements)
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
    },

    // Called when a measurement entry has been deleted.
    onMxDeleted: function (name) {
      var mxs = this.$data.mxs
      for (var i = 0; i < mxs.length; i++) {
        if (mxs[i].name === name) {
          mxs.splice(i, 1)
        }
      }
    },

    // Called when a measurement entry has been added.
    onMxAdded: function (entry) {
      var mxs = this.$data.mxs
      mxs.push(entry)
    }
  }
}
</script>

<style scoped>
</style>
