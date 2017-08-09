<template>
  <v-card hover class="tenant white pa-2">
    <v-card-text>
      <div class="tenant-logo" :style="tenantLogoStyle(this.tenant)">
      </div>
      <div class="tenant-divider"></div>
      <div class="tenant-name headline ellipsis">
        {{tenant.name}} ({{tenant.id}})
      </div>
      <div class="tenant-actions">
        <v-btn @click.native.stop="onConfigureTenant"
          class="blue white--text tenant-configure ma-0" >
          <v-icon left dark fa>cogs</v-icon>
          Configure Tenant
        </v-btn>
        <v-btn @click.native.stop="onOpenTenant"
          class="green white--text tenant-open" >
          <v-icon left dark fa>database</v-icon>
          Manage Tenant Data
        </v-btn>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['tenant'],

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

    // Open tenant.
    onOpenTenant: function () {
      this.$emit('openTenant', this.tenant)
    },

    // Configure tenant.
    onConfigureTenant: function () {
      this.$emit('configureTenant', this.tenant)
    }
  }
}
</script>

<style scoped>
.tenant {
  min-height: 120px;
  overflow-y: hidden;
}

.tenant-logo {
  position: absolute;
  top: 10px;
  left: 10px;
  bottom: 10px;
  width: 220px;
}
.tenant-divider {
  position: absolute;
  top: 10px;
  left: 220px;
  bottom: 10px;
  width: 20px;
  border-right: 1px solid #eee;
}
.tenant-name {
  position: absolute;
  top: 10px;
  left: 250px;
  right: 10px;
}
.tenant-actions {
  position: absolute;
  right: 7px;
  bottom: 7px;
}
.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
