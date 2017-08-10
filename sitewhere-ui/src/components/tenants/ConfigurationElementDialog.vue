<template>
  <base-dialog :title="title" width="800" :visible="dialogVisible"
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
            <v-card v-for="group in groups" :key="group.id" class="mb-3">
              <v-card-text class="subheading blue darken-2 white--text">
                <strong>{{ group.description }}</strong>
              </v-card-text>
              <v-container fluid>
                <v-layout row wrap v-for="attribute in group.attributes"
                  :key="attribute.name">
                  <v-flex xs4 class="text-xs-right subheading mt-1">
                    <v-icon fa class="mr-1 mb-1">{{ attribute.icon }}</v-icon>
                    {{ attribute.name }}:
                  </v-flex>
                  <v-flex xs1>
                  </v-flex>
                  <v-flex xs7>
                    <v-text-field
                      v-if="attribute.type === 'String'"
                      :required="attribute.required"
                      v-model="values[attribute.localName]"
                      hide-details single-line>
                    </v-text-field>
                    <v-text-field v-if="attribute.type === 'Integer'"
                      :required="attribute.required" type="number"
                      class="mt-1"
                      v-model="values[attribute.localName]"
                      hide-details single-line>
                    </v-text-field>
                    <v-checkbox v-if="attribute.type === 'Boolean'"
                      :label="attribute.name"
                      v-model="values[attribute.localName]">
                    </v-checkbox>
                  </v-flex>
                </v-layout>
              </v-container>
            </v-card>
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
    groups: null,
    values: {},
    error: null
  }),

  components: {
    BaseDialog
  },

  props: ['model', 'config', 'title'],

  watch: {
    model: function (model) {
      let groups = []
      if (model) {
        let attributes = []
        let currentGroup = null
        if (model.attributes) {
          for (let i = 0; i < model.attributes.length; i++) {
            var modelAttr = model.attributes[i]
            if (!currentGroup || currentGroup.id !== modelAttr.group) {
              currentGroup = {}
              currentGroup.id = modelAttr.group
              currentGroup.description = model.attributeGroups[modelAttr.group]
              attributes = []
              currentGroup.attributes = attributes
              groups.push(currentGroup)
            }
            attributes.push({
              'localName': modelAttr.localName,
              'name': modelAttr.name,
              'type': modelAttr.type,
              'icon': modelAttr.icon,
              'description': modelAttr.description,
              'required': modelAttr.required
            })
          }
        }
      }
      this.$data.groups = groups
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
.input-group {
  padding: 0;
}
</style>
