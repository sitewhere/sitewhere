<template>
  <div>
    <base-dialog :title="title" :width="width" :visible="dialogVisible"
      :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
      <v-tabs dark v-model="active">
        <v-tabs-bar slot="activators">
          <v-tabs-slider></v-tabs-slider>
          <v-tabs-item key="details" href="#details">
            Command Details
          </v-tabs-item>
          <v-tabs-item key="schedule" href="#schedule">
            Schedule
          </v-tabs-item>
          <v-tabs-item key="metadata" href="#metadata">
            Metadata
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="details" id="details">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12>
                    <v-select :items="commands" v-model="commandSelection"
                      label="Command" item-text="name" item-value="token"
                      light single-line auto prepend-icon="flash_on"
                      hide-details></v-select>
                  </v-flex>
                  <v-card v-if="command" style="width: 100%;">
                    <v-card-text>
                      {{ command.description }}
                    </v-card-text>
                    <v-card-text class="pt-0" v-if="command.parameters.length">
                      <v-flex xs12 v-for="(param, index) in command.parameters" :key="param.name">
                        <v-text-field :label="param.name"
                          :required="param.required" v-model="parameters[param.name]">
                        </v-text-field>
                      </v-flex>
                    </v-card-text>
                  </v-card>
                </v-layout>
              </v-container>
            </v-card-text>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="schedule" id="schedule">
          <v-card flat>
            <v-card-text>
              <v-container fluid>
                <v-layout row wrap>
                  <v-flex xs12>
                    <v-switch class="mb-0" :label="scheduleMessage" v-model="useSchedule">
                    </v-switch>
                  </v-flex>
                  <v-flex xs12>
                    <v-select class="mt-0" :disabled="!useSchedule"
                      :style="{'opacity': useSchedule ? 1 : 0.3 }"
                      :items="schedules" v-model="scheduleSelection"
                      label="Schedule" item-text="name" item-value="token"
                      light single-line auto prepend-icon="flash_on"
                      hide-details></v-select>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card-text>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="metadata" id="metadata">
          <metadata-panel :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
        </v-tabs-content>
      </v-tabs>
    </base-dialog>
  </div>
</template>

<script>
import Lodash from 'lodash'
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'
import {
  _listDeviceCommands,
  _listSchedules
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    commands: [],
    commandSelection: null,
    schedules: [],
    scheduleSelection: null,
    useSchedule: false,
    parameters: {},
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel', 'specificationToken'],

  computed: {
    // Get currently selected command.
    command: function () {
      return Lodash.find(this.commands, {'token': this.commandSelection})
    },

    // Message shown next to schedule switch.
    scheduleMessage: function () {
      return (!this.useSchedule) ? 'No schedule. Invoke command immediately.'
        : 'Invoke command on schedule below.'
    }
  },

  watch: {
    // Clear schedule selection if not using schedule.
    useSchedule: function (value) {
      if (!value) {
        this.$data.scheduleSelection = null
      }
    },

    // Indicate that schedule was updated.
    scheduleSelection: function (value) {
      this.$emit('scheduleUpdated', value)
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var user = this.$store.getters.user
      var payload = {}
      payload.initiator = 'REST'
      payload.initiatorId = user.username
      payload.target = 'Assignment'
      payload.commandToken = this.$data.commandSelection
      payload.parameterValues = this.$data.parameters
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.commandSelection = null
      this.$data.useSchedule = false
      this.$data.scheduleSelection = null
      this.$data.metadata = []
      this.$data.active = 'details'

      var component = this
      _listDeviceCommands(this.$store, this.specificationToken, false)
        .then(function (response) {
          component.$data.commands = response.data.results
        }).catch(function (e) {
          component.showError(e)
        })
      _listSchedules(this.$store, null)
        .then(function (response) {
          component.$data.schedules = response.data.results
        }).catch(function (e) {
          component.showError(e)
        })
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.commandSelection = payload.xxx
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
