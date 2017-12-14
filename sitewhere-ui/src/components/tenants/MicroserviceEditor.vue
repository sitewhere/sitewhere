<template>
  <span>
    <v-card v-if="currentContext" class="elevation-0">
      <v-card-text>
        <div>
          <v-breadcrumbs divider="/">
            <v-breadcrumbs-item v-for="context in wizardContexts"
              :key="context.model.localName"
              @click.native="onPopToContext(context.model.localName)">
              {{ context.model.name }}
            </v-breadcrumbs-item>
          </v-breadcrumbs>
        </div>
        <!-- Banner -->
        <v-card class="mb-3">
          <v-toolbar flat dark class="primary">
            <v-icon dark fa class="fa-lg">{{currentContext.model.icon}}</v-icon>
            <v-toolbar-title class="white--text">{{currentContext.model.name}}</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon class="ml-0" v-if="wizardContexts.length > 1"
              @click.native="onPopContext">
              <v-icon fa class="fa-lg">arrow-up</v-icon>
            </v-btn>
            <v-btn icon class="ml-0" v-if="currentContext.model.attributes"
              @click.native="onConfigureCurrent">
              <v-icon fa class="fa-lg">gear</v-icon>
            </v-btn>
            <v-btn icon class="ml-0" v-if="currentContext.model.role.optional"
              @click.native="onDeleteCurrent">
              <v-icon fa class="fa-lg">times</v-icon>
            </v-btn>
          </v-toolbar>
          <v-card-text v-html="currentContext.model.description"></v-card-text>
        </v-card>
        <!-- Attributes -->
        <div
          v-if="currentContext.groups && currentContext.groups.length">
          <v-card class="grey lighten-4 mb-3"
            v-for="group in currentContext.groups"
            :key="group.id">
            <v-card-text
              class="subheading blue darken-2 white--text pa-2">
              <strong>
                {{ group.id ? group.description : 'Component Settings' }}
              </strong>
            </v-card-text>
            <v-card-text class="subheading pa-0 pl-2">
              <v-container fluid>
                <attribute-field
                  v-for="attribute in group.attributes"
                  :key="attribute.name" :attribute="attribute"
                  :attrValues="attributeValues" :readOnly="true">
                </attribute-field>
              </v-container>
            </v-card-text>
          </v-card>
        </div>
        <!-- Elements -->
        <v-card v-if="currentContext.content">
          <v-card-text class="pa-0"
            v-for="contextElement in currentContext.content.elements"
            :key="contextElement.name">
            <element-placeholder v-if="!contextElement.hasContent"
              :contextElement="contextElement"
              @addComponent="onAddComponent">
            </element-placeholder>
            <v-toolbar v-else flat light class="grey lighten-4 pa-2">
              <v-icon light fa class="fa-lg">{{contextElement.icon}}</v-icon>
              <v-toolbar-title class="black--text">
                {{ elementTitle(contextElement) }}
              </v-toolbar-title>
              <v-spacer></v-spacer>
              <element-delete-dialog :element="contextElement"
                @elementDeleted="onDeleteElement(contextElement)">
              </element-delete-dialog>
              <v-btn class="green darken-2 white--text mr-3"
                @click.native="onPushContext(contextElement)">
                <v-icon fa class="white--text mr-2 mt-0">arrow-right</v-icon>
                Open
              </v-btn>
            </v-toolbar>
          </v-card-text>
        </v-card>
      </v-card-text>
    </v-card>
    <configuration-element-create-dialog ref="create"
      :model="elementDialogModel"
      @elementAdded="onComponentAdded">
    </configuration-element-create-dialog>
    <configuration-element-update-dialog ref="update"
      :model="elementDialogModel" :config="elementDialogConfig"
      @elementUpdated="onConfigurationElementUpdated">
    </configuration-element-update-dialog>
  </span>
</template>

