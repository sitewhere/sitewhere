<template>
</template>

<script>
import {_listTenantsForCurrentUser} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  created: function () {
    var component = this
    _listTenantsForCurrentUser(component.$store).then(function (response) {
      var tenants = response.data
      component.$store.commit('authTenants', tenants)
      if (tenants.length === 1) {
        var tenant = tenants[0]
        component.$store.commit('selectedTenant', tenants[0])
        component.$router.push('/tenants/' + tenant.id + '/server')
      }
    }).catch(function (e) {
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
