<template>
  <div v-if="tenant">
    <v-app>
      <tenant-detail-header :tenant="tenant" @tenantEdited="onTenantEdited"
        @tenantDeleted="onTenantDeleted">
        <span slot="buttons">
          <v-menu open-on-hover offset-y>
    				<v-btn color="blue darken-2" dark slot="activator">
    					<v-icon left dark>fa-bolt</v-icon>
    					Tenant Actions
    				</v-btn>
    				<v-card>
              <v-btn row class="red darken-2 white--text" @click="onDeleteTenant">
                Delete Tenant<v-icon class="white--text pl-2">fa-times</v-icon>
              </v-btn>
              <v-btn row class="blue white--text" @click="onEditTenant">
                Edit Tenant<v-icon class="white--text pl-2">fa-edit</v-icon>
              </v-btn>
    				</v-card>
    			</v-menu>
        </span>
      </tenant-detail-header>
      <v-tabs v-model="active">
        <v-tabs-bar dark color="primary">
          <v-tabs-item key="microservices" href="#microservices">
            Microservices
          </v-tabs-item>
          <v-tabs-item key="scripts" href="#scripts">
            Scripts
          </v-tabs-item>
          <v-tabs-slider></v-tabs-slider>
        </v-tabs-bar>
        <v-tabs-items>
          <v-tabs-content key="microservices" id="microservices">
            <microservice-list :topology="tenantTopology"
              @microserviceClicked="onMicroserviceClicked">
            </microservice-list>
          </v-tabs-content>
          <v-tabs-content key="scripts" id="scripts">
            <scripts-manager :tenantId="tenantId">
            </scripts-manager>
          </v-tabs-content>
        </v-tabs-items>
      </v-tabs>
    </v-app>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import TenantDetailHeader from './TenantDetailHeader'
import MicroserviceList from '../microservice/MicroserviceList'
import ScriptsManager from './ScriptsManager'
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
    ScriptsManager
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

    // Called after tenant is edited.
    onTenantEdited: function () {
      this.$emit('refresh')
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
