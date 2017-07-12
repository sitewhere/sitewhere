<template>
  <div>
    <v-layout row wrap v-if="namespaces">
      <v-flex xs12>
        <no-results-panel v-if="namespaces.length === 0"
          text="No Commands Found for Device Specification">
        </no-results-panel>
        <div v-if="namespaces.length > 0">
          <namespace-panel :namespace="namespace" v-for="namespace in namespaces" :key="namespace.value">
          </namespace-panel>
        </div>
      </v-flex>
    </v-layout>
  </div>
</template>

<script>
import NoResultsPanel from '../common/NoResultsPanel'
import NamespacePanel from './NamespacePanel'
import {_listSpecificationCommandsByNamespace} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    namespaces: null
  }),

  props: ['specification'],

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
      _listSpecificationCommandsByNamespace(this.$store, this.specification.token, false)
        .then(function (response) {
          component.namespaces = response.data.results
        }).catch(function (e) {
        })
    }
  }
}
</script>

<style scoped>
</style>
