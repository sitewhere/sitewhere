<template>
  <v-card class="ma-3 elevation-0">
    <v-card-text class="pa-0">
      <v-toolbar v-if="parentUnit == null" class="blue darken-2 white--text" dark>
        <v-toolbar-title class="namespace-title subheading">Device Element Schema</v-toolbar-title>
      </v-toolbar>
      <v-toolbar v-else class="blue darken-2 white--text" dark>
        <v-toolbar-title class="namespace-title subheading">{{ unit.name }}</v-toolbar-title>
      </v-toolbar>
      <v-card>
        <v-card-text>
          <v-card>
            <v-toolbar class="grey lighten-2 black--text elevation-0" dark>
              <v-toolbar-title class="namespace-title subheading">Device Slots</v-toolbar-title>
            </v-toolbar>
            <v-list two-line v-if="unit.deviceSlots.length > 0">
              <div v-for="(deviceSlot, index) in unit.deviceSlots" :key="deviceSlot.name">
                <v-divider v-if="index > 0"></v-divider>
                <device-slot-list-entry :deviceSlot="deviceSlot">
                </device-slot-list-entry>
              </div>
            </v-list>
            <no-results-panel v-if="unit.deviceSlots.length === 0"
              text="No Slots Currently Configured"
              minHeight="60px" fontSize="16px" padding="20px">
            </no-results-panel>
          </v-card>
        </v-card-text>
        <v-card-media class="elevation-0" v-for="(deviceUnit, index) in unit.deviceUnits" :key="deviceUnit.name">
          <device-unit-panel :unit="deviceUnit" :parentUnit="this">
          </device-unit-panel>
        </v-card-media>
      </v-card>
    </v-card-text>
  </v-card>
</template>

<script>
import NoResultsPanel from '../common/NoResultsPanel'
import DeviceSlotDeleteDialog from './DeviceSlotDeleteDialog'
import DeviceSlotListEntry from './DeviceSlotListEntry'

export default {
  // Because component is used recursively.
  name: 'device-unit-panel',

  data: () => ({
  }),

  props: ['unit', 'parentUnit'],

  components: {
    NoResultsPanel,
    DeviceSlotDeleteDialog,
    DeviceSlotListEntry
  },

  computed: {
    // Get full path for the unit.
    fullPath: function () {
      if (this.parentUnit) {
        return this.parentUnit.path + '/' + this.unit.path
      }
      return this.unit.path
    }
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
