<template>
  <v-select :items="areaTypes" item-text="name" item-value="token"
    v-model="selectedToken" label="Area type" prepend-icon="subject">
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

  computed: {
    // Indexes area types by token.
    areaTypesByToken: function () {
      let ats = this.$data.areaTypes
      let atById = {}
      if (ats) {
        for (let i = 0; i < ats.length; i++) {
          let at = ats[i]
          atById[at.token] = at
        }
      }
      return atById
    }
  },

  watch: {
    value: function (updated) {
      this.$data.selectedToken = updated
    },
    selectedToken: function (updated) {
      this.$emit('input', updated)
      this.$emit('areaTypeUpdated', this.areaTypesByToken[updated])
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
