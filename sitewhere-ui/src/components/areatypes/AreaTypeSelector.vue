<template>
  <v-select :items="areaTypes" item-text="name" item-value="token"
    v-model="selectedToken" hide-details single-line>
  </v-select>
</template>

<script>
import {_listAreaTypes} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    areaTypes: [],
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
    var paging = 'page=1&pageSize=0'
    var component = this
    _listAreaTypes(this.$store, false, paging)
      .then(function (response) {
        component.$data.areaTypes = response.data.results
      }).catch(function (e) {
      })
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
