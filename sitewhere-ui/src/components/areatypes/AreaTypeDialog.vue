<template>
  <div>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
      <v-tabs v-model="active">
        <v-tabs-bar dark color="primary">
          <v-tabs-item key="details" href="#details">
            Area Type Details
          </v-tabs-item>
          <v-tabs-item key="catypes" href="#catypes">
            Contained Area Types
          </v-tabs-item>
          <v-tabs-item key="metadata" href="#metadata">
            Metadata
          </v-tabs-item>
          <v-tabs-slider></v-tabs-slider>
        </v-tabs-bar>
        <v-tabs-items>
          <v-tabs-content key="details" id="details">
            <v-card flat>
              <v-card-text>
                <v-container fluid>
                  <v-layout row wrap>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Name" v-model="typeName"
                        prepend-icon="info"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" multi-line label="Description"
                        v-model="typeDescription" prepend-icon="subject"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <icon-selector v-model="typeIcon"></icon-selector>
                    </v-flex>
                  </v-layout>
                </v-container>
                </v-card-text>
            </v-card>
          </v-tabs-content>
          <v-tabs-content key="catypes" id="catypes">
            <area-types-multiselect :areaTypes="areaTypes"
              :selectedAreaTypeIds="typeContainedAreaTypeIds"
              @selectedAreaTypesUpdated="onContainedAreaTypesUpdated">
          </area-types-multiselect>
          </v-tabs-content>
          <v-tabs-content key="metadata" id="metadata">
            <metadata-panel :metadata="metadata"
              @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
          </v-tabs-content>
        </v-tabs-items>
      </v-tabs>
    </base-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import IconSelector from '../common/IconSelector'
import AreaTypesMultiselect from './AreaTypesMultiselect'
import MetadataPanel from '../common/MetadataPanel'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    typeName: '',
    typeDescription: '',
    typeIcon: '',
    typeContainedAreaTypeIds: [],
    typeContainedAreaTypeTokens: [],
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    IconSelector,
    AreaTypesMultiselect,
    MetadataPanel
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel', 'areaTypes'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.name = this.$data.typeName
      payload.description = this.$data.typeDescription
      payload.icon = this.$data.typeIcon
      payload.containedAreaTypeTokens = this.$data.typeContainedAreaTypeTokens
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.typeName = null
      this.$data.typeDescription = null
      this.$data.typeIcon = null
      this.$data.typeContainedAreaTypeIds = []
      this.$data.typeContainedAreaTypeTokens = []
      this.$data.metadata = []
      this.$data.active = 'details'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()
      if (payload) {
        this.$data.typeName = payload.name
        this.$data.typeDescription = payload.description
        this.$data.typeIcon = payload.icon
        this.$data.typeContainedAreaTypeIds = payload.containedAreaTypeIds
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

    // Called when list of contained area types is updated.
    onContainedAreaTypesUpdated: function (selected) {
      let tokens = []
      for (let i = 0; i < selected.length; i++) {
        let at = selected[i]
        tokens.push(at.token)
      }
      this.$data.typeContainedAreaTypeTokens = tokens
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

    // Called when map configuration is updated.
    onMapConfigUpdated: function (updated) {
      this.$data.mapConfig = updated
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
