<template>
  <div>
    <v-layout row wrap v-if="namespaces">
      <v-flex xs12>
        <no-results-panel v-if="namespaces.length === 0"
          text="No Commands Found for Device Specification">
        </no-results-panel>
        <div v-if="namespaces.length > 0">
          <namespace-panel :namespace="namespace"
            @commandDeleted="onCommandDeleted" @commandUpdated="onCommandUpdated"
            v-for="namespace in namespaces" :key="namespace.value">
          </namespace-panel>
        </div>
      </v-flex>
    </v-layout>
  </div>
</template>

<script>
import NoResultsPanel from '../common/NoResultsPanel'
import NamespacePanel from './NamespacePanel'
import {_listDeviceCommandsByNamespace} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    namespaces: null
  }),

  props: ['deviceType'],

  components: {
    NoResultsPanel,
    NamespacePanel
  },

  created: function () {
    this.refresh()
  },

  methods: {
    // Refresh list of assignments.
    refresh: function () {
      var component = this
      _listDeviceCommandsByNamespace(this.$store, this.deviceType.token, false)
        .then(function (response) {
          component.namespaces = response.data.results
        }).catch(function (e) {
        })
    },

    // Called after a command has been deleted.
    onCommandDeleted: function () {
      this.refresh()
    },

    // Called after a command has been updated.
    onCommandUpdated: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
