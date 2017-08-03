<template>
  <base-dialog :title="title" :width="width" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <v-tabs dark v-model="active">
      <v-tabs-bar slot="activators">
        <v-tabs-slider></v-tabs-slider>
        <v-tabs-item key="details" href="#details">
          Schedule Details
        </v-tabs-item>
        <v-tabs-item key="metadata" href="#metadata">
          Metadata
        </v-tabs-item>
      </v-tabs-bar>
      <slot name="tabcontent"></slot>
      <v-tabs-content key="details" id="details">
        <v-card flat>
          <v-card-text>
            <v-container fluid>
              <v-layout row wrap>
                <v-flex xs12>
                  <v-text-field required class="mt-1" label="Schedule name"
                    v-model="scheduleName" hide-details prepend-icon="info">
                  </v-text-field>
                </v-flex>
                <v-flex xs12>
                  <date-time-picker v-model="scheduleStartDate"
                    label="Schedule start date">
                  </date-time-picker>
                </v-flex>
                <v-flex xs12>
                  <date-time-picker v-model="scheduleEndDate"
                    label="Schedule end date">
                  </date-time-picker>
                </v-flex>
                <v-flex xs12>
                  <v-select required :items="triggerTypes"
                    v-model="scheduleType" label="Trigger type"
                    prepend-icon="info"></v-select>
                </v-flex>
                <v-flex xs12>
                  <v-divider class="mb-3"></v-divider>
                </v-flex>
                <v-flex xs12 v-if="scheduleType === 'CronTrigger'">
                  <v-text-field required class="mt-1" label="Cron expression"
                    v-model="scheduleCron" hide-details prepend-icon="info">
                  </v-text-field>
                </v-flex>
                <v-flex xs12 v-if="scheduleType === 'SimpleTrigger'">
                  <v-text-field type="number" class="mt-1" label="Interval (ms)"
                    v-model="scheduleInterval" hide-details prepend-icon="info">
                  </v-text-field>
                  <v-text-field type="number" class="mt-1" label="Repetitions"
                    v-model="scheduleRepetitons" hide-details
                    prepend-icon="info">
                  </v-text-field>
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
</template>

<script>
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'
import DateTimePicker from '../common/DateTimePicker'

export default {

  data: () => ({
    active: null,
    menu: null,
    dialogVisible: false,
    scheduleName: null,
    scheduleStartDate: null,
    scheduleEndDate: null,
    scheduleType: null,
    scheduleCron: null,
    scheduleInterval: null,
    scheduleRepetitons: null,
    metadata: [],
    triggerTypes: [
      {
        'text': 'Simple Trigger',
        'value': 'SimpleTrigger'
      }, {
        'text': 'Cron Trigger',
        'value': 'CronTrigger'
      }
    ],
    error: null
  }),

  components: {
    BaseDialog,
    MetadataPanel,
    DateTimePicker
  },

  props: ['title', 'width', 'createLabel', 'cancelLabel'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      let payload = {}
      payload.name = this.$data.scheduleName
      payload.startDate = Utils.formatIso8601(this.$data.scheduleStartDate)
      payload.endDate = Utils.formatIso8601(this.$data.scheduleEndDate)
      payload.triggerType = this.$data.scheduleType

      let triggerConfig = {}
      payload.triggerConfiguration = triggerConfig
      if (payload.triggerType === 'CronTrigger') {
        triggerConfig.cronExpression = this.$data.scheduleCron
      } else if (payload.triggerType === 'SimpleTrigger') {
        triggerConfig.repeatInterval = this.$data.scheduleInterval
        triggerConfig.repeatCount = this.$data.scheduleRepetitons
      }
      payload.metadata = Utils.arrayToMetadata(this.$data.metadata)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.scheduleName = null
      this.$data.scheduleStartDate = null
      this.$data.scheduleEndDate = null
      this.$data.scheduleType = null
      this.$data.scheduleCron = null
      this.$data.scheduleInterval = null
      this.$data.scheduleRepetitons = null
      this.$data.metadata = []
      this.$data.active = 'details'
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()

      if (payload) {
        this.$data.scheduleName = payload.name
        this.$data.scheduleStartDate = Utils.parseIso8601(payload.startDate)
        this.$data.scheduleEndDate = Utils.parseIso8601(payload.endDate)
        this.$data.scheduleType = payload.triggerType

        let triggerConfig = payload.triggerConfiguration
        if (triggerConfig) {
          if (payload.triggerType === 'CronTrigger') {
            this.$data.scheduleCron = triggerConfig.cronExpression
          } else if (payload.triggerType === 'SimpleTrigger') {
            this.$data.scheduleInterval = triggerConfig.repeatInterval
            this.$data.scheduleRepetitons = triggerConfig.repeatCount
          }
        }
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
