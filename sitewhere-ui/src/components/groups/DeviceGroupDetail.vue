<template>
  <div>
    <device-group-detail-header :group="group" class="mb-3">
    </device-group-detail-header>
    <device-group-element-list-panel ref="list" :token="token">
    </device-group-element-list-panel>
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
    <device-group-update-dialog ref="update" :token="token"
      @groupUpdated="refresh">
    </device-group-update-dialog>
    <device-group-element-create-dialog ref="create" :token="token"
      @elementAdded="onElementAdded">
    </device-group-element-create-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import DeviceGroupDetailHeader from './DeviceGroupDetailHeader'
import DeviceGroupElementListPanel from './DeviceGroupElementListPanel'
import DeviceGroupUpdateDialog from './DeviceGroupUpdateDialog'
import DeviceGroupElementCreateDialog from './DeviceGroupElementCreateDialog'
import {_getDeviceGroup} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    results: null,
    paging: null,
    token: null,
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
    DeviceGroupDetailHeader,
    DeviceGroupElementListPanel,
    DeviceGroupUpdateDialog,
    DeviceGroupElementCreateDialog
  },

  // Store group token which is passed in URL.
  created: function () {
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  methods: {
    // Refresh data.
    refresh: function () {
      let component = this
      // Load information.
      _getDeviceGroup(this.$store, this.$data.token)
        .then(function (response) {
          component.onDeviceGroupLoaded(response.data)
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
      this.$refs['list'].refresh()
    }
  }
}
</script>

<style scoped>
</style>
