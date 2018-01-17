<template>
  <v-app>
    <unsaved-updates-warning class="mb-3" :unsaved="dirty"
      @save="onSaveConfiguration" @revert="onRevertConfiguration">
    </unsaved-updates-warning>
    <microservice-editor :config="config" :configModel="configModel"
      :identifier="identifier" @dirty="onConfigurationUpdated">
    </microservice-editor>
  </v-app>
</template>

<script>
import MicroserviceEditor from '../microservice/MicroserviceEditor'
import UnsavedUpdatesWarning from '../microservice/UnsavedUpdatesWarning'
import {
  _getConfigurationModel,
  _getGlobalConfiguration,
  _updateGlobalConfiguration
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    identifier: null,
    config: null,
    configModel: null,
    dirty: false
  }),

  components: {
    MicroserviceEditor,
    UnsavedUpdatesWarning
  },

  created: function () {
    this.$data.identifier = this.$route.params.identifier
    this.refresh()
  },

  methods: {
    // Called to refresh data.
    refresh: function () {
      // Load configuration data.
      var component = this
      _getConfigurationModel(this.$store, this.$data.identifier)
        .then(function (response) {
          component.$data.configModel = response.data
          var microservice = response.data.microserviceDetails
          var section = {
            id: 'global',
            title: 'Configure Microservice',
            icon: 'language',
            route: '/system/microservices/' + microservice.identifier,
            longTitle: 'Configure Global Microservice: ' + microservice.name
          }
          component.$store.commit('currentSection', section)
        }).catch(function (e) {
        })
      _getGlobalConfiguration(this.$store, this.$data.identifier)
        .then(function (response) {
          component.$data.config = response.data
        }).catch(function (e) {
        })
    },

    // Called when configuration is changed.
    onConfigurationUpdated: function () {
      this.$data.dirty = true
    },

    // Called when configuration is to be saved.
    onSaveConfiguration: function () {
      var component = this
      _updateGlobalConfiguration(this.$store, this.$data.identifier,
        this.$data.config)
        .then(function (response) {
          component.$data.dirty = false
        }).catch(function (e) {
        })
    },

    // Called when configuration is to be reverted.
    onRevertConfiguration: function () {
      this.refresh()
      this.$data.dirty = false
    },

    // Navigate back to microservices list.
    onBackToList: function () {
      this.$router.push('/system/microservices')
    }
  }
}
</script>

<style>
</style>
