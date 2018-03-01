<template>
  <div>
    <navigation-page v-if="deviceType" icon="fa-cog"
      :title="deviceType.name">
      <div slot="content">
        <device-type-detail-header :deviceType="deviceType"
          @deviceTypeDeleted="onDeleted" @deviceTypeUpdated="onUpdated"
          class="mb-3">
        </device-type-detail-header>
        <v-tabs v-model="active">
          <v-tabs-bar dark color="primary">
            <v-tabs-slider class="blue lighten-3"></v-tabs-slider>
            <v-tabs-item key="commands" href="#commands">
              Commands
            </v-tabs-item>
            <v-tabs-item key="statuses" href="#statuses">
              Device Statuses
            </v-tabs-item>
            <v-tabs-item key="code" href="#code">
              Code Generation
            </v-tabs-item>
            <v-tabs-item v-if="deviceType.containerPolicy === 'Composite'"
              key="composition" href="#composition">
              Composition
            </v-tabs-item>
          </v-tabs-bar>
          <v-tabs-items>
            <v-tabs-content key="commands" id="commands">
              <device-type-commands ref="commands" :deviceType="deviceType">
              </device-type-commands>
            </v-tabs-content>
            <v-tabs-content key="statuses" id="statuses">
              <device-type-device-statuses ref="statuses"
                :deviceType="deviceType">
              </device-type-device-statuses>
            </v-tabs-content>
            <v-tabs-content key="code" id="code">
              <device-type-codegen :deviceType="deviceType">
              </device-type-codegen>
            </v-tabs-content>
            <v-tabs-content v-if="deviceType.containerPolicy === 'Composite'"
              key="composition" id="composition">
              <device-type-composition :deviceType="deviceType">
              </device-type-composition>
            </v-tabs-content>
          </v-tabs-items>
        </v-tabs>
        <command-create-dialog v-if="active === 'commands'"
          :deviceType="deviceType" @commandAdded="onCommandAdded"/>
        <device-status-create-dialog v-if="active === 'statuses'"
          :deviceType="deviceType" @statusAdded="onStatusAdded">
        </device-status-create-dialog>
      </div>
      <div slot="actions">
        <navigation-action-button icon="fa-edit" tooltip="Edit Device Type"
          @action="onEdit">
        </navigation-action-button>
        <navigation-action-button icon="fa-times" tooltip="Delete Device Type"
          @action="onDelete">
        </navigation-action-button>
      </div>
    </navigation-page>
    <device-type-update-dialog ref="edit" :token="token"
      @deviceTypeUpdated="onUpdated"></device-type-update-dialog>
    <device-type-delete-dialog ref="delete" :token="token"
      @deviceTypeDeleted="onDeleted"></device-type-delete-dialog>
  </div>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import NavigationActionButton from '../common/NavigationActionButton'
import DeviceTypeDetailHeader from './DeviceTypeDetailHeader'
import DeviceTypeCommands from './DeviceTypeCommands'
import DeviceTypeDeviceStatuses from './DeviceTypeDeviceStatuses'
import DeviceTypeCodegen from './DeviceTypeCodegen'
import DeviceTypeComposition from './DeviceTypeComposition'
import DeviceTypeDeleteDialog from './DeviceTypeDeleteDialog'
import DeviceTypeUpdateDialog from './DeviceTypeUpdateDialog'
import CommandCreateDialog from './CommandCreateDialog'
import DeviceStatusCreateDialog from './DeviceStatusCreateDialog'

import {_getDeviceType} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    token: null,
    deviceType: null,
    protobuf: null,
    active: null
  }),

  components: {
    NavigationPage,
    NavigationActionButton,
    DeviceTypeDetailHeader,
    DeviceTypeCommands,
    DeviceTypeDeviceStatuses,
    DeviceTypeCodegen,
    DeviceTypeComposition,
    DeviceTypeDeleteDialog,
    DeviceTypeUpdateDialog,
    CommandCreateDialog,
    DeviceStatusCreateDialog
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
    // Called to refresh device type data.
    refresh: function () {
      var token = this.$data.token
      var component = this

      // Load device type information.
      _getDeviceType(this.$store, token)
        .then(function (response) {
          component.onLoaded(response.data)
        }).catch(function (e) {
        })
    },
    // Called after data is loaded.
    onLoaded: function (deviceType) {
      this.$data.deviceType = deviceType
      var section = {
        id: 'devicetypes',
        title: 'Device Types',
        icon: 'map',
        route: '/admin/devicetypes/' + deviceType.token,
        longTitle: 'Manage Device Type: ' + deviceType.name
      }
      this.$store.commit('currentSection', section)
    },
    // Called to open area edit dialog.
    onEdit: function () {
      this.$refs['edit'].onOpenDialog()
    },
    // Called after update.
    onUpdated: function () {
      this.refresh()
    },
    onDelete: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called after delete.
    onDeleted: function () {
      Utils.routeTo(this, '/devicetypes')
    },
    // Called after a command is added.
    onCommandAdded: function () {
      this.$refs['commands'].refresh()
    },
    // Called after a status is added.
    onStatusAdded: function () {
      this.$refs['statuses'].refresh()
    }
  }
}
</script>

<style scoped>
</style>