<script>
import Utils from '../common/Utils'
import FloatingActionButton from '../common/FloatingActionButton'
import ElementPlaceholder from './ElementPlaceholder'
import AttributeField from './AttributeField'
import ElementDeleteDialog from './ElementDeleteDialog'
import ConfigurationElementCreateDialog from './ConfigurationElementCreateDialog'
import ConfigurationElementUpdateDialog from './ConfigurationElementUpdateDialog'
import {wizard} from './MicroserviceEditorLogic'

export default {

  data: () => ({
    currentContext: null,
    wizardContexts: [],
    elementDialogModel: null,
    elementDialogConfig: null
  }),

  props: ['config', 'configModel'],

  components: {
    FloatingActionButton,
    ElementPlaceholder,
    AttributeField,
    ElementDeleteDialog,
    ConfigurationElementCreateDialog,
    ConfigurationElementUpdateDialog
  },

  computed: {
    configDataAvailable: function () {
      return this.config && this.configModel
    },

    // Compute attribute values for current context.
    attributeValues: function () {
      if (this.$data.currentContext) {
        var attributes = this.$data.currentContext['config'].attributes
        return Utils.arrayToMetadata(attributes)
      }
      return {}
    }
  },

  watch: {
    configDataAvailable: function (available) {
      wizard.config = this.config
      wizard.configModel = this.configModel
      wizard.editorContexts = this.$data.wizardContexts
      this.onWizardContextsUpdated(wizard.reset())
    }
  },

  methods: {
    elementTitle: function (element) {
      let title = element.name
      if (element.resolvedIndexAttribute) {
        title += ' (' + element.resolvedIndexAttribute + ')'
      }
      return title
    },

    // Update wizard context stack.
    onWizardContextsUpdated: function (contexts) {
      this.$data.wizardContexts = contexts
      this.$data.currentContext = null
      this.$data.currentContext = contexts[contexts.length - 1]
    },

    // Add a component.
    onAddComponent: function (option) {
      let relative = wizard.getRelativeContext(option.localName)
      if (relative.model) {
        if (!relative.model.attributes) {
          this.onComponentAdded({
            'name': option.localName,
            'attributes': null
          })
        } else {
          this.$data.elementDialogModel = relative.model
          this.$refs['create'].onOpenDialog()
        }
      }
    },

    // Called after a component is added.
    onComponentAdded: function (component) {
      let contexts = wizard.onAddChild(
        component.name, component.attributes)
      this.onWizardContextsUpdated(contexts)
    },

    // Push a context on to the stack.
    onPushContext: function (contextElement) {
      let contexts = wizard.pushRelativeContext(
        contextElement.id, contextElement.localName)
      this.onWizardContextsUpdated(contexts)
    },

    // Called to pop a context from the stack.
    onPopContext: function () {
      let contexts = wizard.popOne()
      this.onWizardContextsUpdated(contexts)
    },

    // Called to pop to a given context.
    onPopToContext: function (name) {
      let contexts = wizard.popToContext(name)
      this.onWizardContextsUpdated(contexts)
    },

    // Called to configure the current context.
    onConfigureCurrent: function () {
      this.$data.elementDialogModel = this.$data.currentContext.model
      this.$data.elementDialogConfig = this.$data.currentContext.config
      this.$refs['update'].onOpenDialog()
    },

    // Called after configuraton element has been updated.
    onConfigurationElementUpdated: function (updated) {
      let contexts = wizard.onUpdateCurrent(updated.attributes)
      this.onWizardContextsUpdated(contexts)
    },

    // Called to delete the current context.
    onDeleteElement: function (element) {
      let contexts = wizard.onDeleteChild(element.id)
      this.onWizardContextsUpdated(contexts)
    },

    // Called to delete the current context.
    onDeleteCurrent: function () {
    }
  }
}
</script>

<style>
.breadcrumbs {
  justify-content: left;
  padding: 0;
}
</style>
