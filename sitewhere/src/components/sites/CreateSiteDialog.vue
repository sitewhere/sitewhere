<template>
  <div>
    <base-dialog title="Create Site" buttonTooltip="Add Site" width="600"
      @createClicked="onCreateClicked" @cancelClicked="onCancelClicked"
      :visible="dialogVisible">
      <v-tabs light v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider></v-tabs-slider>
          <v-tabs-item key="1" href="#details">
            Site Details
          </v-tabs-item>
          <v-tabs-item key="2" href="#map">
            Map Information
          </v-tabs-item>
          <v-tabs-item key="3" href="#metadata">
            Metadata
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="1" id="details">
          <v-card flat>
            <v-card-row>
              <v-card-text>
                <v-container fluid>
                  <v-layout row wrap>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Site name" v-model="siteName" prepend-icon="info"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" multi-line label="Description" v-model="siteDescription" prepend-icon="subject"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Image URL" v-model="siteImageUrl" prepend-icon="image"></v-text-field>
                    </v-flex>
                  </v-layout>
                </v-container>
              </v-card-text>
            </v-card-row>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="2" id="map">
          <!-- Map Overlay -->
          <v-card flat>
            <!-- Map selector -->
            <v-card-row>
              <v-card-text class="pt-4 pb-4">
                <v-container fluid class="pt-0 pb-0">
                  <v-layout row wrap class="pb-2">
                    <v-flex xs3>
                      <v-subheader class="pt-3">Map Type</v-subheader>
                    </v-flex>
                    <v-flex xs9>
                      <v-select :items="mapTypes" v-model="mapSelection"
                        label="Select Map Type" dark single-line auto
                        prepend-icon="map" hide-details></v-select>
                    </v-flex>
                  </v-layout>
                  <v-divider class="mt-2"></v-divider>
                </v-container>
              </v-card-text>
            </v-card-row>
            <!-- OpenStreetMap Configuration -->
            <v-card-row v-if="mapSelection === 'osm'">
              <v-card-text class="pt-0 pb-0">
                <v-container fluid class="pt-0 pb-0">
                  <v-layout row wrap>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Center Latitude" v-model="mapLatitude" prepend-icon="place"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Center Longitude" v-model="mapLongitude" prepend-icon="place"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Zoom Level" v-model="mapZoom" prepend-icon="zoom_out_map"></v-text-field>
                    </v-flex>
                    <v-flex xs2>
                    </v-flex>
                    <v-flex xs10>
                      <v-btn light @click.native="onShowMapPanel" class="grey darken-1 mt-0 mb-3">
                        <v-icon left light>place</v-icon>
                        Choose Center and Zoom Level on Map
                      </v-btn>
                    </v-flex>
                  </v-layout>
                </v-container>
              </v-card-text>
            </v-card-row>
            <!-- GeoServer Configuration -->
            <v-card-row v-if="mapSelection === 'geo'">
              <v-card-text class="pt-0 pb-0">
                <v-container fluid class="pt-0 pb-0">
                  <v-layout row wrap>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="GeoServer Base URL" v-model="geoBaseUrl" prepend-icon="place"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="GeoServer Layer" v-model="geoLayer" prepend-icon="place"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Center Latitude" v-model="mapLatitude" prepend-icon="place"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Center Longitude" v-model="mapLongitude" prepend-icon="place"></v-text-field>
                    </v-flex>
                    <v-flex xs12>
                      <v-text-field class="mt-1" label="Zoom Level" v-model="mapZoom" prepend-icon="zoom_out_map"></v-text-field>
                    </v-flex>
                    <v-flex xs2>
                    </v-flex>
                    <v-flex xs10>
                      <v-btn light @click.native="onShowMapPanel" class="grey darken-1 mt-0 mb-3">
                        <v-icon left light>place</v-icon>
                        Choose Center and Zoom Level on Map
                      </v-btn>
                    </v-flex>
                  </v-layout>
                </v-container>
              </v-card-text>
            </v-card-row>
          </v-card>
          <v-card id="map-overlay" v-if="mapOverlayShown">
            <div id="app" style="height: 100%">
              <v-map :zoom="mapOverlayZoom" :center="mapOverlayLatLon"
                @l-move="onMapMove" @l-zoom="onMapZoom">
                <v-tilelayer url="http://{s}.tile.osm.org/{z}/{x}/{y}.png"></v-tilelayer>
              </v-map>
            </div>
            <v-btn small floating class="map-overlay-cancel grey"
              v-tooltip:top="{ html: 'Cancel' }" @click.native.stop="onCancelMapPanel">
              <v-icon light>undo</v-icon>
            </v-btn>
            <v-btn small floating class="map-overlay-ok blue"
              v-tooltip:top="{ html: 'Update' }" @click.native.stop="onSubmitMapPanel">
              <v-icon light>done</v-icon>
            </v-btn>
          </v-card>
        </v-tabs-content>
        <v-tabs-content key="3" id="metadata">
          <metadata-panel :metadata="metadata"
            @itemDeleted="onMetadataDeleted" @itemAdded="onMetadataAdded"/>
        </v-tabs-content>
      </v-tabs>
    </base-dialog>
    <v-btn slot="activator" floating class="add-button red darken-1"
      v-tooltip:top="{ html: 'Add Site' }" @click.native.stop="onOpenDialog">
      <v-icon light>add</v-icon>
    </v-btn>
  </div>
