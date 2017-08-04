<template>
  <v-card hover class="tenant white pa-2">
    <v-card-text>
      <div class="tenant-logo" :style="tenantLogoStyle(this.tenant)">
      </div>
      <div class="tenant-divider"></div>
      <div class="tenant-name title ellipsis">{{tenant.name}}</div>
      <div class="tenant-id subheading ellipsis">{{tenant.id}}</div>
      <div v-if="tenant.engineState" class="tenant-state ellipsis">
        {{tenant.engineState.lifecycleStatus}}
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
.tenant-divider {
  position: absolute;
  top: 10px;
  left: 200px;
  bottom: 10px;
  width: 20px;
  border-right: 1px solid #eee;
}
.tenant-name {
  position: absolute;
  top: 10px;
  left: 235px;
  right: 10px;
}
.tenant-id {
  position: absolute;
  top: 40px;
  left: 235px;
  right: 10px;
  bottom: 10px;
}
.tenant-state {
  position: absolute;
  top: 70px;
  left: 235px;
  right: 10px;
  bottom: 10px;
}
.ellipsis {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
