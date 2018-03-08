<template>
  <navigation-page icon="fa-list-alt" title="Manage Batch Operation">
    <div v-if="operation" slot="content">
      <batch-operation-detail-header :operation="operation" class="mb-3">
      </batch-operation-detail-header>
      <v-tabs v-model="active">
        <v-tabs-bar dark color="primary">
          <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
          <v-tabs-item key="elements" href="#elements">
            Batch Operation Elements
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-items>
          <v-tabs-content key="elements" id="elements">
            <batch-operation-elements-list :token="token">
            </batch-operation-elements-list>
          </v-tabs-content>
        </v-tabs-items>
      </v-tabs>
    </div>
  </navigation-page>
</template>

<script>
import NavigationPage from '../common/NavigationPage'
import BatchOperationDetailHeader from './BatchOperationDetailHeader'
import BatchOperationElementsList from './BatchOperationElementsList'

import {
  _getBatchOperation
} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    operation: null,
    active: null
  }),

  components: {
    NavigationPage,
    BatchOperationDetailHeader,
    BatchOperationElementsList
  },

  // Called on initial create.
  created: function () {
    this.display(this.$route.params.token)
  },

  // Called when component is reused.
  beforeRouteUpdate (to, from, next) {
    this.display(to.params.token)
    next()
  },

  methods: {
    // Display entity with the given token.
    display: function (token) {
      this.$data.token = token
      this.refresh()
    },
    // Called to refresh data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load information.
      _getBatchOperation(this.$store, token)
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
