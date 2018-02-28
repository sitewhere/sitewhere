<template>
  <div>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
      <v-tabs v-model="active">
        <v-tabs-bar dark color="primary">
          <v-tabs-item key="details" href="#details">
            Asset Details
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
                      <v-text-field class="mt-1" label="Name"
                        v-model="assetName" prepend-icon="info">
                      </v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Image URL"
                        v-model="assetImageUrl" prepend-icon="image">
                      </v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <asset-type-chooser :selectedToken="assetTypeToken"
                        chosenText="Asset is based on the type below:"
                        notChosenText="Choose an asset type from the list below:"
                        @assetTypeUpdated="onAssetTypeUpdated">
                      </asset-type-chooser>
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
        </v-tabs-items>
      </v-tabs>
    </base-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import IconSelector from '../common/IconSelector'
import MetadataPanel from '../common/MetadataPanel'
import AssetTypeChooser from '../assettypes/AssetTypeChooser'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    assetName: null,
    assetImageUrl: null,
    assetTypeToken: null,
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    IconSelector,
    MetadataPanel,
    AssetTypeChooser
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  methods: {
    // Called when asset type is updated.
    onAssetTypeUpdated: function (assetType) {
      this.$data.assetTypeToken = assetType ? assetType.token : null
    },
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.name = this.$data.assetName
      payload.imageUrl = this.$data.assetImageUrl
      payload.assetTypeToken = this.$data.assetTypeToken
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },
    // Reset dialog contents.
    reset: function (e) {
      this.$data.assetName = null
      this.$data.assetImageUrl = null
      this.$data.assetTypeToken = null
      this.$data.metadata = []
      this.$data.active = 'details'
    },
    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()
      if (payload) {
        this.$data.assetName = payload.name
        this.$data.assetImageUrl = payload.imageUrl
        this.$data.assetTypeToken = payload.assetType.token
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
