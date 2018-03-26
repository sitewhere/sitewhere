<template>
  <span>
    <v-card v-if="currentContext" flat>
      <v-card-text>
        <!-- Banner shown above microservice content -->
        <microservice-banner :currentContext="currentContext"
          :wizardContexts="wizardContexts" @popContext="onPopContext"
          @popToContext="onPopToContext"
          @configureCurrent="onConfigureCurrent"
          @deleteCurrent="onDeleteCurrent">
        </microservice-banner>
        <!-- Grouped attributes for current context -->
        <component-attributes :currentContext="currentContext"
          :tenantToken="tenantToken" :readOnly="true" :dirty="dirty">
        </component-attributes>
        <!-- Elements -->
        <v-card v-if="currentContext.content">
          <v-card-text class="pa-0"
            v-for="contextElement in currentContext.content.elements"
            :key="contextElement.id">
            <element-placeholder v-if="!contextElement.hasContent"
              :contextElement="contextElement"
              @addComponent="onAddComponent">
            </element-placeholder>
            <v-toolbar v-else flat light class="grey lighten-4">
              <v-icon light class="fa-lg">fa-{{contextElement.icon}}</v-icon>
              <v-toolbar-title class="black--text">
                {{ elementTitle(contextElement) }}
              </v-toolbar-title>
              <v-spacer></v-spacer>
              <element-delete-dialog :element="contextElement"
                @elementDeleted="onDeleteElement(contextElement)">
              </element-delete-dialog>
              <v-btn class="green darken-2 white--text mr-3"
                @click="onPushContext(contextElement)">
                <v-icon class="white--text mr-2 mt-0">fa-arrow-right</v-icon>
                Open
              </v-btn>
            </v-toolbar>
          </v-card-text>
        </v-card>
      </v-card-text>
    </v-card>
    <component-create-dialog ref="create" :context="dialogContext"
      :tenantToken="tenantToken" @elementAdded="onComponentAdded">
    </component-create-dialog>
    <component-update-dialog ref="update" :context="dialogContext"
      :tenantToken="tenantToken" @elementUpdated="onConfigurationElementUpdated">
    </component-update-dialog>
  </span>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import MicroserviceBanner from './MicroserviceBanner'
import ComponentAttributes from './ComponentAttributes'
import ElementPlaceholder from './ElementPlaceholder'
import ElementDeleteDialog from './ElementDeleteDialog'
import ComponentCreateDialog from './ComponentCreateDialog'
import ComponentUpdateDialog from './ComponentUpdateDialog'
import { wizard } from './MicroserviceEditorLogic'

export default {

  data: () => ({
    currentContext: null,
    wizardContexts: [],
    dialogContext: null,
    dirty: false
  }),

  props: ['config', 'configModel', 'identifier', 'tenantToken'],

  components: {
    FloatingActionButton,
    MicroserviceBanner,
    ComponentAttributes,
    ElementPlaceholder,
    ElementDeleteDialog,
    ComponentCreateDialog,
    ComponentUpdateDialog
  },

  computed: {
    configDataAvailable: function () {
      return this.config && this.configModel
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
      console.log(relative)
      if (relative.model) {
        this.$data.dialogContext = relative
        this.$refs['create'].onOpenDialog()
      }
    },

    // Called after a component is added.
    onComponentAdded: function (component) {
      let contexts = wizard.onAddChild(
        component.name, component.attributes)
      this.onWizardContextsUpdated(contexts)
      this.fireDirty()
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
      this.$data.dialogContext = this.$data.currentContext
      this.$refs['update'].onOpenDialog()
    },

    // Called after configuraton element has been updated.
    onConfigurationElementUpdated: function (updated) {
      let contexts = wizard.onUpdateCurrent(updated.attributes)
      console.log(contexts)
      this.onWizardContextsUpdated(contexts)
      this.fireDirty()
    },

    // Called to delete the current context.
    onDeleteElement: function (element) {
      let contexts = wizard.onDeleteChild(element.id)
      this.onWizardContextsUpdated(contexts)
      this.fireDirty()
    },

    // Called to delete the current context.
    onDeleteCurrent: function () {
    },

    // Fire event indicating updates have been made.
    fireDirty: function () {
      this.$data.dirty = true
      this.$emit('dirty')
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
