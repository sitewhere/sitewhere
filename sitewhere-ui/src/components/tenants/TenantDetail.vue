<template>
  <div v-if="tenant">
    <v-app>
      <tenant-detail-header :tenant="tenant"
        :tenantCommandRunning="tenantCommandRunning"
        :tenantCommandPercent="tenantCommandPercent" class="mb-3"
        @start="onStartTenant" @stop="onStopTenant" @reboot="onRebootTenant">
      </tenant-detail-header>
      <v-tabs class="elevation-2" dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
          <v-tabs-item key="configuration" href="#configuration">
            Tenant Configuration
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="configuration" id="configuration">
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
                  <v-toolbar v-else flat light class="grey lighten-4">
                    <v-icon light fa class="fa-lg">{{contextElement.icon}}</v-icon>
                    <v-toolbar-title class="black--text">
                      {{ elementTitle(contextElement) }}
                    </v-toolbar-title>
                    <v-spacer></v-spacer>
                    <element-delete-dialog v-if="contextElement.optional"
                      :element="contextElement"
                      @elementDeleted="onDeleteElement(contextElement)">
                    </element-delete-dialog>
                    <v-btn class="blue darken-2 white--text mr-3"
                      @click.native="onPushContext(contextElement)">
                      <v-icon fa class="white--text mr-1">edit</v-icon>
                      Edit
                    </v-btn>
                  </v-toolbar>
                </v-card-text>
              </v-card>
            </v-card-text>
          </v-card>
        </v-tabs-content>
      </v-tabs>
      <configuration-element-create-dialog ref="create"
        :model="tenantDialogModel"
        @elementAdded="onComponentAdded">
      </configuration-element-create-dialog>
      <configuration-element-update-dialog ref="update"
        :model="tenantDialogModel" :config="tenantDialogConfig"
        @elementUpdated="onConfigurationElementUpdated">
      </configuration-element-update-dialog>
      <stage-updates-dialog :tenantId="tenantId" :json="tenantConfig"
        @staged="onStagingComplete">
      </stage-updates-dialog>
    </v-app>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import FloatingActionButton from '../common/FloatingActionButton'
import TenantDetailHeader from './TenantDetailHeader'
import ElementPlaceholder from './ElementPlaceholder'
import AttributeField from './AttributeField'
import ElementDeleteDialog from './ElementDeleteDialog'
import StageUpdatesDialog from './StageUpdatesDialog'
import ConfigurationElementCreateDialog from './ConfigurationElementCreateDialog'
import ConfigurationElementUpdateDialog from './ConfigurationElementUpdateDialog'
import {wizard} from './TenantConfigEditor'
import {
  _getTenant,
  _getTenantConfiguration,
  _getTenantConfigurationModel,
  _getTenantConfigurationRoles,
  _startTenant,
  _stopTenant,
  _rebootTenant
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    tenantId: null,
    tenant: null,
    tenantConfig: null,
    tenantConfigModel: null,
    tenantConfigRoles: null,
    currentContext: null,
    wizardContexts: [],
    tenantDialogModel: null,
    tenantDialogConfig: null,
    tenantCommandPercent: 0,
    tenantCommandRunning: false,
    active: null
  }),

  components: {
    FloatingActionButton,
    TenantDetailHeader,
    ElementPlaceholder,
    AttributeField,
    ElementDeleteDialog,
    StageUpdatesDialog,
    ConfigurationElementCreateDialog,
    ConfigurationElementUpdateDialog
  },

  created: function () {
    this.$data.tenantId = this.$route.params.tenantId
    this.refresh()
  },

  computed: {
    configDataAvailable: function () {
      return this.$data.tenantConfig && this.$data.tenantConfigModel &&
        this.$data.tenantConfigRoles
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
      wizard.config = this.$data.tenantConfig
      wizard.configModel = this.$data.tenantConfigModel
      wizard.editorContexts = this.$data.wizardContexts
      wizard.roles = this.$data.tenantConfigRoles
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
    // Called to refresh data.
    refresh: function () {
      // Load information.
      var component = this
      _getTenant(this.$store, this.$data.tenantId, true)
        .then(function (response) {
          component.onLoaded(response.data)
        }).catch(function (e) {
        })
      _getTenantConfiguration(this.$store, this.$data.tenantId)
        .then(function (response) {
          component.$data.tenantConfig = response.data
        }).catch(function (e) {
        })
      _getTenantConfigurationModel(this.$store)
        .then(function (response) {
          component.$data.tenantConfigModel = response.data
        }).catch(function (e) {
        })
      _getTenantConfigurationRoles(this.$store)
        .then(function (response) {
          component.$data.tenantConfigRoles = response.data
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (tenant) {
      this.$data.tenant = tenant
      var section = {
        id: 'tenants',
        title: 'Manage Tenant',
        icon: 'layers',
        route: '/tenants/' + tenant.id,
        longTitle: 'Manage Tenant: ' + tenant.id
      }
      this.$store.commit('currentSection', section)
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
          this.$data.tenantDialogModel = relative.model
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
    onPushContext: function (role) {
      let contexts = wizard.pushRelativeContext(role.localName)
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
      this.$data.tenantDialogModel = this.$data.currentContext.model
      this.$data.tenantDialogConfig = this.$data.currentContext.config
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
    },

    // Called to stage updates.
    onStagingComplete: function () {
      this.refresh()
    },

    // Gets JSON object for last complete progress record.
    lastRecord: function (response) {
      var entries = response.split(/\r?\n/)
      if (entries.length === 0) {
        return null
      } else if (entries.length === 1) {
        if (entries[0].endsWith('}')) {
          return JSON.parse(entries[0])
        }
        return null
      } else {
        if (entries[entries.length - 1].endsWith('}')) {
          return JSON.parse(entries[entries.length - 1])
        }
        return JSON.parse(entries[entries.length - 2])
      }
    },

    // Start a tenant while monitoring progress.
    onStartTenant: function () {
      var component = this
      this.$data.tenantCommandRunning = true
      this.$data.tenantCommandPercent = 0
      _startTenant(this.$store, this.$data.tenantId,
        e => {
          let record = this.lastRecord(e.currentTarget.response)
          if (record.progressPercentage) {
            this.$data.tenantCommandPercent = record.progressPercentage
          }
        })
        .then(function (response) {
          component.$data.tenantCommandRunning = false
          component.refresh()
        }).catch(function (e) {
          component.$data.tenantCommandRunning = false
        })
    },

    // Stop a tenant while monitoring progress.
    onStopTenant: function () {
      var component = this
      this.$data.tenantCommandRunning = true
      this.$data.tenantCommandPercent = 0
      _stopTenant(this.$store, this.$data.tenantId,
        e => {
          let record = this.lastRecord(e.currentTarget.response)
          if (record.progressPercentage) {
            this.$data.tenantCommandPercent = record.progressPercentage
          }
        })
        .then(function (response) {
          component.$data.tenantCommandRunning = false
          component.refresh()
        }).catch(function (e) {
          component.$data.tenantCommandRunning = false
        })
    },

    // Reboot a tenant while monitoring progress.
    onRebootTenant: function () {
      var component = this
      this.$data.tenantCommandRunning = true
      this.$data.tenantCommandPercent = 0
      _rebootTenant(this.$store, this.$data.tenantId,
        e => {
          let record = this.lastRecord(e.currentTarget.response)
          if (record.progressPercentage) {
            this.$data.tenantCommandPercent = record.progressPercentage
          }
        })
        .then(function (response) {
          component.$data.tenantCommandRunning = false
          component.refresh()
        }).catch(function (e) {
          component.$data.tenantCommandRunning = false
        })
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
