<template>
  <div>
    <v-container fluid grid-list-md  v-if="elements">
      <v-layout row wrap>
         <v-flex xs6 v-for="(element, index) in elements" :key="element.index">
          <device-group-element-list-panel :element="element" class="mb-1">
          </device-group-element-list-panel>
        </v-flex>
      </v-layout>
    </v-container>
    <pager :results="results" :pageSizes="pageSizes"
      @pagingUpdated="updatePaging">
    </pager>

    <v-speed-dial v-model="fab" direction="top" hover fixed bottom right
      class="action-chooser-fab"
      transition="slide-y-reverse-transition">
      <v-btn slot="activator" class="red darken-1 elevation-5" dark
        fab hover>
        <v-icon fa style="margin-top: -10px;" class="fa-2x">bolt</v-icon>
      </v-btn>
      <v-btn fab dark small class="blue darken-3 elevation-5"
         v-tooltip:left="{ html: 'Update Device Group' }"
          @click.native="onUpdateDeviceGroup">
        <v-icon fa style="margin-top: -3px;">edit</v-icon>
      </v-btn>
      <v-btn fab dark small class="red darken-3 elevation-5"
         v-tooltip:left="{ html: 'Delete Device Group' }"
          @click.native="onDeleteDeviceGroup">
        <v-icon fa style="margin-top: -3px;">remove</v-icon>
      </v-btn>
      <v-btn fab dark small class="green darken-3 elevation-5"
         v-tooltip:left="{ html: 'Add Element' }"
          @click.native="onAddElement">
        <v-icon fa style="margin-top: -3px;">plus</v-icon>
      </v-btn>
    </v-speed-dial>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import DeviceGroupElementListPanel from './DeviceGroupElementListPanel'
import {_getDeviceGroup, _listDeviceGroupElements} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    groupToken: null,
    group: null,
    elements: null,
    pageSizes: [
      {
        text: '20',
        value: 20
      }, {
        text: '50',
        value: 50
      }, {
        text: '100',
        value: 100
      }
    ],
    fab: null
  }),

  components: {
    Pager,
    DeviceGroupElementListPanel
  },

  // Store group token which is passed in URL.
  created: function () {
    this.$data.groupToken = this.$route.params.groupToken
  },

  methods: {
    // Update paging values and run query.
    updatePaging: function (paging) {
      this.$data.paging = paging
      this.refresh()
    },

    // Refresh data.
    refresh: function () {
      let paging = this.$data.paging.query
      let component = this
      // Load information.
      _getDeviceGroup(this.$store, this.$data.categoryId)
        .then(function (response) {
          component.onDeviceGroupLoaded(response.data)
        }).catch(function (e) {
        })
      _listDeviceGroupElements(this.$store, this.$data.categoryId, paging)
        .then(function (response) {
          component.results = response.data
          component.assets = response.data.results
        }).catch(function (e) {
        })
    },

    // Called after device group is loaded.
    onDeviceGroupLoaded: function (group) {
      this.$data.group = group
      var section = {
        id: 'groups',
        title: 'Device Group',
        icon: 'view_module',
        route: '/admin/groups/' + group.token,
        longTitle: 'Manage Device Group: ' + group.token
      }
      this.$store.commit('currentSection', section)
    },

    // Show dialog on update requested.
    onUpdateDeviceGroup: function () {
      this.$refs['update'].onOpenDialog()
    },

    // Show dialog on delete requested.
    onDeleteDeviceGroup: function () {
      this.$refs['delete'].showDeleteDialog()
    },

    // Handle successful delete.
    onDeviceGroupDeleted: function () {
      Utils.routeTo(this, '/assets/categories')
    },

    // Called when 'add element' button is clicked.
    onAddElement: function () {
      this.$refs['create'].onOpenDialog()
    },

    // Called when an element is added.
    onElementAdded: function () {
      this.refresh()
    }
  }
}
</script>

<style scoped>
</style>
