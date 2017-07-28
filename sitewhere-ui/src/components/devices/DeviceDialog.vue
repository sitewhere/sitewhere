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
          <v-stepper-step step="2" :complete="step > 2">Site</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step step="3" :complete="step > 3">Specification</v-stepper-step>
          <v-divider></v-divider>
          <v-stepper-step step="4">Metadata<small>Optional</small></v-stepper-step>
        </v-stepper-header>
        <v-stepper-content step="1">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12>
                    <v-text-field required class="mt-1" label="Hardware id"
                      v-model="devHardwareId" prepend-icon="info"></v-text-field>
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
            <v-btn :disabled="!firstPageComplete" flat primary
              @click.native="step = 2">Assign Site
              <v-icon light primary>keyboard_arrow_right</v-icon>
            </v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="2">
          <site-chooser
            chosenText="Device will be associated with the site below."
            notChosenText="Choose which site the device will be associated with:"
            :selectedToken="devSiteToken"
            @siteUpdated="onSiteUpdated">
          </site-chooser>
          <v-card-actions>
            <v-btn flat primary @click.native="step = 1">
              <v-icon light primary>keyboard_arrow_left</v-icon>
              Back
            </v-btn>
            <v-spacer></v-spacer>
            <v-btn flat @click.native="onCancelClicked">{{ cancelLabel }}</v-btn>
            <v-btn flat primary :disabled="!secondPageComplete"
              @click.native="step = 3">Assign Specification
              <v-icon light primary>keyboard_arrow_right</v-icon>
            </v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="3">
          <specification-chooser
            chosenText="Device will implement the specification below."
            notChosenText="Choose a specification that will be implemented by the device:"
            :selectedToken="devSpecificationToken"
            @specificationUpdated="onSpecificationUpdated">
          </specification-chooser>
          <v-card-actions>
            <v-btn flat primary @click.native="step = 2">
              <v-icon light primary>keyboard_arrow_left</v-icon>
              Back
            </v-btn>
            <v-spacer></v-spacer>
            <v-btn flat @click.native="onCancelClicked">{{ cancelLabel }}</v-btn>
            <v-btn flat primary :disabled="!thirdPageComplete"
              @click.native="onCreateClicked">{{ createLabel }}</v-btn>
            <v-btn flat primary :disabled="!thirdPageComplete"
              @click.native="step = 4">Add Metadata
              <v-icon light primary>keyboard_arrow_right</v-icon>
            </v-btn>
          </v-card-actions>
        </v-stepper-content>
        <v-stepper-content step="4">
          <metadata-panel class="mb-3" :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
            <v-card-actions>
              <v-btn flat primary @click.native="step = 3">
                <v-icon light primary>keyboard_arrow_left</v-icon>
                Back
              </v-btn>
              <v-spacer></v-spacer>
              <v-btn flat @click.native="onCancelClicked">{{ cancelLabel }}</v-btn>
              <v-btn flat primary :disabled="!thirdPageComplete"
                @click.native="onCreateClicked">{{ createLabel }}</v-btn>
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
import SiteChooser from '../sites/SiteChooser'
import SpecificationChooser from '../specifications/SpecificationChooser'
import {_getAssetModules} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    step: null,
    dialogVisible: false,
    devHardwareId: null,
    devComments: null,
    devSiteToken: null,
    devSpecificationToken: null,
    metadata: [],
    assetModules: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel,
    SiteChooser,
    SpecificationChooser
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  computed: {
    // Indicates if first page fields are filled in.
    firstPageComplete: function () {
      return !Utils.isBlank(this.$data.devHardwareId)
    },

    // Indicates if second page is complete.
    secondPageComplete: function () {
      return this.firstPageComplete && (this.$data.devSiteToken != null)
    },

    // Indicates if third page is complete.
    thirdPageComplete: function () {
      return this.firstPageComplete && this.secondPageComplete &&
        (this.$data.devSpecificationToken != null)
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.hardwareId = this.$data.devHardwareId
      payload.comments = this.$data.devComments
      payload.siteToken = this.$data.devSiteToken
      payload.specificationToken = this.$data.devSpecificationToken
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function () {
      this.$data.devHardwareId = null
      this.$data.devComments = null
      this.$data.devSiteToken = null
      this.$data.devSpecificationToken = null
      this.$data.metadata = []
      this.$data.step = 1
      this.$data.error = null

      var component = this
      _getAssetModules(this.$store, 'Device')
        .then(function (response) {
          component.assetModules = response.data
          this.onAssetModulesLoaded()
        }).catch(function (e) {
        })
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.devHardwareId = payload.hardwareId
        this.$data.devComments = payload.comments
        this.$data.devSiteToken = payload.siteToken
        this.$data.devSpecificationToken = payload.specificationToken
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

    // Called when site choice is updated.
    onSiteUpdated: function (site) {
      if (site) {
        this.$data.devSiteToken = site.token
      } else {
        this.$data.devSiteToken = null
      }
    },

    // Called when specification choice is updated.
    onSpecificationUpdated: function (specification) {
      if (specification) {
        this.$data.devSpecificationToken = specification.token
      } else {
        this.$data.devSpecificationToken = null
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
