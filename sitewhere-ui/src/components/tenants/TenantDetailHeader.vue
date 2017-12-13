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
        </div>
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
</style>
