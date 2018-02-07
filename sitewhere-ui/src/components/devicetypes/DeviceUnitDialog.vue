<template>
  <span>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel"
      :error="error" :invalid="invalid" @createClicked="onCreateClicked"
      @cancelClicked="onCancelClicked">
      <v-tabs dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider></v-tabs-slider>
          <v-tabs-item key="settings" href="#settings">
            Device Unit Settings
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="settings" id="settings">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12>
                    <v-text-field required class="mt-1" label="Unit name"
                      v-model="unitName" prepend-icon="info">
                    </v-text-field>
                  </v-flex>
                  <v-flex xs12>
                    <v-text-field required class="mt-1" label="Relative path"
                      v-model="unitPath" prepend-icon="folder">
                    </v-text-field>
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
    unitName: null,
    unitPath: null,
    unitDeviceSlots: [],
    unitDeviceunits: [],
    error: null,
    validationRules: {
      unitName: 'required|alpha|min:3',
      email: 'required|alpha|min:3'
    }
  }),

  components: {
    BaseDialog
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  computed: {
    // Determines whether input is invalid.
    invalid: function () {
      return !(this.$data.unitName && this.$data.unitPath)
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var unit = {}
      unit.name = this.$data.unitName
      unit.path = this.$data.unitPath
      unit.deviceSlots = this.$data.unitDeviceSlots
      unit.deviceUnits = this.$data.unitDeviceunits
      return unit
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.unitName = null
      this.$data.unitPath = null
      this.$data.unitDeviceSlots = []
      this.$data.unitDeviceunits = []
      this.$data.active = 'settings'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.unitName = payload.name
        this.$data.unitPath = payload.path
        this.$data.unitDeviceSlots = payload.deviceSlots
        this.$data.unitDeviceunits = payload.deviceUnits
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
