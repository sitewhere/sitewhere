<template>
  <div v-if="tenant">
    <v-app>
      <tenant-detail-header :tenant="tenant" @refresh="refresh">
        <span slot="buttons">
          <v-btn class="red darken-2 white--text" @click="onDeleteTenant">
            Delete <v-icon class="white--text pl-2">fa-times</v-icon>
          </v-btn>
          <v-btn class="blue white--text" @click="onEditTenant">
            Edit <v-icon class="white--text pl-2">fa-edit</v-icon>
          </v-btn>
        </span>
      </tenant-detail-header>
      <microservice-list title="Tenant Microservices" :topology="tenantTopology"
        @microserviceClicked="onMicroserviceClicked">
      </microservice-list>
    </v-app>
    <tenant-update-dialog ref="update" :tenantId="tenant.id"
      @tenantUpdated="onTenantEdited">
    </tenant-update-dialog>
    <tenant-delete-dialog ref="delete" :tenantId="tenant.id"
      @tenantDeleted="onTenantDeleted">
    </tenant-delete-dialog>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import TenantDetailHeader from './TenantDetailHeader'
import MicroserviceList from '../microservice/MicroserviceList'
import TenantUpdateDialog from './TenantUpdateDialog'
import TenantDeleteDialog from './TenantDeleteDialog'
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
    TenantDetailHeader,
    MicroserviceList,
    TenantUpdateDialog,
    TenantDeleteDialog
  },

  created: function () {
    this.$data.tenantId = this.$route.params.tenantId
    this.refresh()
  },

  methods: {
    // Called if a microservice is clicked.
    onMicroserviceClicked: function (microservice) {
      this.$router.push('/system/tenants/' +
        this.$data.tenantId + '/' + microservice.identifier)
    },

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
    },

    // Called to edit tenant.
    onEditTenant: function () {
      this.$refs['update'].onOpenDialog()
    },

    // Called after tenant is edited.
    onTenantEdited: function () {
      this.$emit('refresh')
    },

    // Called to delete tenant.
    onDeleteTenant: function () {
      this.$refs['delete'].showDeleteDialog()
    },

    // Called after tenant is deleted.
    onTenantDeleted: function () {
      this.$router.push('/system/tenants')
    }
  }
}
</script>

<style>
</style>
