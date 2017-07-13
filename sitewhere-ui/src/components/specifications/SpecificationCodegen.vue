<template>
  <v-card class="ma-3">
    <v-card-text class="pa-0">
      <v-toolbar class="blue darken-2 white--text">
        <v-toolbar-title>Google Protocol Buffers Definition</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn class="ma-0" dark icon v-tooltip:left="{ html: 'Download Code' }"
          @click.native="onProtobufDownload">
          <v-icon fa class="fa-lg">download</v-icon>
        </v-btn>
        <v-btn class="ml-0 mr-2" dark icon v-tooltip:left="{ html: 'Refresh Code' }"
          @click.native="refresh">
          <v-icon >refresh</v-icon>
        </v-btn>
      </v-toolbar>
      <div>
        <pre class="proto-code" v-highlightjs="protobuf"><code class="protobuf"></code></pre>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import {_getDeviceSpecificationProtobuf} from '../../http/sitewhere-api-wrapper'
import {BASE_URL} from '../../http/sitewhere-api'

export default {

  data: () => ({
    protobuf: null
  }),

  props: ['specification'],

  created: function () {
    this.refresh()
  },

  methods: {
    // Refresh list of assignments.
    refresh: function () {
      var component = this
      _getDeviceSpecificationProtobuf(this.$store, this.specification.token)
        .then(function (response) {
          component.protobuf = response.data
        }).catch(function (e) {
        })
    },

    // Called to download protobuf definition.
    onProtobufDownload: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        let url = BASE_URL + 'specifications/' + this.specification.token +
          '/spec.proto?tenantAuthToken=' + tenant.authenticationToken
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
