<template>
  <v-select :items="deviceTypes" item-text="name" item-value="token"
    v-model="selectedToken" hide-details single-line>
  </v-select>
</template>

<script>
import {_listDeviceTypes} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    deviceTypes: [],
    selectedToken: null
  }),

  props: ['value'],

  watch: {
    value: function (updated) {
      this.$data.selectedToken = updated
    },
    selectedToken: function (updated) {
      this.$emit('input', updated)
    }
  },

  // Initially load list of all sites.
  created: function () {
    this.$data.selectedToken = this.value
    var component = this
    _listDeviceTypes(this.$store, false, true)
      .then(function (response) {
        component.$data.deviceTypes = response.data.results
      }).catch(function (e) {
      })
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
