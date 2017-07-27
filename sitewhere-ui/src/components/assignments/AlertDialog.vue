<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs dark v-model="active">
      <v-tabs-bar slot="activators">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="details" href="#details">
          Alert Details
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
                  <v-text-field required class="mt-1" label="Alert Type"
                    v-model="alertType" prepend-icon="info"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-select required :items="alertLevels" v-model="alertLevel"
                    label="Select Alert Level" light single-line auto
                    prepend-icon="warning" hide-details></v-select>
                </v-flex>
                <v-flex xs12>
                  <v-text-field required class="mt-1" multi-line label="Message"
                    v-model="alertMessage" prepend-icon="subject"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-text-field class="mt-1" label="Alternate Id"
                    v-model="alertAlternateId" prepend-icon="info"></v-text-field>
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
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'

export default {

  data: () => ({
    alertLevels: [
      {
        'text': 'Information',
        'value': 'Info'
      },
      {
        'text': 'Warning',
        'value': 'Warning'
      },
      {
        'text': 'Error',
        'value': 'Error'
      },
      {
        'text': 'Critical',
        'value': 'Critical'
      }
    ],
    active: null,
    menu: null,
    dialogVisible: false,
    alertAlternateId: null,
    alertType: null,
    alertLevel: null,
    alertMessage: null,
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
      payload.alternateId = this.$data.alertAlternateId
      payload.type = this.$data.alertType
      payload.level = this.$data.alertLevel
      payload.message = this.$data.alertMessage
      payload.source = 'Device'
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.alertAlternateId = null
      this.$data.alertType = null
      this.$data.alertLevel = null
      this.$data.alertMessage = null
      this.$data.metadata = []
      this.$data.active = 'details'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.alertAlternateId = payload.alternateId
        this.$data.alertType = payload.type
        this.$data.alertLevel = payload.level
        this.$data.alertMessage = payload.message
        this.$data.metadata = Utils.metadataToArray(payload.metadata)
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
