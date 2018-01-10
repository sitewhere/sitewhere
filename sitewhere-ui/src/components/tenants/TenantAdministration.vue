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
import {_getTenant} from '../../http/sitewhere-api-wrapper'
import Navigation from '../common/Navigation'
import ErrorBanner from '../common/ErrorBanner'

export default {
  data: () => ({
    drawer: true,
    tenantId: null,
    sections: [{
      id: 'sites',
      title: 'Sites',
      icon: 'map',
      route: 'sites',
      longTitle: 'Manage Sites'
    },
    {
      id: 'deviceGroup',
      title: 'Devices',
      icon: 'developer_board',
      route: 'devices',
      longTitle: 'Manage Devices',
      subsections: [{
        id: 'specifications',
        title: 'Device Specifications',
        icon: 'description',
        route: 'specifications',
        longTitle: 'Manage Device Specifications'
      }, {
        id: 'devices',
        title: 'Devices',
        icon: 'developer_board',
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
      id: 'assets',
      title: 'Assets',
      icon: 'local_offer',
      route: 'assets/categories',
      longTitle: 'Manage Asset Categories'
    },
    {
      id: 'batch',
      title: 'Batch Operations',
      icon: 'group_work',
      route: 'batch',
      longTitle: 'Manage Batch Operations'
    },
    {
      id: 'schedules',
      title: 'Schedules',
      icon: 'event',
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
    // Verify that user is logged in.
    var user = this.$store.getters.user
    if (!user) {
      console.log('No user found in store. Logging out!')
      this.onLogOut()
      return
    }

    // Verify that a tenant id was specified in the route.
    var tenantId = this.$route.params.tenantId
    if (!tenantId) {
      console.log('No tenant id passed. Logging out!')
      this.onLogOut()
      return
    }
    this.$data.tenantId = tenantId

    // Load tenant if tenant id changed or not already loaded.
    var tenant = this.$store.getters.selectedTenant
    if ((!tenant) || (tenant.id !== tenantId)) {
      this.onLoadTenant(tenantId)
    } else {
      console.log('tenant ' + tenantId + ' already loaded')

      // Select first section from list.
      this.onSectionClicked(this.$data.sections[0])
    }
  },

  methods: {
    // Load tenant based on tenant id.
    onLoadTenant: function (tenantId) {
      console.log('loading tenant ' + tenantId)
      var component = this

      // Make api call to load tenant.
      _getTenant(this.$store, tenantId)
        .then(function (response) {
          component.onTenantLoaded(response.data)
        }).catch(function (e) {
          console.log('Unable to load tenant ' + tenantId + '. Logging out!')
          component.onLogOut()
        })
    },
    // Called after tenant is loaded.
    onTenantLoaded: function (tenant) {
      console.log('Successfully loaded ' + tenant.id + ' tenant.')
      this.$store.commit('selectedTenant', tenant)

      // Select first section from list.
      this.onSectionClicked(this.$data.sections[0])
    },
    // Called when a section is clicked.
    onSectionClicked: function (section) {
      this.$store.commit('currentSection', section)
      this.$router.push('/tenants/' + this.$data.tenantId + '/' + section.route)
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