</template>

<script>
import BaseDialog from '../common/BaseDialog'
import MetadataPanel from '../common/MetadataPanel'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    siteName: '',
    siteDescription: '',
    siteImageUrl: '',
    mapTypes: [
      {
        'text': 'OpenStreetMap',
        'value': 'osm'
      },
      {
        'text': 'GeoServer Layer',
        'value': 'geo'
      }
    ],
    mapSelection: null,
    mapOverlayShown: false,
    mapOverlayZoom: 13,
    mapOverlayLatLon: [47.413220, -1.219482],
    origLatitude: null,
    origLongitude: null,
    origZoom: null,
    mapLatitude: null,
    mapLongitude: null,
    mapZoom: null,
    geoBaseUrl: null,
    geoLayer: null,
    metadata: []
  }),

  components: {
    BaseDialog,
    MetadataPanel
  },

  created: function () {
    var maps = this.$data.mapTypes
    this.$data.mapSelection = maps[0].value
  },

  watch: {
  },

  methods: {
    // Called to open the dialog.
    onOpenDialog: function (e) {
      this.$data.dialogVisible = true
    },

    // Called after create button is clicked.
    onCreateClicked: function (e) {
      this.$data.dialogVisible = false
    },

    // Called after cancel button is clicked.
    onCancelClicked: function (e) {
      this.$data.dialogVisible = false
    },

    // Called to display map panel.
    onShowMapPanel: function () {
      var lat = this.$data.mapLatitude
      var lng = this.$data.mapLongitude
      var zoom = this.$data.mapZoom
      this.$data.origLatitude = lat
      this.$data.origLongitude = lng
      this.$data.origZoom = zoom

      // Create defaults if fields not populated.
      lat = lat || 33.7490
      lng = lng || -84.3880
      zoom = zoom || 13

      // Save defaults into form.
      this.$data.mapLatitude = lat
      this.$data.mapLongitude = lng
      this.$data.mapZoom = zoom

      // Push values into overlay.
      this.$data.mapOverlayLatLon = [lat, lng]
      this.$data.mapOverlayZoom = zoom
      this.$data.mapOverlayShown = true
    },

    // Called when overlay map is moved.
    onMapMove: function (e) {
      // This should be debounced!
      this.$data.mapLatitude = e.target.getCenter().lat
      this.$data.mapLongitude = e.target.getCenter().lng
    },

    // Called when overlay map is moved.
    onMapZoom: function (e) {
      this.$data.mapZoom = e.target.getZoom()
    },

    // Called to cancel map panel.
    onCancelMapPanel: function () {
      this.$data.mapLatitude = this.$data.origLatitude
      this.$data.mapLongitude = this.$data.origLongitude
      this.$data.mapZoom = this.$data.origZoom
      this.$data.mapOverlayShown = false
    },

    // Called to submit map panel.
    onSubmitMapPanel: function () {
      this.$data.mapOverlayShown = false
    },

    // Called when a metadata entry has been deleted.
    onMetadataDeleted: function (name) {
      var metadata = this.$data.metadata
      for (var i = 0; i < metadata.length; i++) {
        if (metadata[i].name === name) {
          metadata.splice(i, 1)
        }
      }
    },

    // Called when a metadata entry has been added.
    onMetadataAdded: function (entry) {
      var metadata = this.$data.metadata
      metadata.push(entry)
    }
  }
}
</script>

<style scoped>
#map-overlay {
  background-color: #fff;
  position: absolute;
  top: 0px;
  bottom: 0px;
  left: 0px;
  right: 0px;
}

.add-button {
  position: absolute;
  bottom: 16px;
  right: 16px;
}

.map-overlay-ok {
  position: absolute;
  bottom: 16px;
  right: 16px;
  z-index: 1000;
}

.map-overlay-cancel {
  position: absolute;
  bottom: 16px;
  right: 64px;
  z-index: 1000;
}
</style>
