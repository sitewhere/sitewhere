<template>
  <v-card hover class="white pa-2">
    <v-card-text class="deviceType" @click="onOpenDeviceType">
      <div class="type-logo" :style="logoStyle"></div>
      <div class="type-name">{{deviceType.name}}</div>
      <div class="type-desc">{{deviceType.description}}</div>
    </v-card-text>
  </v-card>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['deviceType'],

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-color': '#fff',
        'background-image': 'url(' + this.deviceType.imageUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    }
  },

  methods: {
    // Called when a device type is clicked.
    onOpenDeviceType: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/tenants/' + tenant.token + '/devicetypes/' +
          this.deviceType.token)
      }
    }
  }
}
</script>

<style scoped>
.deviceType {
  min-height: 122px;
  overflow-y: hidden;
}

.type-logo {
  position: absolute;
  top: 10px;
  left: 10px;
  bottom: 10px;
  width: 100px;
}

.type-name {
  position: absolute;
  top: 5px;
  left: 158px;
  right: 10px;
  font-size: 22px;
  font-weight: 400;
  white-space: nowrap;
  overflow-x: hidden;
}

.type-desc {
  position: absolute;
  top: 42px;
  left: 160px;
  right: 10px;
  bottom: 10px;
  font-size: 14px;
  overflow-y: hidden;
}
</style>
