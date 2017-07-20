<template>
  <div v-if="assignment">
    <v-app>
      <assignment-list-panel :assignment="assignment" headerMode="false"
        class="mb-3">
      </assignment-list-panel>
      <v-card>
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
              <v-icon class="white--text mr-2" fa>plug</v-icon>
              MQTT Connected
            </v-btn>
          </v-tabs-bar>
          <v-tabs-content key="emulator" id="emulator">
            <assignment-emulator-map ref="map" :assignment="assignment"
              height="600px" @location="onLocationClicked">
            </assignment-emulator-map>
            <v-speed-dial v-model="fab" direction="top" :hover="true"
              class="action-chooser-fab"
              transition="slide-y-reverse-transition">
              <v-btn slot="activator" class="blue darken-3 elevation-5" dark
                fab hover>
                <v-icon fa style="margin-top: -10px;" class="fa-2x">bolt</v-icon>
              </v-btn>
              <v-btn fab dark small class="green darken-3 elevation-5"
                 v-tooltip:left="{ html: 'Pan to Last Location' }"
                  @click.native="onPanToLastLocation">
                <v-icon fa style="margin-top: -3px;">crosshairs</v-icon>
              </v-btn>
              <v-btn fab dark small class="green darken-3 elevation-5"
                 v-tooltip:left="{ html: 'Add Location' }"
                 @click.native="onEnterAddLocationMode">
                <v-icon>room</v-icon>
              </v-btn>
              <v-btn fab dark small class="blue darken-3 elevation-5"
                v-tooltip:left="{ html: 'Add Measurements' }"
                 @click.native="onAddMeasurementsClicked">
                <v-icon fa style="margin-top: -3px;">thermometer</v-icon>
              </v-btn>
              <v-btn fab dark small class="red darken-3 elevation-5"
                v-tooltip:left="{ html: 'Add Alert' }">
                <v-icon>warning</v-icon>
              </v-btn>
            </v-speed-dial>
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
      <location-create-dialog ref="locationCreate" :token="token"
        @locationAdded="onLocationAdded">
      </location-create-dialog>
      <measurements-create-dialog ref="mxCreate" :token="token"
        @locationAdded="onMeasurementsAdded">
      </measurements-create-dialog>
    </v-app>
  </div>
</template>

<script>
import MQTT from 'mqtt'
import AssignmentListPanel from './AssignmentListPanel'
import AssignmentEmulatorMap from './AssignmentEmulatorMap'
import LocationCreateDialog from './LocationCreateDialog'
import MeasurementsCreateDialog from './MeasurementsCreateDialog'
import {_getDeviceAssignment} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    assignment: null,
    mqttClient: null,
    mqttHostname: 'localhost',
    mqttWsPort: 61623,
    mqttTopic: 'SiteWhere/input/jsonbatch',
    mqttConnected: false,
    swUsername: 'admin',
    swPassword: 'password',
    active: null,
    mapVisible: true,
    fab: null
  }),

  components: {
    AssignmentListPanel,
    AssignmentEmulatorMap,
    LocationCreateDialog,
    MeasurementsCreateDialog
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
    // Get map reference.
    getMap: function () {
      return this.$refs.map
    },

    // Get location create dialog reference.
    getLocationCreateDialog: function () {
      return this.$refs.locationCreate
    },

    // Get location create dialog reference.
    getMeasurementsCreateDialog: function () {
      return this.$refs.mxCreate
    },

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
      this.$data.assignment = assignment
      var section = {
        id: 'emulator',
        title: 'Assignment Emulator',
        icon: 'link',
        route: '/admin/assignments/' + assignment.token + '/emulator',
        longTitle: 'Emulate Assignment: ' + assignment.token
      }
      this.$store.commit('currentSection', section)

      // Connect to MQTT broker with current settings.
      this.establishMqttConnection()
    },

    // Called when a map location is clicked.
    onLocationClicked: function (e) {
      let payload = {
        latitude: e.latlng.lat,
        longitude: e.latlng.lng,
        elevation: 0.0
      }
      this.getLocationCreateDialog().onOpenDialog()
      this.getLocationCreateDialog().load(payload)
    },

    // Called after a location has been added.
    onLocationAdded: function () {
      var component = this

      // Wait for data to become available.
      setTimeout(function () {
        component.getMap().refreshLocations()
      }, 1000)
    },

    // Called after measurements have been added.
    onMeasurementsAdded: function () {
    },

    // Asks map to pan to last recorded location.
    onPanToLastLocation: function () {
      this.getMap().panToLastLocation()
    },

    // Puts map in mode for adding location.
    onEnterAddLocationMode: function () {
      this.getMap().enterAddLocationMode()
    },

    // Called when button for adding mx is clicked.
    onAddMeasurementsClicked: function () {
      this.getMeasurementsCreateDialog().onOpenDialog()
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
.action-chooser-fab {
  position: absolute;
  bottom: 48px;
  right: 48px;
  z-index: 1000;
}
</style>
