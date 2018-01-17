<template>
  <v-card class="device white pa-2">
    <div :style="assignmentStyle"></div>
    <v-card-text>
      <span class="device-logo" :style="logoStyle"></span>
      <span class="device-qrcode" :style="qrCodeStyle"></span>
      <div class="device-headers">
        <header-field label="Hardware Id">
          <clipboard-copy-field :field="device.hardwareId"
            message="Hardware id copied to clipboard">
          </clipboard-copy-field>
        </header-field>
        <header-field label="Device Specification">
          <span>{{ device.specification.assetName }}</span>
        </header-field>
        <header-field label="Site">
          <span>{{ device.site.name }}</span>
        </header-field>
        <header-field label="Assignment">
          <span v-if="device.assignment">{{ device.assignment.assetName }}</span>
          <span v-else>Device is not assigned</span>
        </header-field>
        <header-field label="Created">
          <span>{{ formatDate(device.createdDate) }}</span>
        </header-field>
        <header-field label="Updated">
          <span>{{ formatDate(device.updatedDate) }}</span>
        </header-field>
      </div>
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import Style from '../common/Style'
import HeaderField from '../common/HeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    copyData: null,
    showIdCopied: false
  }),

  props: ['device'],

  components: {
    HeaderField,
    ClipboardCopyField
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
        'background-image': 'url(' + this.device.specification.asset.imageUrl + ')',
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
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
.device {
  min-height: 210px;
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
</style>
