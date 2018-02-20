<template>
  <v-select :items="areaTypes" item-text="name" item-value="id"
    v-model="selectedId" label="Area type" prepend-icon="subject">
  </v-select>
</template>

<script>
import {_listAreaTypes} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    areaTypes: [],
    selectedId: null
  }),

  props: ['value'],

  computed: {
    // Indexes area types by id.
    areaTypesById: function () {
      let ats = this.$data.areaTypes
      let atById = {}
      if (ats) {
        for (let i = 0; i < ats.length; i++) {
          let at = ats[i]
          atById[at.id] = at
        }
      }
      return atById
    }
  },

  watch: {
    value: function (updated) {
      this.$data.selectedId = updated
    },
    selectedId: function (updated) {
      this.$emit('input', updated)
      this.$emit('areaTypeUpdated', this.areaTypesById[updated])
    }
  },

  // Initially load list of all area types.
  created: function () {
    this.$data.selectedId = this.value
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
