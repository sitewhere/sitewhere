<template>
  <v-card class="device white pa-2">
    <div :style="assignmentStyle"></div>
    <v-card-text>
      <span class="device-logo" :style="logoStyle"></span>
      <span class="device-qrcode" :style="qrCodeStyle"></span>
      <div class="device-headers">
        <header-field label="Token">
          <clipboard-copy-field :field="device.token"
            message="Token copied to clipboard">
          </clipboard-copy-field>
        </header-field>
        <linked-header-field label="Device Type"
          :text="device.deviceType.name"
          :url="'/devicetypes/' + device.deviceType.token">
        </linked-header-field>
        <linked-header-field v-if="device.assignment" label="Assignment"
          :text="device.assignment.assetName"
          :url="'/assignments/' + device.assignment.token">
        </linked-header-field>
        <header-field v-else label="Assignment">
          <span>Device is not assigned</span>
        </header-field>
        <header-field label="Created">
          <span>{{ formatDate(device.createdDate) }}</span>
        </header-field>
        <header-field label="Updated">
          <span>{{ formatDate(device.updatedDate) }}</span>
        </header-field>
      </div>
      <options-menu class="options-menu">
        <v-list slot="options">
          <v-list-tile>
            <v-btn block class="blue white--text" @click="onEditDevice">
              Edit Device
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-edit</v-icon>
            </v-btn>
          </v-list-tile>
          <v-list-tile>
            <v-btn block class="red darken-2 white--text" @click="onDeleteDevice">
              Delete Device
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-times</v-icon>
            </v-btn>
          </v-list-tile>
        </v-list>
      </options-menu>
    </v-card-text>
    <device-update-dialog ref="update" :hardwareId="device.hardwareId">
    </device-update-dialog>
    <device-delete-dialog ref="delete" :hardwareId="device.hardwareId"
      @deviceDeleted="onDeviceDeleted">
    </device-delete-dialog>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import Style from '../common/Style'
import HeaderField from '../common/HeaderField'
import LinkedHeaderField from '../common/LinkedHeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'
import OptionsMenu from '../common/OptionsMenu'
import DeviceUpdateDialog from './DeviceUpdateDialog'
import DeviceDeleteDialog from './DeviceDeleteDialog'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    copyData: null,
    showIdCopied: false
  }),

  props: ['device'],

  components: {
    HeaderField,
    LinkedHeaderField,
    ClipboardCopyField,
    OptionsMenu,
    DeviceUpdateDialog,
    DeviceDeleteDialog
  },

  computed: {
    // Compute card style based on assignment status.
    assignmentStyle: function () {
      let style = Style.styleForAssignmentStatus(this.device.assignment)
      style['position'] = 'absolute'
      style['left'] = 0
      style['right'] = 0
      style['top'] = 0
      style['bottom'] = 0
      return style
    },

    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-color': '#fff',
        'background-image': 'url(' + this.device.deviceType.assetType.imageUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    },

    // Compute style for QR code URL.
    qrCodeStyle: function () {
      var tenant = this.$store.getters.selectedTenant
      return {
        'background-color': '#fff',
        'background-image': 'url(' + createCoreApiUrl(this.$store) + 'devices/' +
          this.device.hardwareId + '/symbol?tenantAuthToken=' +
          tenant.authenticationToken + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    }
  },

  created: function () {
    this.$data.copyData = this.device.hardwareId
  },

  methods: {
    // Open dialog to edit device.
    onEditDevice: function () {
      this.$refs['update'].onOpenDialog()
    },
    // Open dialog to delete device.
    onDeleteDevice: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    onDeviceDeleted: function () {
      this.$emit('deviceDeleted')
    },
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.device {
  min-height: 200px;
  min-width: 920px;
  overflow-y: hidden;
}

.device-logo {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.device-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
}

.device-headers {
  position: absolute;
  top: 20px;
  left: 200px;
  right: 200px;
}

.options-menu {
  position: absolute;
  top: 10px;
  right: 190px;
}
</style>
