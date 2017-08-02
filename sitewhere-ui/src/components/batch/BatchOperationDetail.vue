<template>
  <div v-if="operation">
    <v-app>
      <batch-operation-detail-header :operation="operation" class="mb-3">
      </batch-operation-detail-header>
      <v-tabs class="elevation-2" dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
          <v-tabs-item key="elements" href="#elements">
            Batch Operation Elements
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="elements" id="elements">
          <batch-operation-elements-list :token="token">
          </batch-operation-elements-list>
        </v-tabs-content>
      </v-tabs>
    </v-app>
  </div>
</template>

<script>
import BatchOperationDetailHeader from './BatchOperationDetailHeader'
import BatchOperationElementsList from './BatchOperationElementsList'

import {_getBatchOperation} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    operation: null,
    active: null
  }),

  components: {
    BatchOperationDetailHeader,
    BatchOperationElementsList
  },

  created: function () {
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  methods: {
    // Called to refresh data.
    refresh: function () {
      var component = this

      // Load information.
      _getBatchOperation(this.$store, this.token)
        .then(function (response) {
          component.onLoaded(response.data)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (operation) {
      this.$data.operation = operation
      var section = {
        id: 'batch',
        title: 'Batch',
        icon: 'group_work',
        route: '/admin/batch/' + operation.token,
        longTitle: 'Manage Batch Operation: ' + operation.token
      }
      this.$store.commit('currentSection', section)
    }
  }
}
</script>

<style scoped>
</style>
