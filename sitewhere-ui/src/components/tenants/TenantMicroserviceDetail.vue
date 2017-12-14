<template>
  <div v-if="tenant">
    <v-app>
      <tenant-detail-header class="mb-3" :tenant="tenant" @refresh="refresh">
        <span slot="buttons">
          <v-btn class="red darken-2 white--text"
            @click.native="onDeleteTenant">
            Delete <v-icon fa class="white--text pl-2">times</v-icon>
          </v-btn>
          <v-btn class="blue white--text"
            @click.native="onEditTenant">
            Edit <v-icon fa class="white--text pl-2">edit</v-icon>
          </v-btn>
        </span>
      </tenant-detail-header>
      <unsaved-updates-warning class="mb-3" :unsaved="dirty"
        @save="onSaveConfiguration" @revert="onRevertConfiguration">
      </unsaved-updates-warning>
      <microservice-editor :configModel="configModel" :config="config"
        @dirty="onConfigurationUpdated">
      </microservice-editor>
    </v-app>
  </div>
</template>

<script>
import TenantDetailHeader from './TenantDetailHeader'
import MicroserviceEditor from '../microservice/MicroserviceEditor'
import UnsavedUpdatesWarning from './UnsavedUpdatesWarning'
import {
  _getTenant,
  _getConfigurationModel,
  _getTenantConfiguration,
  _updateTenantConfiguration
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    tenantId: null,
    identifier: null,
    tenant: null,
    config: null,
    configModel: null,
    tenantCommandPercent: 0,
    tenantCommandRunning: false,
    dirty: false
  }),

  components: {
    TenantDetailHeader,
    MicroserviceEditor,
    UnsavedUpdatesWarning
  },

  created: function () {
    this.$data.tenantId = this.$route.params.tenantId
    this.$data.identifier = this.$route.params.identifier
    this.refresh()
  },

  methods: {
    // Called to refresh data.
    refresh: function () {
      // Load tenant data.
      this.refreshTenant()

      // Load configuration data.
      var component = this
      _getConfigurationModel(this.$store, this.$data.identifier)
        .then(function (response) {
          component.$data.configModel = response.data
          var microservice = response.data.microserviceDetails
          var section = {
            id: 'tenants',
            title: 'Configure Microservice',
            icon: 'layers',
            route: '/tenants/' + component.$data.tenantId + '/' + microservice.identifier,
            longTitle: 'Configure Tenant Microservice: ' + microservice.name
          }
          component.$store.commit('currentSection', section)
        }).catch(function (e) {
        })
      _getTenantConfiguration(this.$store, this.$data.tenantId,
        this.$data.identifier)
        .then(function (response) {
          component.$data.config = response.data
        }).catch(function (e) {
        })
    },

    // Refresh only tenant information.
    refreshTenant: function () {
      var component = this
      _getTenant(this.$store, this.$data.tenantId, true)
        .then(function (response) {
          component.onLoaded(response.data)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (tenant) {
      this.$data.tenant = tenant
    },

    // Called when configuration is changed.
    onConfigurationUpdated: function () {
      this.$data.dirty = true
    },

    // Called when configuration is to be saved.
    onSaveConfiguration: function () {
      var component = this
      _updateTenantConfiguration(this.$store, this.$data.tenantId,
        this.$data.identifier, this.$data.config)
        .then(function (response) {
          component.$data.dirty = false
        }).catch(function (e) {
        })
    },

    // Called when configuration is to be reverted.
    onRevertConfiguration: function () {
      this.refresh()
      this.$data.dirty = false
    }
  }
}
</script>

<style>
</style>
