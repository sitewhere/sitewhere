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
            <v-btn flat @click.native="onCancelClicked">{{ cancelLabel }}</v-btn>
            <v-btn flat primary :disabled="!firstPageComplete"
              @click.native="onCreateClicked">{{ createLabel }}</v-btn>
            <v-btn :disabled="!firstPageComplete" flat primary
              @click.native="step = 2">Add Parameters
              <v-icon light primary>keyboard_arrow_right</v-icon>
            </v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="2">
          <parameters-panel :parameters="cmdParameters"
            @parameterAdded="onParameterAdded"
            @parameterDeleted="onParameterDeleted">
          </parameters-panel>
          <v-card-actions>
            <v-btn flat primary @click.native="step = 1">
              <v-icon light primary>keyboard_arrow_left</v-icon>
              Back
            </v-btn>
            <v-spacer></v-spacer>
            <v-btn flat @click.native="onCancelClicked">{{ cancelLabel }}</v-btn>
            <v-btn flat primary :disabled="!secondPageComplete"
              @click.native="onCreateClicked">{{ createLabel }}</v-btn>
            <v-btn flat primary :disabled="!secondPageComplete"
              @click.native="step = 3">Add Metadata
              <v-icon light primary>keyboard_arrow_right</v-icon>
            </v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="3">
          <metadata-panel class="mb-3" :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
            <v-card-actions>
              <v-btn flat primary @click.native="step = 2">
                <v-icon light primary>keyboard_arrow_left</v-icon>
                Back
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn flat @click.native="onCancelClicked">{{ cancelLabel }}</v-btn>
              <v-btn flat primary :disabled="!secondPageComplete"
                @click.native="onCreateClicked">{{ createLabel }}</v-btn>
            </v-card-actions>
        </v-stepper-content>
      </v-stepper>
    </base-dialog>
  </div>
</template>

<script>
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'
import ParametersPanel from './ParametersPanel'

export default {

  data: () => ({
    step: null,
    dialogVisible: false,
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

  props: ['title', 'width', 'createLabel', 'cancelLabel', 'specification'],

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

      var metadata = {}
      var flat = this.$data.metadata
      for (var i = 0; i < flat.length; i++) {
        metadata[flat[i].name] = flat[i].value
      }
      payload.metadata = metadata
      return payload
    },

    // Reset dialog contents.
    reset: function () {
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
        this.$data.cmdName = payload.name
        this.$data.cmdNamespace = payload.namespace
        this.$data.cmdDescription = payload.description

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

    // Tests whether a string is blank.
    isBlank: function (str) {
      return (!str || /^\s*$/.test(str))
    }
  }
}
</script>

<style scoped>
</style>
