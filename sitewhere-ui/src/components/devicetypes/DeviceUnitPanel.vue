<template>
  <v-card v-if="deviceUnit != null"class="ma-3 elevation-0">
    <v-card-text class="pa-0">
      <v-toolbar v-if="deviceUnit.path == null" class="blue darken-2 white--text" dark>
        <v-toolbar-title class="namespace-title subheading">Device Element Schema</v-toolbar-title>
        <v-spacer></v-spacer>
        <device-unit-create-dialog :deviceUnit="deviceUnit"
          @deviceUnitAdded="onDeviceUnitAdded">
        </device-unit-create-dialog>
      </v-toolbar>
      <v-toolbar v-else class="blue darken-2 white--text" dark>
        <v-toolbar-title class="namespace-title subheading">{{ deviceUnit.name }} ({{ fullPath }})</v-toolbar-title>
        <v-spacer></v-spacer>
        <device-unit-create-dialog :deviceUnit="deviceUnit"
          @deviceUnitAdded="onDeviceUnitAdded">
        </device-unit-create-dialog>
        <device-unit-delete-dialog :deviceUnit="deviceUnit"
          @deviceUnitDeleted="onDeviceUnitDeleted">
        </device-unit-delete-dialog>
      </v-toolbar>
      <v-card>
        <v-card-text>
          <v-card>
            <v-toolbar class="grey lighten-3 black--text elevation-0" dark>
              <v-toolbar-title class="namespace-title subheading">Device Slots</v-toolbar-title>
              <v-spacer></v-spacer>
              <device-slot-create-dialog :deviceUnit="deviceUnit"
                @deviceSlotAdded="onDeviceSlotAdded">
              </device-slot-create-dialog>
            </v-toolbar>
            <v-list class="pa-0" v-if="deviceUnit.deviceSlots.length > 0">
              <div v-for="(deviceSlot, index) in deviceUnit.deviceSlots"
                :key="deviceSlot.name">
                <v-divider v-if="index > 0"></v-divider>
                <device-slot-list-entry :parentPath="fullPath"
                  :deviceSlot="deviceSlot"
                  @deviceSlotDeleted="onDeviceSlotDeleted">
                </device-slot-list-entry>
              </div>
            </v-list>
            <no-results-panel v-if="deviceUnit.deviceSlots.length === 0"
              text="No Slots Currently Configured"
              minHeight="60px" fontSize="16px" padding="20px">
            </no-results-panel>
          </v-card>
        </v-card-text>
        <v-card-media class="elevation-0" v-for="(subUnit, index) in deviceUnit.deviceUnits" :key="subUnit.name">
          <device-unit-panel :deviceUnit="subUnit" :parentPath="fullPath"
            @contentUpdated="onContentUpdated" @childUnitDeleted="onChildUnitDeleted">
          </device-unit-panel>
        </v-card-media>
      </v-card>
    </v-card-text>
  </v-card>
</template>

<script>
import lodash from 'lodash'
import NoResultsPanel from '../common/NoResultsPanel'
import DeviceUnitCreateDialog from './DeviceUnitCreateDialog'
import DeviceUnitDeleteDialog from './DeviceUnitDeleteDialog'
import DeviceSlotCreateDialog from './DeviceSlotCreateDialog'
import DeviceSlotDeleteDialog from './DeviceSlotDeleteDialog'
import DeviceSlotListEntry from './DeviceSlotListEntry'

export default {
  // Because component is used recursively.
  name: 'device-unit-panel',

  data: () => ({
  }),

  props: ['deviceUnit', 'parentPath'],

  components: {
    NoResultsPanel,
    DeviceUnitCreateDialog,
    DeviceUnitDeleteDialog,
    DeviceSlotCreateDialog,
    DeviceSlotDeleteDialog,
    DeviceSlotListEntry
  },

  computed: {
    // Get full path for the unit.
    fullPath: function () {
      if (!this.parentPath) {
        if (!this.deviceUnit.path) {
          return null
        }
        return '/' + this.deviceUnit.path
      }
      return this.parentPath + '/' + this.deviceUnit.path
    }
  },

  methods: {
    // Called when a device unit is added.
    onDeviceUnitAdded: function (unit) {
      this.deviceUnit.deviceUnits.push(unit)
      this.$emit('contentUpdated')
    },

    // Called when a device unit is deleted.
    onDeviceUnitDeleted: function (unit) {
      this.$emit('childUnitDeleted', unit)
    },

    // Called when a child device unit is deleted.
    onChildUnitDeleted: function (unit) {
      this.deviceUnit.deviceUnits =
        lodash.reject(this.deviceUnit.deviceUnits, {'path': unit.path})
      this.$emit('contentUpdated')
    },

    // Called when device slot is added.
    onDeviceSlotAdded: function (slot) {
      this.deviceUnit.deviceSlots.push(slot)
      this.$emit('contentUpdated')
    },

    // Called when device slot is deleted.
    onDeviceSlotDeleted: function (slot) {
      this.deviceUnit.deviceSlots =
        lodash.reject(this.deviceUnit.deviceSlots, {'path': slot.path})
      this.$emit('contentUpdated')
    },

    // Notify parent that content was updated.
    onContentUpdated: function () {
      this.$emit('contentUpdated')
    }
  }
}
</script>

<style scoped>
</style>
