<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs dark v-model="active">
      <v-tabs-bar slot="activators">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="details" href="#details">
          Element Details
        </v-tabs-item>
      </v-tabs-bar>
      <slot name="tabcontent"></slot>
      <v-tabs-content key="details" id="details">
        <v-card flat>
          <v-card-text>
            <v-container fluid>
              <v-layout row wrap>
                <v-flex xs12>
                  <v-select required :items="elementTypes" v-model="elementType"
                    item-text="text" item-value="value" label="Element type"
                    prepend-icon="info"></v-select>
                </v-flex>
                <v-flex xs12>
                  <v-text-field class="mt-1" label="Element id"
                    v-model="elementId" prepend-icon="info">
                  </v-text-field>
                </v-flex>
                <v-flex xs12>
                  <roles-field :roles="elementRoles"
                    @onRolesUpdated="onRolesUpdated">
                  </roles-field>
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
import RolesField from './RolesField'

export default {

  data: () => ({
    active: null,
    menu: null,
    dialogVisible: false,
    elementType: null,
    elementId: null,
    elementRoles: [],
    elementTypes: [
      {
        'text': 'Device',
        'value': 'Device'
      }, {
        'text': 'Group',
        'value': 'Group'
      }
    ],
    error: null
  }),

  components: {
    BaseDialog,
    RolesField
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      let payload = []
      let element = {}
      element.type = this.$data.elementType
      element.elementId = this.$data.elementId
      element.roles = this.$data.elementRoles
      payload.push(element)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.elementType = null
      this.$data.elementId = null
      this.$data.elementRoles = []
      this.$data.active = 'details'
      this.$data.error = null
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.elementType = payload.type
        this.$data.elementId = payload.elementId
        this.$data.elementRoles = payload.roles
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

    // Called when roles are updated.
    onRolesUpdated: function (roles) {
      this.$data.groupRoles = roles
    }
  }
}
</script>

<style scoped>
</style>
