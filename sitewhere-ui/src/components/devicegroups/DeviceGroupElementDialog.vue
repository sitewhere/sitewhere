<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs v-model="active">
      <v-tabs-bar dark color="primary">
        <v-tabs-item key="details" href="#details">
          Element details
        </v-tabs-item>
        <v-tabs-item key="roles" href="#roles">
          Roles
        </v-tabs-item>
        <v-tabs-slider></v-tabs-slider>
      </v-tabs-bar>
      <v-tabs-items>
        <v-tabs-content key="details" id="details">
          <v-card flat>
            <v-card-text>
              <v-select required :items="elementTypes" v-model="elementType"
                item-text="text" item-value="value" label="Element type"
                prepend-icon="info"></v-select>
              <device-chooser v-if="elementType === 'device'"
                v-model="deviceToken"
                chosenText="Element points to device below:"
                notChosenText="Choose a device from the list below:">
              </device-chooser>
              <device-group-chooser v-if="elementType === 'group'"
                v-model="groupToken"
                chosenText="Element points to group below:"
                notChosenText="Choose a group from the list below:">
              </device-group-chooser>
            </v-card-text>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="roles" id="roles">
          <v-card flat>
            <v-card-text>
              <roles-field :roles="elementRoles"
                @onRolesUpdated="onRolesUpdated">
              </roles-field>
            </v-card-text>
          </v-card>
        </v-tabs-content>
      </v-tabs-items>
    </v-tabs>
  </base-dialog>
</template>

<script>
import BaseDialog from '../common/BaseDialog'
import DeviceChooser from '../devices/DeviceChooser'
import DeviceGroupChooser from '../devicegroups/DeviceGroupChooser'
import RolesField from './RolesField'

export default {

  data: () => ({
    active: null,
    menu: null,
    dialogVisible: false,
    elementType: 'device',
    deviceToken: null,
    groupToken: null,
    elementRoles: [],
    elementTypes: [
      {
        'text': 'Device',
        'value': 'device'
      }, {
        'text': 'Nested Group',
        'value': 'group'
      }
    ],
    error: null
  }),

  components: {
    BaseDialog,
    DeviceChooser,
    DeviceGroupChooser,
    RolesField
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      let payload = []
      let element = {}
      if (this.$data.elementType === 'device') {
        element.deviceToken = this.$data.deviceToken
      } else if (this.$data.elementType === 'group') {
        element.nestedGroupToken = this.$data.groupToken
      }
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
