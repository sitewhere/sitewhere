<template>
  <base-dialog :title="title" width="800" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
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
            <element-attribute-group-panel
              v-for="group in groups" :key="group.id" class="mb-3"
              :group="group" :attrValues="attrValues">
            </element-attribute-group-panel>
          </v-card-text>
        </v-card>
      </v-tabs-content>
    </v-tabs>
  </base-dialog>
</template>

<script>
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import ElementAttributeGroupPanel from './ElementAttributeGroupPanel'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    groups: null,
    attrValues: {},
    attrByName: {},
    error: null
  }),

  components: {
    BaseDialog,
    ElementAttributeGroupPanel
  },

  props: ['model', 'config', 'title', 'createLabel', 'cancelLabel'],

  watch: {
    model: function (model) {
      let groups = []
      let attrByName = {}
      if (model) {
        let attributes = []
        let currentGroup = null
        if (model.attributes) {
          for (let i = 0; i < model.attributes.length; i++) {
            var modelAttr = model.attributes[i]
            if (!currentGroup || currentGroup.id !== modelAttr.group) {
              currentGroup = {}
              currentGroup.id = modelAttr.group
              if (modelAttr.group) {
                currentGroup.description = model.attributeGroups[modelAttr.group]
              }
              attributes = []
              currentGroup.attributes = attributes
              groups.push(currentGroup)
            }
            let attr = {
              'localName': modelAttr.localName,
              'name': modelAttr.name,
              'type': modelAttr.type,
              'icon': modelAttr.icon,
              'description': modelAttr.description,
              'required': modelAttr.required
            }
            attributes.push(attr)
            attrByName[modelAttr.localName] = attr
          }
        }
      }
      this.$data.groups = groups
      this.$data.attrByName = attrByName
    },
    config: function (config) {
      this.$data.attrValues = config.attributes
        ? Utils.arrayToMetadata(config.attributes) : {}
    }
  },

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      let payload = {}
      payload.name = this.model.localName
      payload.attributes = Utils.metadataToArray(this.$data.attrValues)
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
</style>
