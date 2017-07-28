<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs dark v-model="active">
      <v-tabs-bar slot="activators">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="details" href="#details">
          Asset Details
        </v-tabs-item>
        <slot name="tabitem"></slot>
        <v-tabs-item key="properties" href="#properties">
          Properties
        </v-tabs-item>
      </v-tabs-bar>
      <slot name="tabcontent"></slot>
      <v-tabs-content key="details" id="details">
        <v-card flat>
          <v-card-text>
            <v-container fluid>
              <v-layout row wrap>
                <v-flex xs12>
                  <v-text-field required class="mt-1" label="Asset id"
                    v-model="assetId" prepend-icon="info"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-text-field required class="mt-1" label="Asset name"
                    v-model="assetName" prepend-icon="info"></v-text-field>
                </v-flex>
                <v-flex xs12 v-if="isPerson">
                  <v-text-field required class="mt-1" label="Username"
                    v-model="assetUsername" prepend-icon="info">
                  </v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-text-field required class="mt-1" label="Image URL"
                    v-model="assetImageUrl" prepend-icon="insert_photo">
                  </v-text-field>
                </v-flex>
                <v-flex xs12 v-if="isHardwareOrDevice">
                  <v-text-field required class="mt-1" label="SKU"
                    v-model="assetSku" prepend-icon="info">
                  </v-text-field>
                </v-flex>
                <v-flex xs12 v-if="isHardwareOrDevice">
                  <v-text-field class="mt-1" multi-line label="Description"
                    v-model="assetDescription" prepend-icon="subject">
                  </v-text-field>
                </v-flex>
                <v-flex xs12 v-if="isPerson">
                  <v-text-field required class="mt-1" label="Email address"
                    v-model="assetEmail" prepend-icon="info">
                  </v-text-field>
                </v-flex>
                <v-flex xs12 v-if="isLocation">
                  <v-text-field type="number" required class="mt-1"
                    label="Latitude" v-model="assetLatitude"
                    prepend-icon="language">
                  </v-text-field>
                </v-flex>
                <v-flex xs12 v-if="isLocation">
                  <v-text-field type="number" required class="mt-1"
                    label="Longitude" v-model="assetLongitude"
                    prepend-icon="language">
                  </v-text-field>
                </v-flex>
                <v-flex xs12 v-if="isLocation">
                  <v-text-field type="number" required class="mt-1"
                    label="Elevation" v-model="assetElevation"
                    prepend-icon="flight_takeoff">
                  </v-text-field>
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>
        </v-card>
      </v-tabs-content>
      <v-tabs-content key="properties" id="properties">
        <metadata-panel :metadata="metadata"
          noDataMessage="No properties listed for asset"
          @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
      </v-tabs-content>
    </v-tabs>
  </base-dialog>
</template>

<script>
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'

export default {

  data: () => ({
    active: null,
    menu: null,
    dialogVisible: false,
    assetId: null,
    assetName: null,
    assetImageUrl: null,
    assetSku: null,
    assetDescription: null,
    assetUsername: null,
    assetEmail: null,
    assetLatitude: null,
    assetLongitude: null,
    assetElevation: null,
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel
  },

  props: ['category', 'title', 'width', 'createLabel', 'cancelLabel'],

  computed: {
    isHardwareOrDevice: function () {
      return (this.category) && ((this.category.assetType === 'Device') ||
        (this.category.assetType === 'Hardware'))
    },
    isPerson: function () {
      return (this.category) && (this.category.assetType === 'Person')
    },
    isLocation: function () {
      return (this.category) && (this.category.assetType === 'Location')
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.id = this.$data.assetId
      payload.name = this.$data.assetName
      payload.imageUrl = this.$data.assetImageUrl
      if (this.isHardwareOrDevice) {
        payload.sku = this.$data.assetSku
        payload.description = this.$data.assetDescription
      } else if (this.isPerson) {
        payload.userName = this.$data.assetUsername
        payload.emailAddress = this.$data.assetEmail
      } else if (this.isLocation) {
        payload.latitude = this.$data.assetLatitude
        payload.longitude = this.$data.assetLongitude
        payload.elevation = this.$data.assetElevation
      }
      payload.properties = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.assetId = null
      this.$data.assetName = null
      this.$data.assetImageUrl = null
      this.$data.assetSku = null
      this.$data.assetDescription = null
      this.$data.assetUsername = null
      this.$data.assetEmail = null
      this.$data.assetLatitude = null
      this.$data.assetLongitude = null
      this.$data.assetElevation = null
      this.$data.metadata = []
      this.$data.active = 'details'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.assetId = payload.id
        this.$data.assetName = payload.name
        this.$data.assetImageUrl = payload.imageUrl
        if (this.isHardwareOrDevice) {
          this.$data.assetSku = payload.sku
          this.$data.assetDescription = payload.description
        } else if (this.isPerson) {
          this.$data.assetUsername = payload.userName
          this.$data.assetEmail = payload.emailAddress
        } else if (this.isLocation) {
          this.$data.assetLatitude = payload.latitude
          this.$data.assetLongitude = payload.longitude
          this.$data.assetElevation = payload.elevation
        }
        this.$data.metadata = Utils.metadataToArray(payload.properties)
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
