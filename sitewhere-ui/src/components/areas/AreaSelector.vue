<template>
  <v-select :items="sites" item-text="name" item-value="token"
    v-model="selectedToken" hide-details single-line>
  </v-select>
</template>

<script>
import {_listAreas} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    sites: [],
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
    _listAreas(component.$store, {}, 'page=1&pageSize=0')
      .then(function (response) {
        component.$data.sites = response.data.results
      }).catch(function (e) {
      })
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
