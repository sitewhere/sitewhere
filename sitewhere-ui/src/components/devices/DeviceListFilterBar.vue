<template>
  <v-card dark color="primary" class="elevation-1">
    <v-card color="primary" v-if="!emptyFilter">
      <v-card-text class="pt-0 mt-0">
        <v-tooltip bottom>
          <v-chip close v-if="areaFilter" @input="onAreaFilterClosed"
            slot="activator">
            <v-avatar>
              <img :src="areaFilter.imageUrl" alt="areaFilter.name">
            </v-avatar>
            {{ areaFilter.name }}
          </v-chip>
          <span>Only include devices from this area</span>
        </v-tooltip>
        <v-tooltip bottom>
          <v-chip close v-if="deviceTypeFilter"
            @input="onDeviceTypeFilterClosed" slot="activator">
            <v-avatar>
              <img :src="deviceTypeFilter.imageUrl" alt="deviceTypeFilter.name">
            </v-avatar>
            {{ deviceTypeFilter.name }}
          </v-chip>
          <span>Only include devices from this device type</span>
        </v-tooltip>
        <v-tooltip bottom>
          <v-chip close v-if="deviceGroupFilter"
            @input="onDeviceGroupFilterClosed" slot="activator">
            <v-avatar>
              <v-icon light>view_module</v-icon>
            </v-avatar>
            {{ deviceGroupFilter.name }}
          </v-chip>
          <span>Only include devices from this device group</span>
        </v-tooltip>
      </v-card-text>
    </v-card>
    <v-spacer></v-spacer>
    <device-list-filter-dialog ref="criteria"
      :filter="filter" @filter="onFilterUpdated">
    </device-list-filter-dialog>
  </v-card>
</template>

<script>
import DeviceListFilterDialog from './DeviceListFilterDialog'

export default {

  data: () => ({
    areaFilter: null,
    deviceTypeFilter: null,
    deviceGroupFilter: null
  }),

  components: {
    DeviceListFilterDialog
  },

  computed: {
    emptyFilter: function () {
      return (!this.areaFilter && !this.deviceTypeFilter &&
        !this.deviceGroupFilter)
    },
    filter: function () {
      var result = {}
      result.areaFilter = this.$data.areaFilter
      result.deviceTypeFilter = this.$data.deviceTypeFilter
      result.deviceGroupFilter = this.$data.deviceGroupFilter
      return result
    }
  },

  methods: {
    // Called to show filter criteria dialog.
    showFilterCriteriaDialog: function () {
      this.$refs['criteria'].openDialog()
    },
    // Called when filter criteria are updated.
    onFilterUpdated: function (filter) {
      if (filter) {
        this.$data.areaFilter = filter.areaFilter
        this.$data.deviceTypeFilter = filter.deviceTypeFilter
        this.$data.deviceGroupFilter = filter.deviceGroupFilter

        let criteria = {}
        criteria.area = (filter.areaFilter) ? filter.areaFilter.token : null
        criteria.deviceType = (filter.deviceTypeFilter)
          ? filter.deviceTypeFilter.token : null
        criteria.group = (filter.deviceGroupFilter)
          ? filter.deviceGroupFilter.token : null
        this.$emit('filter', criteria)
      }
    },
    // Remove area filter on close.
    onAreaFilterClosed: function () {
      this.$data.areaFilter = null
      this.onFilterUpdated(this.filter)
    },
    // Remove deviceType filter on close.
    onDeviceTypeFilterClosed: function () {
      this.$data.deviceTypeFilter = null
      this.onFilterUpdated(this.filter)
    },
    // Remove device group filter on close.
    onDeviceGroupFilterClosed: function () {
      this.$data.deviceGroupFilter = null
      this.onFilterUpdated(this.filter)
    }
  }
}
</script>

<style scoped>
</style>
