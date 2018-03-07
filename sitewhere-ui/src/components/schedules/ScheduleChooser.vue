<template>
  <v-card flat>
    <v-select v-if="schedules" :disabled="!enabled"
      :style="{'opacity': enabled ? 1 : 0.3 }"
      :items="schedules" v-model="scheduleToken"
      label="Schedule" item-text="name" item-value="token"
      light single-line auto prepend-icon="flash_on"
      hide-details>
    </v-select>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import {_listSchedules} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    scheduleToken: null,
    schedules: null
  }),

  components: {
  },

  props: ['enabled'],

  watch: {
    scheduleToken: function (value) {
      this.$emit('scheduleUpdated', value)
    }
  },

  // Initially load list of all schedules.
  created: function () {
    var component = this

    // Search options.
    let options = {}

    let paging = Utils.pagingForAllResults()
    _listSchedules(this.$store, options, paging)
      .then(function (response) {
        component.schedules = response.data.results
      }).catch(function (e) {
      })
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
