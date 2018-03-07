<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs v-model="active">
      <v-tabs-bar dark color="primary">
        <v-tabs-item key="details" href="#details">
          Command Details
        </v-tabs-item>
        <v-tabs-item key="schedule" href="#schedule">
          Schedule
        </v-tabs-item>
        <v-tabs-item key="metadata" href="#metadata">
          Metadata
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
                    <v-select :items="commands" v-model="commandToken"
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
          <v-card>
            <v-card-text>
              <v-switch class="mb-0" :label="scheduleMessage" v-model="useSchedule">
              </v-switch>
              <schedule-chooser :enabled="useSchedule"
                @scheduleUpdated="onScheduleUpdated">
              </schedule-chooser>
            </v-card-text>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="metadata" id="metadata">
          <metadata-panel :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
        </v-tabs-content>
      </v-tabs-items>
    </v-tabs>
  </base-dialog>
</template>

<script>
import Lodash from 'lodash'
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'
import ScheduleChooser from '../schedules/ScheduleChooser'
import {
  _listDeviceCommands
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    command: null,
    commands: [],
    commandToken: null,
    parameters: {},
    useSchedule: false,
    scheduleToken: null,
    metadata: [],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel,
    ScheduleChooser
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel', 'filter'],

  computed: {
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
        this.$data.scheduleToken = null
      }
    },
    // Indicate that command was updated.
    commandToken: function (value) {
      let commands = this.$data.commands
      if (commands) {
        let command = Lodash.find(commands, {'token': value})
        if (command) {
          this.$data.command = command
        } else {
          this.$data.command = null
        }
      }
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      var payload = {}
      payload.commandToken = this.$data.commandToken
      payload.parameterValues = this.$data.parameters
      payload.deviceTypeToken = this.filter.deviceType
      payload.areaToken = this.filter.area
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },
    // Reset dialog contents.
    reset: function (e) {
      this.$data.commandToken = null
      this.$data.parameters = {}
      this.$data.useSchedule = false
      this.$data.scheduleToken = null
      this.$data.metadata = []
      this.$data.active = 'details'

      // Command list filter options.
      let options = {}
      options.includeDeleted = false

      if (!this.filter.deviceType) {
        console.log('Device type not set for batch command.')
        return
      }

      var component = this
      _listDeviceCommands(this.$store, this.filter.deviceType, options)
        .then(function (response) {
          let commands = response.data.results
          component.$data.commands = commands
        }).catch(function (e) {
          component.showError(e)
        })
    },
    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
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
    // Called when schedule is updated.
    onScheduleUpdated: function (scheduleToken) {
      this.$data.scheduleToken = scheduleToken
      this.$emit('scheduleUpdated', scheduleToken)
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
