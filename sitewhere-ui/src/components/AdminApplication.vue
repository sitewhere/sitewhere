<template>
  <v-app v-if="user">
    <v-navigation-drawer persistent dark :mini-variant.sync="mini" v-model="drawer">
      <v-list class="pa-0">
        <v-list-item>
          <v-list-tile avatar tag="div">
            <img src="https://s3.amazonaws.com/sitewhere-demo/sitewhere-white.png"
              style="height: 40px;" />
          </v-list-tile>
        </v-list-item>
      </v-list>
      <v-list v-if="sections" dense class="pt-0">
        <v-divider></v-divider>
        <v-list-group v-for="navsect in sections" :value="navsect.active" :key="navsect.id">
          <v-list-tile @click.native="onSectionClicked(navsect)" slot="item">
            <v-list-tile-action>
              <v-icon light>{{ navsect.icon }}</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{ navsect.title }}</v-list-tile-title>
            </v-list-tile-content>
            <v-list-tile-action v-if="navsect.subsections">
              <v-icon light>keyboard_arrow_down</v-icon>
            </v-list-tile-action>
          </v-list-tile>
          <v-list-item v-for="navsub in navsect.subsections" :key="navsub">
            <v-list-tile @click.native="onSectionClicked(navsub)">
              <v-list-tile-content>
                <v-list-tile-title>{{ navsub.title }}</v-list-tile-title>
              </v-list-tile-content>
              <v-list-tile-action>
                <v-icon light>{{ navsub.icon }}</v-icon>
              </v-list-tile-action>
            </v-list-tile>
          </v-list-item>
        </v-list-group>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar fixed class="grey darken-3" light>
      <v-toolbar-side-icon class="grey--text" @click.native.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-icon left light>{{ section.icon }}</v-icon>
      <v-toolbar-title>{{ section.longTitle }}</v-toolbar-title>
      <v-menu bottom right offset-y origin="bottom right" transition="v-slide-y-transition">
        <v-btn class="grey darken-1 white--text" slot="activator">
          <v-icon light class="mr-2">portrait</v-icon>
          {{ fullname }}
        </v-btn>
        <v-list>
          <v-list-item v-for="action in userActions" :key="action">
            <v-list-tile @click.native="onUserAction(action)">
              <v-icon left dark class="mr-2">{{action.icon}}</v-icon>
              <v-list-tile-title v-text="action.title"></v-list-tile-title>
            </v-list-tile>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-toolbar>
    <main>
      <error-banner :error="error"></error-banner>
      <v-progress-linear v-if="loading" class="login-progress" :indeterminate="true"></v-progress-linear>
      <div style="height: 7px;" v-else></div>
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </main>
  </v-app>
</template>

<script>
import {restAuthGet} from '../http/http-common'
import ErrorBanner from './common/ErrorBanner'

export default {
  data: () => ({
    drawer: true,
    tenantId: null,
    sections: [{
      id: 'configure',
      title: 'Tenant Configuration',
      icon: 'settings',
      route: 'server',
      longTitle: 'Tenant Configuration'
    },
    {
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
        route: '/admin/specifications',
        longTitle: 'Manage Device Specifications'
      }, {
        id: 'devices',
        title: 'Devices',
        icon: 'developer_board',
        route: 'devices',
        longTitle: 'Manage Devices'
      }, {
        id: 'devicegroups',
        title: 'Device Groups',
        icon: 'view_module',
        route: 'devicegroups',
        longTitle: 'Manage Device Groups'
      }]
    },
    {
      id: 'assets',
      title: 'Assets',
      icon: 'local_offer',
      route: 'assets',
      longTitle: 'Manage Assets'
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
      id: 'logout',
      title: 'Log Out',
      icon: 'power_settings_new'
    }],
    mini: false,
    right: null
  }),

  components: {
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

      restAuthGet(this.$store,
        'tenants/' + tenantId,
        function (response) {
          component.onTenantLoaded(response.data)
        }, function (e) {
          console.log('Unable to load tenant ' + tenantId + '. Logging out!')
          component.onLogOut()
        }
      )
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
      this.$router.push('/admin/' + this.$data.tenantId + '/' + section.route)
    },
    onUserAction: function (action) {
      if (action.id === 'logout') {
        this.onLogOut()
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
.list__tile__action {
  min-width: 40px;
}
.list__tile__title {
  font-size: 16px;
  padding-top: 3px;
}
.login-progress {
  margin: 0px;
}
</style>
