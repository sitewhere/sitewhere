<template>
  <v-app v-if="user">
    <error-banner :error="error"></error-banner>
    <v-progress-linear v-if="loading" class="call-progress pa-0 ma-0">
    </v-progress-linear>
    <v-navigation-drawer fixed dark mini-variant.sync="false"
      v-model="drawer" app>
      <v-list>
        <v-list-tile tag="div">
          <img src="https://s3.amazonaws.com/sitewhere-demo/sitewhere-white.png"
            style="height: 40px;" />
        </v-list-tile>
      </v-list>
      <v-divider></v-divider>
      <navigation :sections="sections" @sectionSelected="onSectionClicked">
      </navigation>
    </v-navigation-drawer>
    <v-toolbar fixed class="grey darken-3" dark app>
      <v-toolbar-side-icon class="grey--text" @click.native.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-icon left dark>{{ section.icon }}</v-icon>
      <v-toolbar-title class="subheading">{{ section.longTitle }}</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-menu bottom right offset-y>
        <v-btn class="grey darken-1 white--text" slot="activator">
          <v-icon dark class="mr-2">portrait</v-icon>
          {{ fullname }}
        </v-btn>
        <v-list>
          <v-list-tile @click="onUserAction(action)"
            v-for="action in userActions" :key="action.id">
            <v-icon left light class="mr-2">{{action.icon}}</v-icon>
            <v-list-tile-title v-text="action.title"></v-list-tile-title>
          </v-list-tile>
        </v-list>
      </v-menu>
    </v-toolbar>
    <v-content>
      <router-view></router-view>
    </v-content>
  </v-app>
</template>

<script>
import {_getTenant, _getJwt} from '../../http/sitewhere-api-wrapper'
import Navigation from '../common/Navigation'
import ErrorBanner from '../common/ErrorBanner'

