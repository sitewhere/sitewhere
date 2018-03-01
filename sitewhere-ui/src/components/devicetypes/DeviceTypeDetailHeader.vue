<template>
  <v-card class="deviceType white pa-2">
    <v-card-text>
      <span class="type-logo" :style="logoStyle"></span>
      <span class="type-qrcode" :style="qrCodeStyle"></span>
      <div class="type-headers">
        <header-field label="Token">
          <clipboard-copy-field :field="deviceType.token"
            message="Token copied to clipboard">
          </clipboard-copy-field>
        </header-field>
        <header-field label="Name">
          <span>{{ deviceType.name }}</span>
        </header-field>
        <header-field label="Description">
          <span>{{ deviceType.description }}</span>
        </header-field>
        <header-field label="Image URL">
          <span>{{ deviceType.imageUrl }}</span>
        </header-field>
        <header-field label="Container Policy">
          <span>{{ deviceType.containerPolicy }}</span>
        </header-field>
        <header-field label="Created">
          <span>{{ formatDate(deviceType.createdDate) }}</span>
        </header-field>
        <header-field label="Updated">
          <span>{{ formatDate(deviceType.updatedDate) }}</span>
        </header-field>
      </div>
      <options-menu class="options-menu">
        <v-list slot="options">
          <v-list-tile>
            <v-btn block class="blue white--text" @click="onEditDeviceType">
              Edit Device Type
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-edit</v-icon>
            </v-btn>
          </v-list-tile>
          <v-list-tile>
            <v-btn block class="red darken-2 white--text" @click="onDeleteDeviceType">
              Delete Device Type
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-times</v-icon>
            </v-btn>
          </v-list-tile>
        </v-list>
      </options-menu>
      <device-type-update-dialog ref="update" :token="deviceType.token"
        @deviceTypeUpdated="onUpdated"></device-type-update-dialog>
      <device-type-delete-dialog ref="delete" :token="deviceType.token"
        @deviceTypeDeleted="onDeleted"></device-type-delete-dialog>
    </v-card-text>
    <v-snackbar :timeout="2000" success v-model="showTokenCopied">Token copied to clipboard
      <v-btn dark flat @click.native="showTokenCopied = false">Close</v-btn>
    </v-snackbar>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'
import DeviceTypeDeleteDialog from './DeviceTypeDeleteDialog'
import DeviceTypeUpdateDialog from './DeviceTypeUpdateDialog'
import ClipboardCopyField from '../common/ClipboardCopyField'
import OptionsMenu from '../common/OptionsMenu'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    copyData: null,
    showTokenCopied: false
  }),

  props: ['deviceType'],

  components: {
    HeaderField,
    ClipboardCopyField,
    OptionsMenu,
    DeviceTypeDeleteDialog,
    DeviceTypeUpdateDialog
  },

  created: function () {
    this.$data.copyData = this.deviceType.token
  },

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-image': 'url(' + this.deviceType.imageUrl + ')',
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
        'background-image': 'url(' + createCoreApiUrl(this.$store) + 'devicetypes/' +
          this.deviceType.token + '/symbol?tenantAuthToken=' +
          tenant.authenticationToken + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    }
  },

  methods: {
    // Open dialog to edit device type.
    onEditDeviceType: function () {
      this.$refs['update'].onOpenDialog()
    },
    // Open dialog to delete device type.
    onDeleteDeviceType: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when deleted.
    onDeleted: function () {
      this.$emit('deviceTypeDeleted')
    },
    // Called when updated.
    onUpdated: function () {
      this.$emit('deviceTypeUpdated')
    },
    // Called after token is copied.
    onTokenCopied: function (e) {
      this.$data.showTokenCopied = true
    },
    // Called if unable to copy token.
    onTokenCopyFailed: function (e) {
      console.log('Token copy failed.')
    },
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.deviceType {
  min-height: 225px;
  min-width: 920px;
  overflow-y: hidden;
}

.type-logo {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.type-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
}

.type-headers {
  position: absolute;
  top: 20px;
  left: 190px;
  right: 190px;
}

.options-menu {
  position: absolute;
  top: 10px;
  right: 190px;
}
</style>
