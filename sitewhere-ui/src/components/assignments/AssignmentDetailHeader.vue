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
      <div class="assn-headers">
        <header-field label="Assignment token">
          <clipboard-copy-field :field="assignment.token"
            message="Assignment token copied to clipboard">
          </clipboard-copy-field>
        </header-field>
        <header-field label="Assigned asset">
          <span>{{ assignment.assetName }}</span>
        </header-field>
        <header-field label="Assigned device">
          <span>{{ assignment.device.deviceType.assetName }}</span>
        </header-field>
        <header-field label="Created date">
          <span>{{ formatDate(assignment.createdDate) }}</span>
        </header-field>
        <header-field label="Last updated date">
          <span>{{ formatDate(assignment.updatedDate) }}</span>
        </header-field>
        <header-field label="Active date">
          <span>{{ formatDate(assignment.activeDate) }}</span>
        </header-field>
        <header-field label="Released date">
          <span>{{ formatDate(assignment.releasedDate) }}</span>
        </header-field>
      </div>
      <options-menu class="options-menu">
        <v-list slot="options">
          <v-list-tile>
            <v-btn block class="red darken-2 white--text"
              @click="onDeleteAssignment">
              Delete Assignment
              <v-spacer></v-spacer>
              <v-icon class="white--text pl-2">fa-times</v-icon>
            </v-btn>
          </v-list-tile>
        </v-list>
      </options-menu>
      <assignment-delete-dialog ref="delete" :token="assignment.token"
        @assignmentDeleted="onAssignmentDeleted">
      </assignment-delete-dialog>
    </v-card-text>
    <v-snackbar :timeout="2000" success v-model="showTokenCopied">Token copied to clipboard
      <v-btn dark flat @click.native="showTokenCopied = false">Close</v-btn>
    </v-snackbar>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import Style from '../common/Style'
import {createCoreApiUrl} from '../../http/sitewhere-api-wrapper'
import HeaderField from '../common/HeaderField'
import ClipboardCopyField from '../common/ClipboardCopyField'
import OptionsMenu from '../common/OptionsMenu'
import AssignmentDeleteDialog from './AssignmentDeleteDialog'

export default {

  data: () => ({
    copyData: null,
    showTokenCopied: false
  }),

  props: ['assignment'],

  components: {
    HeaderField,
    ClipboardCopyField,
    OptionsMenu,
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
        'background-image': 'url(' + createCoreApiUrl(this.$store) +
          'assignments/' + this.assignment.token + '/symbol?tenantAuthToken=' +
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
    // Open dialog to delete assignment.
    onDeleteAssignment: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called when site is deleted.
    onAssignmentDeleted: function () {
      this.$emit('assignmentDeleted')
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
  min-height: 220px;
  min-width: 920px;
  overflow-y: hidden;
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

.assn-headers {
  position: absolute;
  top: 20px;
  left: 200px;
  right: 200px;
}

.assn-emulator {
  position: absolute;
  left: 15px;
  bottom: 10px;
}

.assn-delete {
  position: absolute;
  bottom: 0px;
  right: 200px;
}

.options-menu {
  position: absolute;
  top: 10px;
  right: 190px;
}
</style>
