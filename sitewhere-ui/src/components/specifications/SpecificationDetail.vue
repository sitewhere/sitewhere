<template>
  <div v-if="specification">
    <v-app>
      <specification-detail-header :specification="specification"
        @specificationDeleted="onDeleted" @specificationUpdated="onUpdated"
        class="mb-3">
      </specification-detail-header>
      <v-tabs class="elevation-2" dark v-model="active">
        <v-tabs-bar slot="activators" class="blue darken-2">
          <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
          <v-tabs-item key="commands" href="#commands">
            Commands
          </v-tabs-item>
          <v-tabs-item key="code" href="#code">
            Code Generation
          </v-tabs-item>
        </v-tabs-bar>
        <v-tabs-content key="commands" id="commands">
          <specification-commands ref="commands" :specification="specification">
          </specification-commands>
        </v-tabs-content>
        <v-tabs-content key="code" id="code">
          <specification-codegen :specification="specification">
          </specification-codegen>
        </v-tabs-content>
      </v-tabs>
      <command-create-dialog v-if="active === 'commands'"
        :specification="specification" @commandAdded="onCommandAdded"/>
    </v-app>
  </div>
</template>

<script>
import SpecificationDetailHeader from './SpecificationDetailHeader'
import SpecificationCommands from './SpecificationCommands'
import SpecificationCodegen from './SpecificationCodegen'
import CommandCreateDialog from './CommandCreateDialog'

import {_getDeviceSpecification} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    specification: null,
    protobuf: null,
    active: null
  }),

  components: {
    SpecificationDetailHeader,
    SpecificationCommands,
    SpecificationCodegen,
    CommandCreateDialog
  },

  created: function () {
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  methods: {
    // Called to refresh specification data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load specification information.
      _getDeviceSpecification(this.$store, token)
        .then(function (response) {
          component.onLoaded(response.data)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (specification) {
      this.$data.specification = specification
      var section = {
        id: 'specifications',
        title: 'Specifications',
        icon: 'map',
        route: '/admin/specifications/' + specification.token,
        longTitle: 'Manage Specification: ' + specification.name
      }
      this.$store.commit('currentSection', section)
    },

    // Called after delete.
    onDeleted: function () {
      var tenant = this.$store.getters.selectedTenant
      if (tenant) {
        this.$router.push('/admin/' + tenant.id + '/specifications')
      }
    },

    // Called after update.
    onUpdated: function () {
      this.refresh()
    },

    // Called after a command is added.
    onCommandAdded: function () {
      console.log('command added')
      this.$refs['commands'].refresh()
    }
  }
}
</script>

<style scoped>
.add-button {
  position: fixed;
  right: 16px;
  bottom: 16px;
}
</style>
