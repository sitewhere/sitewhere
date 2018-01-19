<template>
  <div v-if="topology">
    <v-toolbar flat card dark color="primary" v-if="topology">
      <v-toolbar-title>Global Microservices</v-toolbar-title>
    </v-toolbar>
    <microservice-list :topology="topology"
      @microserviceClicked="onMicroserviceClicked">
    </microservice-list>
  </div>
</template>

<script>
import MicroserviceList from '../microservice/MicroserviceList'
import {
  _getGlobalTopology
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    topology: null
  }),

  components: {
    MicroserviceList
  },

  created: function () {
    this.refresh()
  },

  methods: {
    // Called if a microservice is clicked.
    onMicroserviceClicked: function (microservice) {
      this.$router.push('/system/microservices/' + microservice.identifier)
    },

    // Called to refresh data.
    refresh: function () {
      // Load configuration data.
      var component = this
      _getGlobalTopology(this.$store)
        .then(function (response) {
          component.$data.topology = response.data
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (tenant) {
      var section = {
        id: 'global',
        title: 'Global Microservices',
        icon: 'layers',
        route: '/system/microservices',
        longTitle: 'Manage Global Microservices'
      }
      this.$store.commit('currentSection', section)
    }
  }
}
</script>

<style>
</style>
