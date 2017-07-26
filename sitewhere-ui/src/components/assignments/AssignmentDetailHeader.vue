<template>
  <v-card class="assn white pa-2">
    <div class="assn-styling" :style="assignmentStyle"></div>
    <v-card-text>
      <span class="assn-logo" :style="logoStyle"></span>
      <span class="assn-qrcode" :style="qrCodeStyle"></span>
      <v-btn small @click.native="onOpenEmulator"
        class="green white--text assn-emulator" >
        <v-icon left dark>gps_fixed</v-icon>
        Emulator
      </v-btn>
      <span class="assn-token-label assn-label">Token:</span>
      <span class="assn-token assn-field">
        {{assignment.token}}
        <v-btn style="position: relative;"
          v-tooltip:left="{ html: 'Copy to Clipboard' }" class="mt-0"
          light icon v-clipboard="copyData"
          @success="onTokenCopied" @error="onTokenCopyFailed">
          <v-icon fa class="fa-lg">clipboard</v-icon>
        </v-btn>
      </span>
      <span class="assn-asset">{{assignment.assetName}}</span>
      <span class="assn-device-label assn-label">Device:</span>
      <span class="assn-device assn-field">{{assignment.device.assetName}}</span>
      <span class="assn-created-label assn-label">Created:</span>
      <span class="assn-created assn-field">{{ formatDate(assignment.createdDate) }}</span>
      <span class="assn-updated-label assn-label">Updated:</span>
      <span class="assn-updated assn-field">{{ formatDate(assignment.updatedDate) }}</span>
      <span class="assn-active-label assn-label">Active:</span>
      <span class="assn-active assn-field">{{ formatDate(assignment.activeDate) }}</span>
      <span class="assn-released-label assn-label">Released:</span>
      <span class="assn-released assn-field">{{ formatDate(assignment.releasedDate) }}</span>
      <assignment-delete-dialog :token="assignment.token" class="assn-delete"
        @assignmentDeleted="onAssignmentDeleted">
      </assignment-delete-dialog>
    </v-card-text>
    <v-snackbar :timeout="2000" success v-model="showTokenCopied">Token copied to clipboard
      <v-btn dark flat @click.native="showTokenCopied = false">Close</v-btn>
    </v-snackbar>
  </v-card>
</template>

<script>
import Utils from '../common/utils'
import Style from '../common/style'
import {BASE_URL} from '../../http/sitewhere-api'
import AssignmentDeleteDialog from './AssignmentDeleteDialog'

export default {

  data: () => ({
    copyData: null,
    showTokenCopied: false
  }),

  props: ['assignment'],

  components: {
    AssignmentDeleteDialog
  },

  computed: {
    // Compute card style based on assignment status.
    assignmentStyle: function () {
      let style = Style.styleForAssignmentStatus(this.assignment)
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
        'background-image': 'url(' + this.assignment.assetImageUrl + ')',
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
        'background-image': 'url(' + BASE_URL + 'assignments/' +
          this.assignment.token + '/symbol?tenantAuthToken=' +
          tenant.authenticationToken + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee'
      }
    }
  },

  created: function () {
    this.$data.copyData = this.assignment.token
  },

  methods: {
    // Called when site is deleted.
    onAssignmentDeleted: function () {
      this.$emit('assignmentDeleted')
    },

    // Called after token is copied.
    onTokenCopied: function (e) {
      this.$data.showTokenCopied = true
    },

    // Called if unable to copy token.
    onTokenCopyFailed: function (e) {
    },

    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    },

    // Open the assignment emulator.
    onOpenEmulator: function () {
      this.$emit('emulatorOpened')
    }
  }
}
</script>

<style scoped>
.assn {
  min-height: 210px;
  min-width: 920px;
  overflow-y: hidden;
}

.assn-label {
  font-weight: 700;
  font-size: 14px;
}

.assn-field {
  font-size: 14px;
}

.assn-logo {
  position: absolute;
  top: 10px;
  left: 7px;
  bottom: 7px;
  width: 180px;
}

.assn-qrcode {
  position: absolute;
  top: 10px;
  right: 7px;
  bottom: 7px;
  width: 180px;
}

.assn-emulator {
  position: absolute;
  left: 15px;
  bottom: 10px;
}

.assn-asset {
  position: absolute;
  top: 8px;
  left: 230px;
  font-size: 24px;
  white-space: nowrap;
}

.assn-token-label {
  position: absolute;
  top: 46px;
  left: 230px;
  white-space: nowrap;
}

.assn-token {
  position: absolute;
  top: 36px;
  left: 340px;
  white-space: nowrap;
  overflow-x: hidden;
}

.assn-device-label {
  position: absolute;
  top: 73px;
  left: 230px;
  white-space: nowrap;
}

.assn-device {
  position: absolute;
  top: 73px;
  left: 340px;
  white-space: nowrap;
}

.assn-created-label {
  position: absolute;
  top: 100px;
  left: 230px;
}

.assn-created {
  position: absolute;
  top: 100px;
  left: 340px;
  white-space: nowrap;
}

.assn-updated-label {
  position: absolute;
  top: 127px;
  left: 230px;
}

.assn-updated {
  position: absolute;
  top: 127px;
  left: 340px;
  white-space: nowrap;
}

.assn-active-label {
  position: absolute;
  top: 154px;
  left: 230px;
}

.assn-active {
  position: absolute;
  top: 154px;
  left: 340px;
  white-space: nowrap;
}

.assn-released-label {
  position: absolute;
  top: 181px;
  left: 230px;
}

.assn-released {
  position: absolute;
  top: 181px;
  left: 340px;
  white-space: nowrap;
}

.assn-delete {
  position: absolute;
  bottom: 0px;
  right: 200px;
}
</style>
