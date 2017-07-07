<template>
  <div>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked"
      hideButtons="true">
      <v-stepper v-model="step">
        <v-stepper-header>
          <v-stepper-step step="1" :complete="step > 1">Specification</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step step="2" :complete="step > 2">Asset</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step step="3">Metadata</v-stepper-step>
        </v-stepper-header>
        <v-stepper-content step="1">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12>
                    <v-text-field required class="mt-1" label="Specification name"
                      v-model="specName" prepend-icon="info"></v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-select required :items="containerPolicies" v-model="specContainerPolicy"
                      label="Container policy" prepend-icon="developer_board"></v-select>
                  </v-flex>
                  <v-flex xs12>
                    <v-select required :items="assetProviders" v-model="specAssetProvider"
                      label="Asset provider" prepend-icon="local_offer"></v-select>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
          </v-card>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn flat>Cancel</v-btn>
            <v-btn primary @click.native="step = 2">Continue</v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="2">
          <v-card class="grey lighten-1 z-depth-1 mb-5" height="200px"></v-card>
          <v-card-actions>
            <v-btn primary @click.native="step = 1">Back</v-btn>
            <v-spacer></v-spacer>
            <v-btn flat>Cancel</v-btn>
            <v-btn primary @click.native="step = 3">Continue</v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="3">
          <metadata-panel :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn primary @click.native="step = 3">Create Specification</v-btn>
              <v-btn flat>Cancel</v-btn>
            </v-card-actions>
        </v-stepper-content>
      </v-stepper>
    </base-dialog>
  </div>
</template>

<script>
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    specName: null,
    specContainerPolicy: null,
    metadata: [],
    containerPolicies: [
      {
        'text': 'Standalone Device',
        'value': 'Standalone'
      }, {
        'text': 'Composite Device',
        'value': 'Composite'
      }
    ],
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
      payload.name = this.$data.specName
      payload.containerPolicy = this.$data.specContainerPolicy

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
      this.$data.specName = null
      this.$data.specContainerPolicy = null
      this.$data.metadata = []
      this.$data.active = 'details'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.specName = payload.name
        this.$data.specContainerPolicy = payload.containerPolicy

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
