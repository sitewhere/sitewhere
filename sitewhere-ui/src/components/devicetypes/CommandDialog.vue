<template>
  <div>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked"
      hideButtons="true">
      <v-stepper v-model="step">
        <v-stepper-header>
          <v-stepper-step step="1" :complete="step > 1">Command</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step step="2" :complete="step > 2">Parameters<small>Optional</small></v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step step="3">Metadata<small>Optional</small></v-stepper-step>
        </v-stepper-header>
        <v-stepper-content step="1">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12 v-if="cmdToken">
                    <div class="mb-4">
                      <v-icon class="mr-2">label</v-icon>
                      <span class="subheading">
                        Token: {{ cmdToken }}
                        <v-tooltip left>
                          <v-btn style="position: relative;"
                            v-clipboard="copyData" :key="cmdToken"
                            class="mt-0" light icon @success="onTokenCopied"
                            @error="onTokenCopyFailed" slot="activator">
                            <v-icon>fa-clipboard</v-icon>
                          </v-btn>
                          <span>Copy to Clipboard</span>
                        </v-tooltip>
                      </span>
                    </div>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field required class="mt-1" label="Command name"
                      v-model="cmdName" prepend-icon="info"></v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field required class="mt-1" label="Namespace"
                      v-model="cmdNamespace" prepend-icon="info"></v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field class="mt-1" multi-line label="Description"
                    v-model="cmdDescription" prepend-icon="subject"></v-text-field>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
          </v-card>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn flat @click="onCancelClicked">{{ cancelLabel }}</v-btn>
            <v-btn color="primary" flat :disabled="!firstPageComplete"
              @click="onCreateClicked">{{ createLabel }}</v-btn>
            <v-btn color="primary" :disabled="!firstPageComplete" flat
              @click="step = 2">Add Parameters
              <v-icon light>keyboard_arrow_right</v-icon>
            </v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="2">
          <parameters-panel :parameters="cmdParameters"
            @parameterAdded="onParameterAdded"
            @parameterDeleted="onParameterDeleted">
          </parameters-panel>
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
              <v-btn color="primary" flat @click="step = 2">
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
      <v-snackbar :timeout="2000" success v-model="showTokenCopied">Token copied to clipboard
        <v-btn dark flat @click="showTokenCopied = false">Close</v-btn>
      </v-snackbar>
    </base-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'
import ParametersPanel from './ParametersPanel'

export default {

  data: () => ({
    copyData: null,
    showTokenCopied: false,
    step: null,
    dialogVisible: false,
    cmdToken: null,
    cmdName: null,
    cmdNamespace: null,
    cmdDescription: null,
    cmdParameters: [],
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel,
    ParametersPanel
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  computed: {
    // Indicates if first page fields are filled in.
    firstPageComplete: function () {
      return (!this.isBlank(this.$data.cmdName) &&
        !this.isBlank(this.$data.cmdNamespace) &&
        !this.isBlank(this.$data.cmdDescription))
    },

    // Indicates if second page fields are filled in.
    secondPageComplete: function () {
      return this.firstPageComplete
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.name = this.$data.cmdName
      payload.namespace = this.$data.cmdNamespace
      payload.description = this.$data.cmdDescription
      payload.parameters = this.$data.cmdParameters
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function () {
      this.$data.cmdToken = null
      this.$data.cmdName = null
      this.$data.cmdNamespace = null
      this.$data.cmdDescription = null
      this.$data.metadata = []
      this.$data.step = 1
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.cmdToken = payload.token
        this.$data.cmdName = payload.name
        this.$data.cmdNamespace = payload.namespace
        this.$data.cmdDescription = payload.description
        this.$data.cmdParameters = payload.parameters
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

    // Called when a parameter is added.
    onParameterAdded: function (param) {
      var params = this.$data.cmdParameters
      params.push(param)
    },

    // Called when a parameter is deleted.
    onParameterDeleted: function (name) {
      var params = this.$data.cmdParameters
      for (var i = 0; i < params.length; i++) {
        if (params[i].name === name) {
          params.splice(i, 1)
        }
      }
    },

    // Called when a metadata entry has been added.
    onMetadataAdded: function (entry) {
      var metadata = this.$data.metadata
      metadata.push(entry)
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

    // Called after token is copied.
    onTokenCopied: function (e) {
      console.log('Token copied.')
      this.$data.showTokenCopied = true
    },

    // Called if unable to copy token.
    onTokenCopyFailed: function (e) {
      console.log('Token copy failed.')
    },

    // Tests whether a string is blank.
    isBlank: function (str) {
      return (!str || /^\s*$/.test(str))
    }
  }
}
</script>

<style scoped>
</style>
