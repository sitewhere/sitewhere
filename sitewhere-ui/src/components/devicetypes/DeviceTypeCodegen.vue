<template>
  <v-card class="ma-3">
    <v-card-text class="pa-0">
      <v-toolbar class="blue darken-2 white--text">
        <v-toolbar-title>Google Protocol Buffers Definition</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip left>
          <v-btn class="ma-0" dark icon @click="onProtobufDownload"
            slot="activator">
            <v-icon>fa-download</v-icon>
          </v-btn>
          <span>Download Code</span>
        </v-tooltip>
        <v-tooltip left>
          <v-btn class="ml-0 mr-2" dark icon @click="refresh"
            slot="activator">
            <v-icon>fa-refresh</v-icon>
          </v-btn>
          <span>Refresh Code</span>
        </v-tooltip>
      </v-toolbar>
      <div>
        <pre class="proto-code" v-highlightjs="protobuf"><code class="protobuf"></code></pre>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import {
  createCoreApiUrl,
  _getDeviceTypeProtobuf
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    protobuf: null
  }),

  props: ['deviceType'],

  created: function () {
    this.refresh()
  },

  methods: {
    // Refresh list of assignments.
    refresh: function () {
      var component = this
      _getDeviceTypeProtobuf(this.$store, this.deviceType.token)
        .then(function (response) {
          component.protobuf = response.data
        }).catch(function (e) {
        })
    },

    // Called to download protobuf definition.
    onProtobufDownload: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        let url = createCoreApiUrl(this.$store) + 'devicetypes/' +
          this.deviceType.token + '/spec.proto?tenantAuthToken=' +
          tenant.authenticationToken
        window.open(url, '_blank')
      }
    }
  }
}
</script>

<style scoped>
.proto-code {
  max-height: 500px;
  overflow-y: auto;
}
</style>
