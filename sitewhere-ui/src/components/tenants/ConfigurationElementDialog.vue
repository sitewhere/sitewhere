<template>
  <base-dialog :title="title" width="600" :visible="dialogVisible"
    createLabel="Create" cancelLabel="Cancel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs dark v-model="active">
      <v-tabs-bar slot="activators">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="configuration" href="#configuration">
          Configuration
        </v-tabs-item>
      </v-tabs-bar>
      <slot name="tabcontent"></slot>
      <v-tabs-content key="configuration" id="configuration">
        <v-card flat>
          <v-card-text>
            <v-container fluid>
              <v-layout row wrap>
                <v-flex xs12 v-for="attribute in attributes"
                  :key="attribute.name">
                  <v-icon fa>{{ attribute.icon }}</v-icon>
                  <v-text-field v-if="attribute.type === 'String'"
                    :required="attribute.required"
                    class="mt-1" :label="attribute.name"
                    v-model="values[attribute.localName]" hide-details>
                  </v-text-field>
                  <v-text-field v-if="attribute.type === 'Integer'"
                    :required="attribute.required" type="number"
                    class="mt-1" :label="attribute.name"
                    v-model="values[attribute.localName]" hide-details>
                  </v-text-field>
                  <v-checkbox v-if="attribute.type === 'Boolean'"
                    :label="attribute.name"
                    v-model="values[attribute.localName]">
                  </v-checkbox>
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
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    attributes: null,
    values: {},
    error: null
  }),

  components: {
    BaseDialog
  },

  props: ['model', 'config', 'title'],

  watch: {
    model: function (model) {
      if (model) {
        this.$data.attributes = model.attributes
      }
    },
    config: function (config) {
      if (config) {
        this.$data.values = Utils.arrayToMetadata(config.attributes)
      }
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      let payload = {}
      payload.name = this.model.localName
      payload.attributes = Utils.metadataToArray(this.$data.values)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.active = 'configuration'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()
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
