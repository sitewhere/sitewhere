<template>
  <span>
    <v-card hover>
      <v-toolbar flat dark dense card @click="onShowEditDialog"
        :style="{'background-color': status.backgroundColor, 'border': '1px solid ' + status.borderColor}">
        <v-icon>{{ status.icon }}</v-icon>
        <v-toolbar-title :style="{'color': status.foregroundColor}">
          {{ status.name }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip class="ma-0 pa-0" top>
          <v-btn class="ma-0 pa-0" icon slot="activator" @click.stop="onShowEditDialog">
            <v-icon class="white--text">fa-edit</v-icon>
          </v-btn>
          <span>Edit Device Status</span>
        </v-tooltip>
        <v-tooltip class="ma-0 pa-0" top>
          <v-btn class="ml-0 pl-0" icon slot="activator" @click.stop="onShowDeleteDialog">
            <v-icon class="white--text">fa-times</v-icon>
          </v-btn>
          <span>Delete Device Status</span>
        </v-tooltip>
      </v-toolbar>
    </v-card>
    <device-status-update-dialog ref="update"
      :deviceType="deviceType" :code="status.code"
      @statusUpdated="onStatusUpdated">
    </device-status-update-dialog>
    <device-status-delete-dialog ref="delete"
      :deviceType="deviceType" :code="status.code"
      @statusDeleted="onStatusDeleted">
    </device-status-delete-dialog>
  </span>
</template>

<script>
import DeviceStatusUpdateDialog from './DeviceStatusUpdateDialog'
import DeviceStatusDeleteDialog from './DeviceStatusDeleteDialog'

export default {

  data: () => ({
  }),

  components: {
    DeviceStatusUpdateDialog,
    DeviceStatusDeleteDialog
  },

  props: ['status', 'deviceType'],

  methods: {
    // Show edit dialog.
    onShowEditDialog: function () {
      this.$refs['update'].onOpenDialog()
    },
    // Show delete dialog.
    onShowDeleteDialog: function () {
      this.$refs['delete'].showDeleteDialog()
    },
    // Called after status has been deleted.
    onStatusDeleted: function () {
      this.$emit('statusDeleted')
    },
    // Called after status has been updated.
    onStatusUpdated: function () {
      this.$emit('statusUpdated')
    }
  }
}
</script>

<style scoped>
.status-text {
  font-size: 18px;
  vertical-align: top;
  margin-left: 5px;
}
</style>
