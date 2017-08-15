<template>
  <v-card hover>
    <v-card-text class="pa-1">
      <div :style="boxStyle()" class="pa-2">
        <v-icon class="fa-lg" :style="{'color': status.foregroundColor}" v-if="status.icon" fa>{{ status.icon }}</v-icon>
        <span class="status-text">{{ status.name }}</span>
      </div>
    </v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <device-status-update-dialog class="ma-0" ref="update"
        :specification="specification" :code="status.code"
        @statusUpdated="onStatusUpdated">
      </device-status-update-dialog>
      <device-status-delete-dialog class="ma-0" ref="update"
        :specification="specification" :code="status.code"
        @statusDeleted="onStatusDeleted">
      </device-status-delete-dialog>
    </v-card-actions>
  </v-card>
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

  props: ['status', 'specification'],

  methods: {
    boxStyle: function () {
      let style = {}
      style['background-color'] = this.status.backgroundColor || '#fff'
      if (this.status.borderColor) {
        style['border'] = '2px solid ' + this.status.borderColor
      }
      style['color'] = this.status.foregroundColor || '#333'
      return style
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
