<template>
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
  </navigation-page>
</template>

<script>
import Utils from '../common/Utils'
import NavigationPage from '../common/NavigationPage'
import DeviceTypeDetailHeader from './DeviceTypeDetailHeader'
import DeviceTypeCommands from './DeviceTypeCommands'
import DeviceTypeDeviceStatuses from './DeviceTypeDeviceStatuses'
import DeviceTypeCodegen from './DeviceTypeCodegen'
import DeviceTypeComposition from './DeviceTypeComposition'
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
    DeviceTypeDetailHeader,
    DeviceTypeCommands,
    DeviceTypeDeviceStatuses,
    DeviceTypeCodegen,
    DeviceTypeComposition,
    CommandCreateDialog,
    DeviceStatusCreateDialog
  },

  created: function () {
    this.$data.token = this.$route.params.token
    this.refresh()
  },

  methods: {
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

    // Called after delete.
    onDeleted: function () {
      Utils.routeTo(this, '/devicetypes')
    },

    // Called after update.
    onUpdated: function () {
      this.refresh()
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
