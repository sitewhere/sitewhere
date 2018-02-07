<template>
  <span>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
      <v-tabs dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider></v-tabs-slider>
          <v-tabs-item key="settings" href="#settings">
            Device Slot Settings
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="settings" id="settings">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12>
                    <v-text-field class="mt-1" label="Slot name"
                      v-model="slotName" prepend-icon="info"></v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field class="mt-1" label="Relative path"
                      v-model="slotPath" prepend-icon="folder"></v-text-field>
                  </v-flex>
                </v-layout>
              </v-container>
              </v-card-text>
          </v-card>
        </v-tabs-content>
      </v-tabs>
    </base-dialog>
  </span>
</template>

<script>
import BaseDialog from '../common/BaseDialog'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    slotName: null,
    slotPath: null,
    error: null
  }),

  components: {
    BaseDialog
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var slot = {}
      slot.name = this.$data.slotName
      slot.path = this.$data.slotPath
      return slot
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.slotName = null
      this.$data.slotPath = null
      this.$data.active = 'settings'
      this.$data.error = null
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.slotName = payload.name
        this.$data.slotPath = payload.path
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
