<template>
  <div>
    <v-card v-if="tenant" class="tenant white">
      <v-card-text>
        <div class="tenant-logo" :style="tenantLogoStyle(this.tenant)"></div>
        <div class="tenant-headers">
          <header-field label="Tenant id">
            <span>{{ tenant.id }}</span>
          </header-field>
          <header-field label="Name">
            <span>{{ tenant.name }}</span>
          </header-field>
          <header-field label="State" v-if="tenant.engineState">
            <span>{{ tenant.engineState.lifecycleStatus }}</span>
          </header-field>
        </div>
        <div v-if="tenant.engineState" class="tenant-buttons">
          <v-btn v-if="tenant.engineState.lifecycleStatus !== 'Started'"
            :disabled="tenantCommandRunning"
            class="blue darken-2 white--text ml-0"
            @click.native.stop="onEditTenant">
            <v-icon fa class="white--text mr-2 fa-lg">edit</v-icon>
            Edit
          </v-btn>
          <v-btn v-if="tenant.engineState.lifecycleStatus !== 'Started'"
            :disabled="tenantCommandRunning"
            class="red darken-2 white--text ml-0"
            @click.native.stop="onDeleteTenant">
            <v-icon fa class="white--text mr-2 fa-lg">times</v-icon>
            Delete
          </v-btn>
          <v-btn v-if="tenant.engineState.lifecycleStatus !== 'Started'"
            :disabled="tenantCommandRunning"
            class="green darken-2 white--text ml-0"
            @click.native.stop="onStartTenant">
            <v-icon fa class="white--text mr-2 fa-lg">power-off</v-icon>
            Start
          </v-btn>
          <v-btn v-if="tenant.engineState.lifecycleStatus === 'Started'"
            :disabled="tenantCommandRunning"
            class="red darken-2 white--text ml-0"
            @click.native.stop="onStopTenant">
            <v-icon fa class="white--text mr-2 fa-lg">power-off</v-icon>
            Stop Tenant
          </v-btn>
        </div>
      </v-card-text>
      <v-progress-linear class="progress ma-0" v-if="tenantCommandRunning"
        v-model="tenantCommandPercent"></v-progress-linear>
    </v-card>
    <v-card v-if="tenant.engineState.staged" class="mt-2">
      <v-card-text class="yellow lighten-3 stage-warning">
        Tenant has staged updates that have not been applied. Reboot tenant to apply changes.
        <v-btn dark left
          class="red darken-2 pa-1 ma-0 ml-3"
          v-tooltip:top="{ html: 'Reboot Tenant' }"
          @click.native.stop="onRebootTenant">
          <v-icon fa class="fa-lg mr-2 white--text">refresh</v-icon>
          Reboot Tenant
        </v-btn>
      </v-card-text>
    </v-card>
    <tenant-update-dialog ref="update" :tenantId="tenant.id"
      @tenantUpdated="onTenantEdited">
    </tenant-update-dialog>
    <tenant-delete-dialog ref="delete" :tenantId="tenant.id"
      @tenantDeleted="onTenantDeleted">
    </tenant-delete-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'
import TenantUpdateDialog from './TenantUpdateDialog'
import TenantDeleteDialog from './TenantDeleteDialog'

export default {

  data: () => ({
    copyData: null,
    showIdCopied: false
  }),

  props: ['tenant', 'tenantCommandRunning', 'tenantCommandPercent'],

  components: {
    HeaderField,
    TenantUpdateDialog,
    TenantDeleteDialog
  },

  methods: {
    // Styling for tenant logo.
    tenantLogoStyle: function (tenant) {
      return {
        'background': 'url(' + tenant.logoUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%'
      }
    },
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
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
    },
    // Indicate start button clicked.
    onStartTenant: function () {
      this.$emit('start')
    },
    // Indicate stop button clicked.
    onStopTenant: function () {
      this.$emit('stop')
    },
    // Indicate reboot button clicked.
    onRebootTenant: function () {
      this.$emit('reboot')
    }
  }
}
</script>

<style scoped>
.tenant {
  min-height: 100px;
  overflow-y: hidden;
}
.tenant-logo {
  position: absolute;
  top: 10px;
  left: 10px;
  bottom: 10px;
  width: 200px;
}
.tenant-headers {
  position: absolute;
  top: 14px;
  left: 250px;
}
.tenant-buttons {
  position: absolute;
  top: 25px;
  right: 10px;
}
.progress {
  position: absolute;
  bottom: 0px;
  left: 0px;
  right: 0px;
}
.stage-warning {
  font-size: 16px;
  font-weight: 500;
  text-align: center;
}
</style>
