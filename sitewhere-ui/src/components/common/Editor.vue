<template>
  <v-navigation-drawer light persistent right v-model="drawerEdit" style="width: 50%; margin-top: 60px;">
  </v-navigation-drawer>
</template>

<script>
import {restAuthGet} from '../../http/http-common'

export default {

  data: () => ({
    sites: null,
    drawerEdit: true
  }),

  created: function () {
    var component = this
    restAuthGet(this.$store,
      'sites',
      function (response) {
        component.sites = response.data
      }, function (e) {
        component.error = e
      })
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
