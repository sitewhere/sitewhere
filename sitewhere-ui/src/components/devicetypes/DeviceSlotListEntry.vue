<template>
  <v-list-tile>
    <v-icon class="grey--text mr-2">storage</v-icon>
    <v-list-tile-content>
      {{ deviceSlot.name }} ({{ fullPath }})
    </v-list-tile-content>
    <v-list-tile-action>
      <span>
        <device-slot-delete-dialog :deviceSlot="deviceSlot"
          @deviceSlotDeleted="onDeviceSlotDeleted">
        </device-slot-delete-dialog>
      </span>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
import DeviceSlotDeleteDialog from './DeviceSlotDeleteDialog'

export default {

  data: () => ({
  }),

  props: ['deviceSlot', 'parentPath'],

  components: {
    DeviceSlotDeleteDialog
  },

  computed: {
    // Full path for slot.
    fullPath: function () {
      if (this.parentPath) {
        return this.parentPath + '/' + this.deviceSlot.path
      }
      return '/' + this.deviceSlot.path
    }
  },

  methods: {
    // Called when slot is deleted.
    onDeviceSlotDeleted: function (slot) {
      console.log('slot delete in list entry')
      this.$emit('deviceSlotDeleted', slot)
    }
  }
}
</script>

<style scoped>
</style>
