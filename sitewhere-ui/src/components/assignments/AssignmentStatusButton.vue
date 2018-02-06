<template>
  <div>
    <confirm-dialog ref="dialog" buttonText="Update"
      title="Update Assignment Status" width="400" @action="onUpdateStatus">
      <v-card-text>
        Are you sure you want to update the assignment status?
      </v-card-text>
    </confirm-dialog>
    <v-menu offset-y v-if="assignment.status === 'Active'">
      <v-tooltip left slot="activator">
        <v-btn small style="height: 20px;"
          class="green darken-2 white--text pa-0 ma-0" slot="activator">
          Active
        </v-btn>
        <span>Update Status</span>
      </v-tooltip>
      <v-list>
        <v-list-tile @click.stop="showDialog(item.action)"
          v-for="item in statusActiveItems" :key="item.text">
          <v-list-tile-title>{{ item.text }}</v-list-tile-title>
        </v-list-tile>
      </v-list>
    </v-menu>
    <v-menu offset-y v-if="assignment.status === 'Missing'">
      <v-tooltip left slot="activator">
        <v-btn small style="height: 20px;"
          class="red darken-2 white--text pa-0 ma-0" slot="activator">
          Missing
        </v-btn>
        <span>Update Status</span>
      </v-tooltip>
      <v-list>
        <v-list-tile @click.stop="showDialog(item.action)"
          v-for="item in statusMissingItems" :key="item.text">
          <v-list-tile-title>{{ item.text }}</v-list-tile-title>
        </v-list-tile>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
import ConfirmDialog from '../common/ConfirmDialog'
import {_releaseAssignment, _missingAssignment} from '../../http/sitewhere-api-wrapper'

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
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Show dialog.
    showDialog: function (action) {
      this.$data.action = action
      this.getDialogComponent().openDialog()
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
      var component = this
      _releaseAssignment(this.$store, this.assignment.token)
        .then(function (response) {
          component.onStatusUpdated(response.data)
        }).catch(function (e) {
        })
    },

    // Called to mark an assignment as missing.
    onMissingAssignment: function (assignment) {
      var component = this
      _missingAssignment(this.$store, this.assignment.token)
        .then(function (response) {
          component.onStatusUpdated(response.data)
        }).catch(function (e) {
        })
    },

    // Handle successful update.
    onStatusUpdated: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('statusUpdated')
    },

    // Handle failed update.
    onFailed: function (error) {
      this.getDialogComponent().showError(error)
    }
  }
}
</script>

<style scoped>
</style>
