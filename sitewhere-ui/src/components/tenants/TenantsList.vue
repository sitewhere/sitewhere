<template>
  <navigation-page icon="fa-cog" title="Manage Tenants">
    <div slot="content">
      <v-container fluid grid-list-md v-if="tenants">
        <v-layout row wrap>
           <v-flex xs12 v-for="(tenant, index) in tenants" :key="tenant.token">
            <tenant-list-entry :tenant="tenant" class="mb-2"
              @click="onOpenTenant(tenant)"
              @openTenant="onOpenTenant(tenant)"
              @configureTenant="onConfigureTenant(tenant)">
            </tenant-list-entry>
          </v-flex>
        </v-layout>
      </v-container>
      <pager :results="results" @pagingUpdated="updatePaging"></pager>
      <tenant-create-dialog @tenantAdded="refresh">
      </tenant-create-dialog>
    </div>
  </navigation-page>
</template>

<script>
import NavigationPage from '../common/NavigationPage'
import Pager from '../common/Pager'
import TenantListEntry from './TenantListEntry'
import TenantCreateDialog from './TenantCreateDialog'
import {_listTenants} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    tenants: null
  }),

  components: {
    NavigationPage,
    Pager,
    TenantListEntry,
    TenantCreateDialog
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh list of sites.
    refresh: function () {
      var user = this.$store.getters.user
      var paging = this.$data.paging.query
      var component = this
      _listTenants(this.$store, null, user.username, true, paging)
        .then(function (response) {
          component.results = response.data
          component.tenants = response.data.results
        }).catch(function (e) {
        })
    },

    // Called to open tenant management for tenant.
    onOpenTenant: function (tenant) {
      this.$store.commit('selectedTenant', tenant)
      this.$router.push('/tenants/' + tenant.token + '/areas')
    },

    // Called to open tenant detail.
    onConfigureTenant: function (tenant) {
      this.$router.push('/system/tenants/' + tenant.token)
    }
  }
}
</script>

<style scoped>
</style>
