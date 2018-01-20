<template>
  <div>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
      <v-card flat>
        <v-card-text>
          <v-container fluid>
            <v-layout row wrap>
              <v-flex xs12>
                <v-text-field label="Id"
                  v-model="scriptId" prepend-icon="fa-info">
                </v-text-field>
              </v-flex>
              <v-flex xs12>
                <v-text-field label="Name"
                  v-model="scriptName" prepend-icon="fa-info">
                </v-text-field>
              </v-flex>
              <v-flex xs12>
                <v-text-field multi-line label="Description"
                  v-model="scriptDescription" prepend-icon="fa-info">
                </v-text-field>
              </v-flex>
              <v-flex xs12>
                <v-select :items="scriptTypes" v-model="scriptType"
                  label="Script Type" light single-line auto
                  prepend-icon="fa-info" hide-details>
                </v-select>
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>
      </v-card>
    </base-dialog>
  </div>
</template>

<script>
import BaseDialog from '../common/BaseDialog'

export default {

  data: () => ({
    dialogVisible: false,
    scriptId: '',
    scriptName: '',
    scriptDescription: '',
    scriptType: {},
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
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.scriptId = null
      this.$data.scriptName = null
      this.$data.scriptDescription = null
      this.$data.scriptType = null
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.scriptId = payload.id
        this.$data.scriptName = payload.name
        this.$data.scriptDescription = payload.description
        this.$data.scriptType = payload.type
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
