<template>
  <navigation-page icon="view_module" title="Manage Device Group">
    <div slot="content">
      <device-group-detail-header :group="group" class="mb-3"
        @deviceGroupUpdated="refresh" @deviceGroupDeleted="onDeviceGroupDeleted">
      </device-group-detail-header>
      <device-group-element-list-panel ref="list" :token="token">
      </device-group-element-list-panel>
      <floating-action-button label="Add Group Element" icon="fa-plus"
        @action="onAddElement">
      </floating-action-button>
      <device-group-element-create-dialog ref="create" :token="token"
        @elementAdded="onElementAdded">
      </device-group-element-create-dialog>
    </div>
  </navigation-page>
</template>

<script>
import NavigationPage from '../common/NavigationPage'
import Utils from '../common/Utils'
import Pager from '../common/Pager'
import FloatingActionButton from '../common/FloatingActionButton'
import DeviceGroupDetailHeader from './DeviceGroupDetailHeader'
import DeviceGroupElementListPanel from './DeviceGroupElementListPanel'
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
    NavigationPage,
    Pager,
    FloatingActionButton,
    DeviceGroupDetailHeader,
    DeviceGroupElementListPanel,
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

    // Handle successful delete.
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
