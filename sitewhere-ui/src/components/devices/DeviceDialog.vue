<template>
  <div>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked"
      hideButtons="true">
      <v-stepper v-model="step">
        <v-stepper-header>
          <v-stepper-step step="1" :complete="step > 1">Device</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step step="2" :complete="step > 3">Device Type</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step step="3">Metadata<small>Optional</small></v-stepper-step>
        </v-stepper-header>
        <v-stepper-content step="1">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12>
                    <v-text-field required class="mt-1" label="Token"
                      v-model="devToken" prepend-icon="info"></v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field class="mt-1" multi-line label="Comments"
                      v-model="devComments" prepend-icon="subject">
                    </v-text-field>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
          </v-card>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn flat @click.native="onCancelClicked">{{ cancelLabel }}</v-btn>
            <v-btn color="primary" :disabled="!firstPageComplete" flat
              @click="step = 2">Choose Device Type
              <v-icon light>keyboard_arrow_right</v-icon>
            </v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="2">
          <device-type-chooser
            chosenText="Device will implement the device type below."
            notChosenText="Choose a device type that will be implemented by the device:"
            :selectedToken="devDeviceTypeToken"
            @deviceTypeUpdated="onDeviceTypeUpdated">
          </device-type-chooser>
          <v-card-actions>
            <v-btn color="primary" flat @click="step = 1">
              <v-icon light>keyboard_arrow_left</v-icon>
              Back
            </v-btn>
            <v-spacer></v-spacer>
            <v-btn flat @click="onCancelClicked">{{ cancelLabel }}</v-btn>
            <v-btn color="primary" flat :disabled="!secondPageComplete"
              @click="onCreateClicked">{{ createLabel }}</v-btn>
            <v-btn color="primary" flat :disabled="!secondPageComplete"
              @click="step = 3">Add Metadata
              <v-icon light>keyboard_arrow_right</v-icon>
            </v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="3">
          <metadata-panel class="mb-3" :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
            <v-card-actions>
              <v-btn color="primary" flat @click.native="step = 2">
                <v-icon light>keyboard_arrow_left</v-icon>
                Back
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn flat @click="onCancelClicked">{{ cancelLabel }}</v-btn>
              <v-btn color="primary" flat :disabled="!secondPageComplete"
                @click="onCreateClicked">{{ createLabel }}</v-btn>
            </v-card-actions>
        </v-stepper-content>
      </v-stepper>
    </base-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'
import DeviceTypeChooser from '../devicetypes/DeviceTypeChooser'

export default {

  data: () => ({
    step: null,
    dialogVisible: false,
    devToken: null,
    devComments: null,
    devDeviceTypeToken: null,
    metadata: [],
    assetModules: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel,
    DeviceTypeChooser
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  computed: {
    // Indicates if first page fields are filled in.
    firstPageComplete: function () {
      return !Utils.isBlank(this.$data.devToken)
    },

    // Indicates if second page is complete.
    secondPageComplete: function () {
      return this.firstPageComplete && (this.$data.devDeviceTypeToken != null)
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.token = this.$data.devToken
      payload.comments = this.$data.devComments
      payload.deviceTypeToken = this.$data.devDeviceTypeToken
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function () {
      this.$data.devToken = null
      this.$data.devComments = null
      this.$data.devDeviceTypeToken = null
      this.$data.metadata = []
      this.$data.step = 1
      this.$data.error = null
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.devToken = payload.token
        this.$data.devComments = payload.comments
        this.$data.devDeviceTypeToken = payload.deviceType.token
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

    // Called when device type choice is updated.
    onDeviceTypeUpdated: function (deviceType) {
      if (deviceType) {
        this.$data.devDeviceTypeToken = deviceType.token
      } else {
        this.$data.devDeviceTypeToken = null
      }
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
