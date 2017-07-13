<template>
  <v-list-tile @click.native="onUpdateSlot">
    <v-list-tile-content>
      Slot {{ deviceSlot.name }} : {{ deviceSlot.path }}
    </v-list-tile-content>
    <v-list-tile-action>
      <span style="width: 80px;">
        <device-slot-delete-dialog ref="update" :deviceSlot="deviceSlot"
          @slotDeleted="onSlotDeleted">
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

  props: ['deviceSlot'],

  components: {
    DeviceSlotDeleteDialog
  },

  methods: {
    // Called when slot is deleted.
    onSlotDeleted: function () {
      this.$emit('slotDeleted', this.slot)
    }
  }
}
</script>

<style scoped>
</style>
