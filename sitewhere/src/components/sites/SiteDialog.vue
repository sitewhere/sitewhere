<template>
  <div>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
      <v-tabs light v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider></v-tabs-slider>
          <v-tabs-item key="details" href="#details">
            Site Details
          </v-tabs-item>
          <v-tabs-item key="map" href="#map">
            Map Information
          </v-tabs-item>
          <v-tabs-item key="metadata" href="#metadata">
            Metadata
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="details" id="details">
          <v-card flat>
            <v-card-row>
              <v-card-text>
                <v-container fluid>
                  <v-layout row wrap>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Site name" v-model="siteName" prepend-icon="info"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" multi-line label="Description" v-model="siteDescription" prepend-icon="subject"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Image URL" v-model="siteImageUrl" prepend-icon="image"></v-text-field>
                    </v-flex>
                  </v-layout>
                </v-container>
              </v-card-text>
            </v-card-row>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="map" id="map">
          <map-panel :json="mapConfig" @mapConfig="onMapConfigUpdated"/>
        </v-tabs-content>
        <v-tabs-content key="metadata" id="metadata">
          <metadata-panel :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
        </v-tabs-content>
      </v-tabs>
    </base-dialog>
    <slot name="activator"></slot>
  </div>
</template>

<script>
import BaseDialog from '../common/BaseDialog'
import MapPanel from './MapPanel'
import MetadataPanel from '../common/MetadataPanel'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    siteName: '',
    siteDescription: '',
    siteImageUrl: '',
    mapConfig: {},
    metadata: []
  }),

  components: {
    BaseDialog,
    MapPanel,
    MetadataPanel
  },

  props: ['title', 'width', 'open', 'close', 'resetOnOpen', 'createLabel', 'cancelLabel'],

  watch: {
    open: function (val) {
      if (val) {
        this.onOpenDialog()
      }
    },
    close: function (val) {
      if (val) {
        this.onCloseDialog()
      }
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.name = this.$data.siteName
      payload.description = this.$data.siteDescription
      payload.imageUrl = this.$data.siteImageUrl
      payload.map = this.$data.mapConfig

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
      this.$data.siteName = null
      this.$data.siteDescription = null
      this.$data.siteImageUrl = null
      this.$data.mapConfig = {}
      this.$data.metadata = []
      this.$data.active = 'details'
    },

    // Called to open the dialog.
    onOpenDialog: function () {
      this.$data.dialogVisible = true
      if (this.$data.resetOnOpen) {
        this.reset()
      }
    },

    // Called to open the dialog.
    onCloseDialog: function () {
      this.$data.dialogVisible = false
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
      console.log(updated)
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
