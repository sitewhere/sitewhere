<template>
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
        <header-field label="State">
          <span>{{ tenant.engineState.lifecycleStatus }}</span>
        </header-field>
      </div>
      <div v-if="tenant.engineState" class="tenant-buttons">
        <v-btn v-if="tenant.engineState.lifecycleStatus !== 'Started'"
          class="blue darken-2 white--text ml-0"
          @click.native.stop="onEditTenant">
          <v-icon fa class="white--text mr-2 fa-lg">edit</v-icon>
          Edit
        </v-btn>
        <v-btn v-if="tenant.engineState.lifecycleStatus !== 'Started'"
          class="red darken-2 white--text ml-0"
          @click.native.stop="onDeleteTenant">
          <v-icon fa class="white--text mr-2 fa-lg">times</v-icon>
          Delete
        </v-btn>
        <v-btn v-if="tenant.engineState.lifecycleStatus !== 'Started'"
          class="green darken-2 white--text ml-0"
          @click.native.stop="onStartTenant">
          <v-icon fa class="white--text mr-2 fa-lg">power-off</v-icon>
          Start
        </v-btn>
        <v-btn v-if="tenant.engineState.lifecycleStatus === 'Started'"
          class="red darken-2 white--text ml-0"
          @click.native.stop="onStopTenant">
          <v-icon fa class="white--text mr-2 fa-lg">power-off</v-icon>
          Stop Tenant
        </v-btn>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'

export default {

  data: () => ({
    copyData: null,
    showIdCopied: false
  }),

  props: ['tenant'],

  components: {
    HeaderField
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
</style>
