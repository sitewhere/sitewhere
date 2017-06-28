<template>
  <div>
    <confirm-dialog buttonText="Update" title="Update Assignment Status" width="400" :error="error"
      @action="onUpdateStatus">
      <v-card-row>
        <v-card-text>
          Are you sure you want to update the assignment status?
        </v-card-text>
      </v-card-row>
    </confirm-dialog>
    <v-menu offset-y v-if="assignment.status === 'Active'">
      <v-btn small style="height: 20px;" class="green darken-2 white--text pa-0 ma-0" slot="activator"
        v-tooltip:top="{ html: 'Update Status' }">Active</v-btn>
      <v-list>
        <v-list-item v-for="item in statusActiveItems" :key="item">
          <v-list-tile @click.native="showDialog(item.action)">
            <v-list-tile-title>{{ item.text }}</v-list-tile-title>
          </v-list-tile>
        </v-list-item>
      </v-list>
    </v-menu>
    <v-menu offset-y v-if="assignment.status === 'Missing'">
      <v-btn small style="height: 20px;" class="red darken-2 white--text pa-0 ma-0" slot="activator"
        v-tooltip:top="{ html: 'Update Status' }">Missing</v-btn>
      <v-list>
        <v-list-item v-for="item in statusMissingItems" :key="item">
          <v-list-tile @click.native="showDialog(item.action)">
            <v-list-tile-title>{{ item.text }}</v-list-tile-title>
          </v-list-tile>
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
import ConfirmDialog from '../common/ConfirmDialog'
import {releaseAssignment, missingAssignment} from '../../http/sitewhere-api'

export default {

  data: function () {
    return {
      action: null,
      statusActiveItems: [
        {
          text: 'Release Assignment',
          action: this.onReleaseAssignment
        }, {
          text: 'Report Missing',
          action: this.onMissingAssignment
        }
      ],
      statusMissingItems: [
        {
          text: 'Release Assignment',
          action: this.onReleaseAssignment
        }
      ]
    }
  },

  props: ['assignment'],

  components: {
    ConfirmDialog
  },

  methods: {
    // Show dialog.
    showDialog: function (action) {
      this.$data.action = action
      this.$children[0].openDialog()
    },

    // Execute action for menu item.
    onUpdateStatus: function () {
      var action = this.$data.action
      if (action) {
        action()
      }
    },

    // Called to mark an assignment as released.
    onReleaseAssignment: function () {
      releaseAssignment(this.$store, this.assignment.token,
        this.onStatusUpdated, this.onFailed)
    },

    // Called to mark an assignment as missing.
    onMissingAssignment: function (assignment) {
      missingAssignment(this.$store, this.assignment.token,
        this.onStatusUpdated, this.onFailed)
    },

    // Handle successful update.
    onStatusUpdated: function (result) {
      this.$children[0].closeDialog()
      this.$emit('statusUpdated')
    },

    // Handle failed update.
    onFailed: function (error) {
      this.$children[0].showError(error)
    }
  }
}
</script>

<style scoped>
</style>
