<template>
  <div v-if="tenant">
    <v-app>
      <tenant-detail-header :tenant="tenant"
        :tenantCommandRunning="tenantCommandRunning"
        :tenantCommandPercent="tenantCommandPercent" class="mb-3"
        @refresh="refresh">
      </tenant-detail-header>
      <microservice-editor :configModel="configModel" :config="config">
      </microservice-editor>
    </v-app>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import TenantDetailHeader from './TenantDetailHeader'
import MicroserviceEditor from './MicroserviceEditor'
import {
  _getTenant,
  _getConfigurationModel,
  _getTenantConfiguration
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
    active: null
  }),

  components: {
    FloatingActionButton,
    TenantDetailHeader,
    MicroserviceEditor
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
    }
  }
}
</script>

<style>
</style>
