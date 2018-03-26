<template>
  <v-card>
    <v-card-text>
      <v-toolbar flat dense color="primary">
        <v-icon dark>fa-gear</v-icon>
        <v-toolbar-title class="white--text">
          Tenant Engine Status
        </v-toolbar-title>
      </v-toolbar>
      <v-card>
        <v-card-text>
          <tenant-runtime-entry v-for="runtime in tenantRuntimes"
            :key="runtime.microservice.hostname" :tenantRuntime="runtime">
          </tenant-runtime-entry>
        </v-card-text>
      </v-card>
    </v-card-text>
  </v-card>
</template>

<script>
import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
import TenantRuntimeEntry from './TenantRuntimeEntry'
import {
  _getMicroserviceTenantRuntimeState,
  createAdminWebSocketUrl
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    tenantRuntimesByHostname: {},
    tenantRuntimes: []
  }),

  components: {
    TenantRuntimeEntry
  },

  props: ['identifier', 'tenantToken'],

  computed: {
  },

  created: function () {
    this.refresh()
    this.connectWebSocket()
  },

  methods: {
    // Called to refresh data.
    refresh: function () {
      var component = this
      _getMicroserviceTenantRuntimeState(this.$store, this.identifier,
        this.tenantToken)
        .then(function (response) {
          var rts = response.data
          rts.forEach(function (element) {
            component.addOrUpdateTenantRuntime(element)
          })
        })
    },

    // Add or update a tenant runtime entry.
    addOrUpdateTenantRuntime: function (runtime) {
      var rtByHostname = this.$data.tenantRuntimesByHostname
      rtByHostname[runtime.microservice.hostname] = runtime
      var keys = Object.keys(rtByHostname)
      var rts = keys.map(function (v) {
        return rtByHostname[v]
      })
      rts.sort(this.compareTenantRuntimes)
      this.$data.tenantRuntimes = rts
    },

    compareTenantRuntimes: function (rt1, rt2) {
      if (rt1.microservice.hostname > rt2.microservice.hostname) {
        return 1
      }
      return -1
    },

    // Connect to WebSocket for dynamic updates.
    connectWebSocket: function () {
      this.socket = new SockJS(createAdminWebSocketUrl(this.$store))
      this.stompClient = Stomp.over(this.socket)
      this.stompClient.connect({}, (frame) => {
        this.stompClient.subscribe('/topic/topology/microservices/' +
          this.identifier + '/**', (response) => {
          var runtime = JSON.parse(response.body)
          console.log(runtime)
          this.addOrUpdateTenantRuntime(runtime)
        })
      }, (error) => {
        console.log(error)
      })
    },
    disconnectWebSocket: function () {
      if (this.stompClient) {
        this.stompClient.disconnect()
      }
    }
  }
}
</script>

<style scoped>
</style>
