<template>
  <div>
    <device-unit-panel :deviceUnit="schema" @contentUpdated="onContentUpdated">
    </device-unit-panel>
    <v-snackbar :timeout="2000" success v-model="showSaved">Changes Saved
      <v-btn dark flat @click.native="showSaved = false">Close</v-btn>
    </v-snackbar>
  </div>
</template>

<script>
import DeviceUnitPanel from './DeviceUnitPanel'
import {_updateDeviceType} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    showSaved: false,
    schema: null
  }),

  props: ['deviceType'],

  components: {
    DeviceUnitPanel
  },

  created: function () {
    this.$data.schema = this.deviceType.deviceElementSchema
  },

  methods: {
    // Refresh UI when content updates.
    onContentUpdated: function () {
      var component = this
      _updateDeviceType(this.$store, this.deviceType.token,
        this.deviceType)
        .then(function (response) {
          component.onContentSaved()
        }).catch(function (e) {
        })
    },

    // Called after content has been saved.
    onContentSaved: function () {
      this.$data.showSaved = true
      this.$data.schema = this.deviceType.deviceElementSchema
    }
  }
}
</script>

<style scoped>
</style>
