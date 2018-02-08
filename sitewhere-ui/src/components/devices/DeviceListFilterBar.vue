<template>
  <v-card dark color="primary" class="elevation-1">
    <v-toolbar flat dark dense card class="primary">
      <v-toolbar-title class="white--text">
        Device Filter Criteria
        <span v-if="emptyFilter">(No filter applied)</span>
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-tooltip left>
        <v-btn icon slot="activator" @click="onShowFilterCriteria">
          <v-icon>fa-filter</v-icon>
        </v-btn>
        <span>Filter Device List</span>
      </v-tooltip>
    </v-toolbar>
    <v-card color="primary" v-if="!emptyFilter">
      <v-card-text class="pt-0 mt-0">
        <v-tooltip bottom>
          <v-chip close v-if="siteFilter" @input="onSiteFilterClosed"
            slot="activator">
            <v-avatar>
              <img :src="siteFilter.imageUrl" alt="siteFilter.name">
            </v-avatar>
            {{ siteFilter.name }}
          </v-chip>
          <span>Only include devices from this site</span>
        </v-tooltip>
        <v-tooltip bottom>
          <v-chip close v-if="deviceTypeFilter"
            @input="onDeviceTypeFilterClosed" slot="activator">
            <v-avatar>
              <img :src="deviceTypeFilter.assetImageUrl" alt="deviceTypeFilter.name">
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
    <device-list-filter-dialog ref="criteria"
      :filter="filter" @filter="onFilterUpdated">
    </device-list-filter-dialog>
  </v-card>
</template>

<script>
import DeviceListFilterDialog from './DeviceListFilterDialog'

export default {

  data: () => ({
    siteFilter: null,
    deviceTypeFilter: null,
    deviceGroupFilter: null
  }),

  components: {
    DeviceListFilterDialog
  },

  computed: {
    emptyFilter: function () {
      return (!this.siteFilter && !this.deviceTypeFilter &&
        !this.deviceGroupFilter)
    },
    filter: function () {
      var result = {}
      result.siteFilter = this.$data.siteFilter
      result.deviceTypeFilter = this.$data.deviceTypeFilter
      result.deviceGroupFilter = this.$data.deviceGroupFilter
      return result
    }
  },

  methods: {
    // Called to show filter criteria dialog.
    onShowFilterCriteria: function () {
      this.$refs['criteria'].openDialog()
    },
    // Called when filter criteria are updated.
    onFilterUpdated: function (filter) {
      if (filter) {
        this.$data.siteFilter = filter.siteFilter
        this.$data.deviceTypeFilter = filter.deviceTypeFilter
        this.$data.deviceGroupFilter = filter.deviceGroupFilter

        let criteria = {}
        criteria.site = (filter.siteFilter) ? filter.siteFilter.token : null
        criteria.deviceType = (filter.deviceTypeFilter)
          ? filter.deviceTypeFilter.token : null
        criteria.group = (filter.deviceGroupFilter)
          ? filter.deviceGroupFilter.token : null
        this.$emit('filter', criteria)
      }
    },
    // Remove site filter on close.
    onSiteFilterClosed: function () {
      this.$data.siteFilter = null
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
