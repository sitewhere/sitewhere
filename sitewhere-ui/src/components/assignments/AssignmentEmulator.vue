<template>
  <div v-if="assignment">
    <v-app>
      <v-card>
        <assignment-list-panel :assignment="assignment" class="mb-3">
        </assignment-list-panel>
        <v-tabs dark v-model="active">
          <v-tabs-bar slot="activators">
            <v-tabs-slider></v-tabs-slider>
            <v-tabs-item key="emulator" href="#emulator">
              Emulator
            </v-tabs-item>
            <v-tabs-item key="mqtt" href="#mqtt">
              MQTT Settings
            </v-tabs-item>
            <v-spacer></v-spacer>
            <v-btn v-if="mqttConnected" small class="green white--text ma-0">
              <v-icon class="white--text mr-2">cloud_upload</v-icon>
              MQTT Connected
            </v-btn>
          </v-tabs-bar>
          <v-tabs-content key="emulator" id="emulator">
            <map-with-zone-overlay-panel ref="map" :site="site" height="600px"
              visible="true" mode="readOnly">
            </map-with-zone-overlay-panel>
          </v-tabs-content>
          <v-tabs-content key="mqtt" id="mqtt">
            <v-card flat>
              <v-card-text>
                <v-container fluid>
                  <v-layout row wrap>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="MQTT Hostname"
                        v-model="mqttHostname" prepend-icon="storage">
                      </v-text-field>
                    </v-flex>
                  </v-layout>
                  <v-layout row wrap>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="MQTT WebSocket Port"
                        v-model="mqttWsPort" prepend-icon="storage">
                      </v-text-field>
                    </v-flex>
                  </v-layout>
                  <v-layout row wrap>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="MQTT Topic"
                        v-model="mqttTopic" prepend-icon="compare_arrows">
                      </v-text-field>
                    </v-flex>
                  </v-layout>
                </v-container>
              </v-card-text>
            </v-card>
          </v-tabs-content>
        </v-tabs>
      </v-card>
    </v-app>
  </div>
</template>

<script>
import MQTT from 'mqtt'
import AssignmentListPanel from './AssignmentListPanel'
import MapWithZoneOverlayPanel from '../sites/MapWithZoneOverlayPanel'
import {_getSite, _getDeviceAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    assignment: null,
    site: null,
    mqttClient: null,
    mqttHostname: 'localhost',
    mqttWsPort: 61623,
    mqttTopic: 'SiteWhere/input/jsonbatch',
    mqttConnected: false,
    swUsername: 'admin',
    swPassword: 'password',
    active: null,
    mapVisible: true
  }),

  components: {
    AssignmentListPanel,
    MapWithZoneOverlayPanel
  },

  created: function () {
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  mounted: function () {
    this.$data.mqttHostname = this.$el.ownerDocument.location.host
  },

  watch: {
    // Monitor MQTT connection.
    mqttConnected: function (value) {
      console.log('MQTT connected ' + value)
    }
  },

  methods: {
    // Called to refresh data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load site information.
      _getDeviceAssignment(this.$store, token)
        .then(function (response) {
          component.onAssignmentLoaded(response.data)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onAssignmentLoaded: function (assignment) {
      var component = this

      this.$data.assignment = assignment
      var section = {
        id: 'emulator',
        title: 'Assignment Emulator',
        icon: 'link',
        route: '/admin/assignments/' + assignment.token + '/emulator',
        longTitle: 'Emulate Assignment: ' + assignment.token
      }
      this.$store.commit('currentSection', section)

      // Load site information for map.
      _getSite(this.$store, assignment.siteToken)
        .then(function (response) {
          component.onSiteLoaded(response.data)
        }).catch(function (e) {
        })

      // Connect to MQTT broker with current settings.
      this.establishMqttConnection()
    },

    // Called after data is loaded.
    onSiteLoaded: function (site) {
      this.$data.site = site
    },

    // Establish connection with MQTT broker.
    establishMqttConnection: function () {
      var component = this

      let mqttClient = this.$data.mqttClient
      let mqttConnected = this.$data.mqttConnected

      // Kill any existing connection.
      if (mqttClient && mqttConnected) {
        mqttClient.end()
        this.$data.mqttConnected = false
      }

      // Build URL based on form data.
      let url = 'mqtt://' + this.$data.mqttHostname + ':' +
        this.$data.mqttWsPort

      mqttClient = MQTT.connect(url)
      mqttClient.on('connect', function () {
        component.$data.mqttConnected = true
        mqttClient.subscribe(component.$data.mqttTopic)
      })
      this.$data.mqttClient = mqttClient
    }
  }
}
</script>

<style scoped>
</style>
