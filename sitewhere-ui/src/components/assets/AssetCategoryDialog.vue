<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs dark v-model="active">
      <v-tabs-bar slot="activators">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="details" href="#details">
          Category Details
        </v-tabs-item>
        <slot name="tabitem"></slot>
      </v-tabs-bar>
      <slot name="tabcontent"></slot>
      <v-tabs-content key="details" id="details">
        <v-card flat>
          <v-card-text>
            <v-container fluid>
              <v-layout row wrap>
                <v-flex xs12>
                  <v-text-field required class="mt-1" label="Category id"
                    v-model="categoryId" prepend-icon="info"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-text-field required class="mt-1" label="Category name"
                    v-model="categoryName" prepend-icon="info"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-select required :items="assetTypes"
                    v-model="categoryAssetType" label="Asset type"
                    prepend-icon="info">
                  </v-select>
                </v-flex>
              </v-layout>
            </v-container>
          </v-card-text>
        </v-card>
      </v-tabs-content>
    </v-tabs>
  </base-dialog>
</template>

<script>
import BaseDialog from '../common/BaseDialog'

export default {

  data: () => ({
    active: null,
    menu: null,
    dialogVisible: false,
    categoryId: null,
    categoryName: null,
    categoryAssetType: null,
    assetTypes: [
      {
        'text': 'Device',
        'value': 'Device'
      }, {
        'text': 'Hardware',
        'value': 'Hardware'
      }, {
        'text': 'Person',
        'value': 'Person'
      }, {
        'text': 'Location',
        'value': 'Location'
      }
    ],
    error: null
  }),

  components: {
    BaseDialog
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.id = this.$data.categoryId
      payload.name = this.$data.categoryName
      payload.assetType = this.$data.categoryAssetType
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.categoryId = null
      this.$data.categoryName = null
      this.$data.categoryAssetType = null
      this.$data.active = 'details'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.categoryId = payload.id
        this.$data.categoryName = payload.name
        this.$data.categoryAssetType = payload.assetType
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
    }
  }
}
</script>

<style scoped>
</style>
