<template>
  <v-card dark class="blue darken-2 elevation-1 mb-3">
    <v-card-title class="ma-0">
      <span class="title white--text">Device Filter Criteria</span>
    </v-card-title>
    <div class="pl-2 pr-2 pt-0 pb-0" style="width: 80%">
      <div>
        <span v-if="emptyFilter" class="subheading white--text pl-3">No filter applied</span>
        <v-chip close v-if="siteFilter" @input="onSiteFilterClosed">
          <v-avatar>
            <img :src="siteFilter.imageUrl" alt="siteFilter.name">
          </v-avatar>
          {{ siteFilter.name }}
        </v-chip>
        <v-chip close v-if="specificationFilter"
          @input="onSpecificationFilterClosed">
          <v-avatar>
            <img :src="specificationFilter.assetImageUrl" alt="specificationFilter.name">
          </v-avatar>
          {{ specificationFilter.name }}
        </v-chip>
        <v-chip close v-if="deviceGroupFilter"
          @input="onDeviceGroupFilterClosed">
          <v-avatar>
            <v-icon light>view_module</v-icon>
          </v-avatar>
          {{ deviceGroupFilter.name }}
        </v-chip>
      </div>
      <v-spacer></v-spacer>
      <device-list-filter-dialog :filter="filter" @filter="onFilterUpdated">
      </device-list-filter-dialog>
    </div>
  </v-card>
</template>

<script>
import DeviceListFilterDialog from './DeviceListFilterDialog'

export default {

  data: () => ({
    siteFilter: null,
    specificationFilter: null,
    deviceGroupFilter: null
  }),

  components: {
    DeviceListFilterDialog
  },

  computed: {
    emptyFilter: function () {
      return (!this.siteFilter && !this.specificationFilter &&
        !this.deviceGroupFilter)
    },
    filter: function () {
      var result = {}
      result.siteFilter = this.$data.siteFilter
      result.specificationFilter = this.$data.specificationFilter
      result.deviceGroupFilter = this.$data.deviceGroupFilter
      return result
    }
  },

  methods: {
    // Called when filter criteria are updated.
    onFilterUpdated: function (filter) {
      if (filter) {
        this.$data.siteFilter = filter.siteFilter
        this.$data.specificationFilter = filter.specificationFilter
        this.$data.deviceGroupFilter = filter.deviceGroupFilter
      }
    },
    // Remove site filter on close.
    onSiteFilterClosed: function () {
      this.$data.siteFilter = null
    },
    // Remove specification filter on close.
    onSpecificationFilterClosed: function () {
      this.$data.specificationFilter = null
    },
    // Remove device group filter on close.
    onDeviceGroupFilterClosed: function () {
      this.$data.deviceGroupFilter = null
    }
  }
}
</script>

<style scoped>
</style>
