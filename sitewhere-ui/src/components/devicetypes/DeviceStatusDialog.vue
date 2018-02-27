<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs dark v-model="active">
      <v-tabs-bar slot="activators">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="details" href="#details">
          Status Details
        </v-tabs-item>
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
                  <v-text-field required label="Status code"
                    v-model="statusCode" prepend-icon="info"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-text-field required label="Name"
                    v-model="statusName" prepend-icon="info"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <icon-selector v-model="statusIcon"></icon-selector>
                </v-flex>
                <v-flex xs12 class="mb-4">
                  <v-icon>info</v-icon>
                  <span class="color-label subheading ml-2">Background color:</span>
                  <color-picker v-model="statusBackground">
                  </color-picker>
                </v-flex>
                <v-flex xs12 class="mb-4">
                  <v-icon>info</v-icon>
                  <span class="color-label subheading ml-2">Foreground color:</span>
                  <color-picker v-model="statusForeground">
                  </color-picker>
                </v-flex>
                <v-flex xs12 class="mb-4">
                  <v-icon>info</v-icon>
                  <span class="color-label subheading ml-2">Border color:</span>
                  <color-picker v-model="statusBorder">
                  </color-picker>
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
import IconSelector from '../common/IconSelector'
import ColorPicker from '../common/ColorPicker'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    statusCode: null,
    statusName: null,
    statusIcon: null,
    statusBackground: null,
    statusForeground: null,
    statusBorder: null,
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel,
    IconSelector,
    ColorPicker
  },

  props: ['deviceType', 'title', 'width', 'createLabel', 'cancelLabel'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.code = this.$data.statusCode
      payload.name = this.$data.statusName
      payload.icon = this.$data.statusIcon
      payload.backgroundColor = this.$data.statusBackground
      payload.foregroundColor = this.$data.statusForeground
      payload.borderColor = this.$data.statusBorder
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.statusCode = null
      this.$data.statusName = null
      this.$data.statusIcon = null
      this.$data.statusBackground = null
      this.$data.statusForeground = null
      this.$data.statusBorder = null
      this.$data.metadata = []
      this.$data.active = 'details'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.statusCode = payload.code
        this.$data.statusName = payload.name
        this.$data.statusIcon = payload.icon
        this.$data.statusBackground = payload.backgroundColor
        this.$data.statusForeground = payload.foregroundColor
        this.$data.statusBorder = payload.borderColor
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
.color-label {
  display: inline-block;
  color: #888;
  min-width: 150px;
}
</style>
