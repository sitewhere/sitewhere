<template>
  <v-card class="deviceType white pa-2">
    <v-card-text>
      <div class="type-logo" :style="logoStyle">
      </div>
      <div class="type-token">
        Token: {{deviceType.token}}
        <v-tooltip left>
          <v-btn style="position: relative;" class="mt-0"
            light icon v-clipboard="copyData" @success="onTokenCopied"
            @error="onTokenCopyFailed" slot="activator">
            <v-icon>fa-clipboard</v-icon>
          </v-btn>
          <span>Copy to Clipboard</span>
        </v-tooltip>
      </div>
      <div class="type-name">{{deviceType.name}}</div>
      <div class="type-desc">{{deviceType.asset.description}}</div>
      <div class="type-right">
        <div class="type-created-label">Created:</div>
        <div class="type-created">{{ formatDate(deviceType.createdDate) }}</div>
        <div class="type-updated-label">Updated:</div>
        <div class="type-updated">{{ formatDate(deviceType.updatedDate) }}</div>
        <device-type-update-dialog :token="deviceType.token" class="type-update"
          @deviceTypeUpdated="onUpdated"></device-type-update-dialog>
        <device-type-delete-dialog :token="deviceType.token" class="type-delete"
          @deviceTypeDeleted="onDeleted"></device-type-delete-dialog>
      </div>
      <div class="type-divider"></div>
    </v-card-text>
    <v-snackbar :timeout="2000" success v-model="showTokenCopied">Token copied to clipboard
      <v-btn dark flat @click.native="showTokenCopied = false">Close</v-btn>
    </v-snackbar>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import DeviceTypeDeleteDialog from './DeviceTypeDeleteDialog'
import DeviceTypeUpdateDialog from './DeviceTypeUpdateDialog'

export default {

  data: () => ({
    copyData: null,
    showTokenCopied: false
  }),

  props: ['deviceType'],

  components: {
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
        'background-image': 'url(' + this.deviceType.assetImageUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%'
      }
    }
  },

  methods: {
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
  min-height: 180px;
  min-width: 800px;
  overflow-y: hidden;
}

.type-logo {
  position: absolute;
  top: 0px;
  left: 0px;
  bottom: 0px;
  width: 140px;
}

.type-name {
  position: absolute;
  top: 5px;
  left: 158px;
  right: 10px;
  font-size: 24px;
  font-weight: 400;
  white-space: nowrap;
  overflow-x: hidden;
}

.type-token {
  position: absolute;
  top: 40px;
  left: 158px;
  right: 300px;
  font-size: 16px;
  white-space: nowrap;
  overflow-x: hidden;
}

.type-desc {
  position: absolute;
  top: 83px;
  left: 160px;
  right: 300px;
  bottom: 10px;
  font-size: 14px;
  overflow-y: hidden;
}

.type-divider {
  position: absolute;
  top: 10px;
  right: 280px;
  bottom: 10px;
  border-left: 1px solid #eee;
}

.type-right {
  position: absolute;
  top: 10px;
  right: 10px;
  bottom: 10px;
  width: 260px;
}

.type-created-label {
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 14px;
}

.type-created {
  position: absolute;
  top: 10px;
  left: 100px;
  font-size: 14px;
  white-space: nowrap;
}

.type-updated-label {
  position: absolute;
  top: 35px;
  left: 10px;
  font-size: 14px;
}

.type-updated {
  position: absolute;
  top: 35px;
  left: 100px;
  font-size: 14px;
  white-space: nowrap;
}

.type-update {
  position: absolute;
  bottom: 0px;
  left: 7px;
}

.type-delete {
  position: absolute;
  bottom: 0px;
  left: 42px;
}
</style>