export default {
  data: () => ({
    drawer: true,
    tenantToken: null,
    sections: [{
      id: 'areasGroup',
      title: 'Area Management',
      icon: 'fa-map',
      route: 'areas',
      longTitle: 'Manage Areas',
      subsections: [{
        id: 'areatypes',
        title: 'Area Types',
        icon: 'fa-cog',
        route: 'areatypes',
        longTitle: 'Manage Area Types'
      }, {
        id: 'areas',
        title: 'Areas',
        icon: 'fa-map',
        route: 'areas',
        longTitle: 'Manage Areas'
      }]
    },
    {
      id: 'deviceGroup',
      title: 'Device Management',
      icon: 'fa-microchip',
      route: 'devices',
      longTitle: 'Manage Devices',
      subsections: [{
        id: 'devicetypes',
        title: 'Device Types',
        icon: 'fa-cog',
        route: 'devicetypes',
        longTitle: 'Manage Device Types'
      }, {
        id: 'devices',
        title: 'Devices',
        icon: 'fa-microchip',
        route: 'devices',
        longTitle: 'Manage Devices'
      }, {
        id: 'groups',
        title: 'Device Groups',
        icon: 'view_module',
        route: 'groups',
        longTitle: 'Manage Device Groups'
      }]
    },
    {
      id: 'assetGroup',
      title: 'Asset Management',
      icon: 'fa-car',
      route: 'assets',
      longTitle: 'Manage Assets',
      subsections: [{
        id: 'assettypes',
        title: 'Asset Types',
        icon: 'fa-cog',
        route: 'assettypes',
        longTitle: 'Manage Asset Types'
      }, {
        id: 'assets',
        title: 'Assets',
        icon: 'fa-car',
        route: 'assets',
        longTitle: 'Manage Assets'
      }]
    },
    {
      id: 'batch',
      title: 'Batch Operations',
      icon: 'fa-list-alt',
      route: 'batch',
      longTitle: 'Manage Batch Operations'
    },
    {
      id: 'schedules',
      title: 'Schedules',
      icon: 'fa-calendar',
      route: 'schedules',
      longTitle: 'Manage Schedules'
    }],
    userActions: [{
      id: 'sysadmin',
      title: 'System Administration',
      icon: 'settings'
    }, {
      id: 'logout',
      title: 'Log Out',
      icon: 'power_settings_new'
    }],
    right: null
  }),

  components: {
    Navigation,
    ErrorBanner
  },

  computed: {
    // Get loggied in user.
    user: function () {
      return this.$store.getters.user
    },
    // Get currently selected section.
    section: function () {
      return this.$store.getters.currentSection
    },
    fullname: function () {
      var user = this.$store.getters.user
      if (user) {
        var first = this.$store.getters.user.firstName
        var last = this.$store.getters.user.lastName
        if (last.length > 1) {
          return first + ' ' + last
        } else {
          return first
        }
      }
      return 'Not Logged In'
    },

    // Get global loading indicator.
    loading: function () {
      return this.$store.getters.loading
    },

    // Get global error indicator.
    error: function () {
      return this.$store.getters.error
    }
  },

  created: function () {
    // Set up JWT auto-refresh.
    this.refreshJwt()

    // Verify that user is logged in.
    var user = this.$store.getters.user
    if (!user) {
      console.log('No user found in store. Logging out!')
      this.onLogOut()
      return
    }

    // Verify that a tenant token was specified in the route.
    var tenantToken = this.$route.params.tenantToken
    if (!tenantToken) {
      console.log('No tenant token passed. Logging out!')
      this.onLogOut()
      return
    }
    this.$data.tenantToken = tenantToken

    // Load tenant if tenant id changed or not already loaded.
    var tenant = this.$store.getters.selectedTenant
    if ((!tenant) || (tenant.token !== tenantToken)) {
      this.onLoadTenant(tenantToken)
    } else {
      console.log('tenant ' + tenantToken + ' already loaded')

      // Select first section from list.
      this.onSectionClicked(this.$data.sections[0])
    }
  },

  methods: {
    // Load tenant based on tenant id.
    onLoadTenant: function (tenantToken) {
      console.log('loading tenant ' + tenantToken)
      var component = this

      // Make api call to load tenant.
      _getTenant(this.$store, tenantToken)
        .then(function (response) {
          component.onTenantLoaded(response.data)
        }).catch(function (e) {
          console.log('Unable to load tenant ' + tenantToken + '. Logging out!')
          component.onLogOut()
        })
    },
    // Called after tenant is loaded.
    onTenantLoaded: function (tenant) {
      console.log('Successfully loaded ' + tenant.token + ' tenant.')
      this.$store.commit('selectedTenant', tenant)

      // Select first section from list.
      this.onSectionClicked(this.$data.sections[0])
    },
    // Called when a section is clicked.
    onSectionClicked: function (section) {
      this.$store.commit('currentSection', section)
      this.$router.push('/tenants/' + this.$data.tenantToken + '/' + section.route)
    },
    onUserAction: function (action) {
      if (action.id === 'logout') {
        this.onLogOut()
      } else if (action.id === 'sysadmin') {
        this.$router.push('/system')
      }
    },
    // Called when user requests log out.
    onLogOut: function () {
      console.log('Logging out!')
      this.$store.commit('logOut')
      this.$router.push('/')
    },

    // Set up timer for reloading JWT.
    refreshJwt: function () {
      var component = this
      _getJwt(this.$store)
        .then(function (response) {
          console.log('Refreshed JWT.')
          var jwt = response.headers['x-sitewhere-jwt']
          component.$store.commit('jwt', jwt)
          setTimeout(function () {
            component.refreshJwt()
          }, (1000 * 60 * 5))
        }).catch(function (e) {
          console.log('Could not update JWT.')
          console.log(e)
          component.onLogOut()
        })
    }
  }
}
</script>

<style scoped>
.call-progress {
  position: fixed;
  height: 100px;
  z-index: 1000;
}
</style>
