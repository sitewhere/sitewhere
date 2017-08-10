<template>
  <div v-if="tenant">
    <v-app>
      <tenant-detail-header :tenant="tenant" class="mb-3">
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
                    <v-icon fa>arrow-up</v-icon>
                  </v-btn>
                  <v-btn icon class="ml-0" v-if="currentContext.model.attributes"
                    @click.native="onConfigureCurrent">
                    <v-icon fa>gear</v-icon>
                  </v-btn>
                  <v-btn icon class="ml-0" v-if="currentContext.model.role.optional"
                    @click.native="onDeleteCurrent">
                    <v-icon fa>times</v-icon>
                  </v-btn>
                </v-toolbar>
                <v-divider></v-divider>
                <v-card-text v-html="currentContext.model.description"></v-card-text>
              </v-card>
              <!-- Attributes -->
              <v-card class="mb-3" 
                v-if="currentContext.groups && currentContext.groups.length">
                <v-card-text>
                  <v-card class="elevation-0 grey lighten-4 pa-1"
                    v-for="group in currentContext.groups"
                    :key="group.id">
                    <v-card-text class="subheading blue darken-2 white--text">
                      <strong>{{ group.description }}</strong>
                    </v-card-text>
                    <v-card-text class="subheading pa-0 pl-2">
                      <v-list dense>
                        <v-list-tile avatar
                          v-for="attribute in group.attributes"
                          :key="attribute.name">
                          <v-list-tile-avatar>
                            <v-icon fa>{{ attribute.icon }}</v-icon>
                          </v-list-tile-avatar>
                          <v-list-tile-content>
                            <v-list-tile-title>
                              {{ attribute.name }}
                            </v-list-tile-title>
                            <v-list-tile-sub-title>
                              {{ attribute.value }}
                            </v-list-tile-sub-title>
                          </v-list-tile-content>
                        </v-list-tile>
                      </v-list>
                    </v-card-text>
                  </v-card>
                </v-card-text>
              </v-card>
              <!-- Elements -->
              <v-card v-if="currentContext.content">
                <v-card-text class="pa-0"
                  v-for="contextElement in currentContext.content.elements"
                  :key="contextElement.name">
                  <v-toolbar v-if="!contextElement.hasContent" flat dark class="grey lighten-5">
                    <v-icon light fa class="fa-lg">plus</v-icon>
                    <v-toolbar-title class="black--text">{{contextElement.name}}</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-menu offset-y left>
                      <v-btn dark class="grey" slot="activator">Add Component</v-btn>
                      <v-list dense>
                        <v-list-tile v-for="option in contextElement.options"
                          :key="option.role"
                          @click.native="onAddComponent(option)">
                          <v-list-tile-title class="subheading">
                            <v-icon fa class="mr-1">{{ option.icon }}</v-icon>
                            {{ option.name }}
                          </v-list-tile-title>
                        </v-list-tile>
                      </v-list>
                    </v-menu>
                  </v-toolbar>
                  <v-toolbar v-else flat light class="grey lighten-4">
                    <v-icon light fa class="fa-lg">{{contextElement.icon}}</v-icon>
                    <v-toolbar-title class="black--text">{{contextElement.name}}</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-btn class="blue darken-2 white--text mr-4"
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
        :model="tenantCreateModel">
      </configuration-element-create-dialog>
    </v-app>
  </div>
</template>

<script>
import TenantDetailHeader from './TenantDetailHeader'
import ConfigurationElementCreateDialog from './ConfigurationElementCreateDialog'
import {wizard} from './TenantConfigEditor'
import {
  _getTenant,
  _getTenantConfiguration,
  _getTenantConfigurationModel,
  _getTenantConfigurationRoles
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
    tenantCreateModel: null,
    tenantCreateConfig: null,
    active: null
  }),

  components: {
    TenantDetailHeader,
    ConfigurationElementCreateDialog
  },

  created: function () {
    this.$data.tenantId = this.$route.params.tenantId
    this.refresh()
  },

  computed: {
    configDataAvailable: function () {
      return this.$data.tenantConfig && this.$data.tenantConfigModel &&
        this.$data.tenantConfigRoles
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
    // Called to refresh data.
    refresh: function () {
      // Load information.
      var component = this
      _getTenant(this.$store, this.$data.tenantId)
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
          this.$data.tenantCreateModel = relative.model
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
      console.log('configure context')
    },

    // Called to delete the current context.
    onDeleteCurrent: function () {
      console.log('delete context')
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
