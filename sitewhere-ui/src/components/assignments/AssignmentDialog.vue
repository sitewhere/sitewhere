<template>
  <div>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked"
      hideButtons="true">
      <v-stepper v-model="step">
        <v-stepper-header>
          <v-stepper-step step="1" :complete="step > 1">Asset Association</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step step="2">Metadata<small>Optional</small></v-stepper-step>
        </v-stepper-header>
        <v-stepper-content step="1">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs6>
                    <v-checkbox label="Associate device with an asset?"
                      v-model="assnAssociateAsset" light>
                    </v-checkbox>
                  </v-flex>
                  <v-flex xs6 v-if="assnAssociateAsset">
                    <v-select required :items="assetModules"
                      v-model="assnAssetModule"
                      item-text="name" item-value="id" label="Asset module"
                      prepend-icon="local_offer"></v-select>
                  </v-flex>
                  <v-flex xs12>
                    <asset-chooser :assetModuleId="assnAssetModule"
                      :assetId="assnAssetId" @assetUpdated="onAssetUpdated">
                    </asset-chooser>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
          </v-card>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn flat @click.native="onCancelClicked">{{ cancelLabel }}</v-btn>
            <v-btn :disabled="!firstPageComplete" flat @click="onCreateClicked">
              {{ createLabel }}
            </v-btn>
            <v-btn :disabled="!firstPageComplete" flat @click="step = 2">Add Metadata
              <v-icon light>keyboard_arrow_right</v-icon>
            </v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="2">
          <metadata-panel class="mb-3" :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
            <v-card-actions>
              <v-btn flat @click="step = 1">
                <v-icon light>keyboard_arrow_left</v-icon>
                Back
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn flat @click="onCancelClicked">{{ cancelLabel }}</v-btn>
              <v-btn flat color="primary" @click="onCreateClicked">
                {{ createLabel }}
              </v-btn>
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
import AssetChooser from '../common/AssetChooser'
import {_getAssetModules} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    step: null,
    dialogVisible: false,
    assnAssociateAsset: false,
    assnAssetModule: null,
    assnAssetId: null,
    metadata: [],
    assetModules: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel,
    AssetChooser
  },

  props: ['hardwareId', 'title', 'width', 'createLabel', 'cancelLabel'],

  computed: {
    // Indicates if first page fields are filled in.
    firstPageComplete: function () {
      return this.$data.assnAssociateAsset
        ? (this.$data.assnAssetModule && this.$data.assnAssetId) : true
    }
  },

  watch: {
    assnAssociateAsset: function (value) {
      if (!value) {
        this.$data.assnAssetModule = null
        this.$data.assnAssetId = null
      }
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      console.log(this.hardwareId)
      var payload = {}
      payload.deviceHardwareId = this.hardwareId
      payload.assignmentType = this.$data.assnAssociateAsset
        ? 'Associated' : 'Unassociated'

      var assetReference = {}
      assetReference.module = this.$data.assnAssetModule
      assetReference.id = this.$data.assnAssetId
      payload.assetReference = assetReference

      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function () {
      this.$data.assnAssociateAsset = false
      this.$data.assnAssetModule = null
      this.$data.assnAssetId = null
      this.$data.metadata = []
      this.$data.step = 1
      this.$data.error = null

      var component = this
      _getAssetModules(this.$store)
        .then(function (response) {
          component.assetModules = response.data
        }).catch(function (e) {
        })
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

    // Called when asset is updated.
    onAssetUpdated: function (asset) {
      if (asset) {
        this.$data.assnAssetId = asset.id
      } else {
        this.$data.assnAssetId = null
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
