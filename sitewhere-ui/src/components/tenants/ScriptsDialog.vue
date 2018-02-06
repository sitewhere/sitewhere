<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs v-model="active">
      <v-tabs-bar dark color="primary">
        <v-tabs-item key="details" href="#details">
          Script Details
        </v-tabs-item>
        <v-tabs-item key="content" href="#content">
          Script Content
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
                    <v-text-field label="Id" v-model="scriptId">
                    </v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field label="Name" v-model="scriptName">
                    </v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field multi-line label="Description"
                      v-model="scriptDescription">
                    </v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-select :items="scriptTypes" v-model="scriptType"
                      label="Script Type" light single-line auto
                      hide-details>
                    </v-select>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="content" id="content">
          <v-card>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12>
                    <v-text-field textarea multi-line :rows="15"
                      label="Script Content" v-model="scriptContent">
                    </v-text-field>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
          </v-card>
        </v-tabs-content>
      </v-tabs-items>
    </v-tabs>
  </base-dialog>
</template>

<script>
import BaseDialog from '../common/BaseDialog'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    scriptId: '',
    scriptName: '',
    scriptDescription: '',
    scriptType: {},
    scriptContent: '',
    scriptTypes: [
      {
        'text': 'Groovy',
        'value': 'groovy'
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
      payload.id = this.$data.scriptId
      payload.name = this.$data.scriptName
      payload.description = this.$data.scriptDescription
      payload.type = this.$data.scriptType
      payload.content = btoa(this.$data.scriptContent)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.scriptId = null
      this.$data.scriptName = null
      this.$data.scriptDescription = null
      this.$data.scriptType = null
      this.$data.scriptContent = '// Add script content here.'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.scriptId = payload.id
        this.$data.scriptName = payload.name
        this.$data.scriptDescription = payload.description
        this.$data.scriptType = payload.type
        this.$data.scriptContent = atob(payload.content)
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
