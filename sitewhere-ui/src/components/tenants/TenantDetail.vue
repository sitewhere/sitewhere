<template>
  <div v-if="tenant">
    <v-app>
      <tenant-detail-header :tenant="tenant" class="mb-3" @refresh="refresh">
      </tenant-detail-header>
      <v-toolbar class="blue darken-2 white--text" v-if="tenantTopology">
        <v-toolbar-title>Tenant Microservices</v-toolbar-title>
      </v-toolbar>
      <v-list v-if="tenantTopology" two-line class="elevation-2">
        <template v-for="microservice in tenantTopology">
          <v-list-tile :key="microservice.identifier">
            <v-list-tile-avatar>
              <v-icon left light fa>{{microservice.icon}}</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title v-text="microservice.name"></v-list-tile-title>
              <v-list-tile-sub-title v-html="microservice.description">
              </v-list-tile-sub-title>
            </v-list-tile-content>
          </v-list-tile>
          <v-divider></v-divider>
        </template>
      </v-list>
    </v-app>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import TenantDetailHeader from './TenantDetailHeader'
import {
  _getTenant,
  _getTenantTopology
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    tenantId: null,
    tenant: null,
    tenantTopology: null,
    active: null
  }),

  components: {
    FloatingActionButton,
    TenantDetailHeader
  },

  created: function () {
    this.$data.tenantId = this.$route.params.tenantId
    this.refresh()
  },

  methods: {
    // Called to refresh data.
    refresh: function () {
      // Load tenant data.
      this.refreshTenant()

      // Load configuration data.
      var component = this
      _getTenantTopology(this.$store)
        .then(function (response) {
          component.$data.tenantTopology = response.data
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
      var section = {
        id: 'tenants',
        title: 'Manage Tenant',
        icon: 'layers',
        route: '/tenants/' + tenant.id,
        longTitle: 'Manage Tenant: ' + tenant.id
      }
      this.$store.commit('currentSection', section)
    }
  }
}
</script>

<style>
</style>
