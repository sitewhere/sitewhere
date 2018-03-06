<template>
  <div>
    <navigation-page icon="view_module" title="Manage Device Group">
      <div slot="content">
        <device-group-detail-header :group="group"
          @deviceGroupUpdated="refresh" @deviceGroupDeleted="onDeviceGroupDeleted">
        </device-group-detail-header>
        <v-tabs v-model="active">
          <v-tabs-bar dark color="primary">
            <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
            <v-tabs-item key="elements" href="#elements">
              Group Elements
            </v-tabs-item>
          </v-tabs-bar>
          <v-tabs-items>
            <v-tabs-content key="elements" id="elements">
              <device-group-element-list-panel ref="list" :token="token">
              </device-group-element-list-panel>
              <floating-action-button label="Add Group Element" icon="fa-plus"
                @action="onAddElement">
              </floating-action-button>
            </v-tabs-content>
          </v-tabs-items>
        </v-tabs>
        <device-group-element-create-dialog ref="create" :token="token"
          @elementAdded="onElementAdded">
        </device-group-element-create-dialog>
      </div>
      <div slot="actions">
        <navigation-action-button icon="fa-edit" tooltip="Edit Device Group"
          @action="onEdit">
        </navigation-action-button>
        <navigation-action-button icon="fa-times" tooltip="Delete Device Group"
          @action="onDelete">
        </navigation-action-button>
      </div>
    </navigation-page>
    <device-group-update-dialog ref="edit" :token="token"
      @groupUpdated="onDeviceGroupUpdated">
    </device-group-update-dialog>
    <device-group-delete-dialog ref="delete" :token="token"
      @groupDeleted="onDeviceGroupDeleted">
    </device-group-delete-dialog>
  </div>
</template>

<script>
import NavigationPage from '../common/NavigationPage'
import NavigationActionButton from '../common/NavigationActionButton'
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import FloatingActionButton from '../common/FloatingActionButton'
import DeviceGroupDetailHeader from './DeviceGroupDetailHeader'
import DeviceGroupUpdateDialog from './DeviceGroupUpdateDialog'
import DeviceGroupDeleteDialog from './DeviceGroupDeleteDialog'
import DeviceGroupElementListPanel from './DeviceGroupElementListPanel'
import DeviceGroupElementCreateDialog from './DeviceGroupElementCreateDialog'
import {_getDeviceGroup} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    active: null,
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
    NavigationPage,
    NavigationActionButton,
    Pager,
    FloatingActionButton,
    DeviceGroupDetailHeader,
    DeviceGroupUpdateDialog,
    DeviceGroupDeleteDialog,
    DeviceGroupElementListPanel,
    DeviceGroupElementCreateDialog
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
    onEdit: function () {
      this.$refs['edit'].onOpenDialog()
    },
    // Called after device group is updated.
    onDeviceGroupUpdated: function () {
      this.refresh()
    },
    // Show dialog on delete requested.
    onDelete: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called after device group is deleted.
    onDeviceGroupDeleted: function () {
      Utils.routeTo(this, '/groups')
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
