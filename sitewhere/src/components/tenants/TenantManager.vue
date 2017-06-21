<template>
</template>

<script>
import {restAuthGet} from '../../http/http-common'

export default {

  data: () => ({
    error: null
  }),

  created: function () {
    var component = this
    restAuthGet(this.$store,
      'users/' + this.$store.getters.user.username + '/tenants?includeRuntimeInfo=false',
      function (response) {
        var tenants = response.data
        component.$store.commit('authTenants', tenants)
        if (tenants.length === 1) {
          component.$store.commit('selectedTenant', tenants[0])
          component.$router.push('/admin/server')
        }
      }, function (e) {
        component.error = e
      })
  },

  methods: {
    onLogin: function () {
    }
  }
}
</script>

<style scoped>
</style>
