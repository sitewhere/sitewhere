<template>
  <v-app v-if="user">
    <error-banner :error="error"></error-banner>
    <v-progress-linear v-if="loading" class="call-progress pa-0 ma-0">
    </v-progress-linear>
    <v-navigation-drawer dark fixed mini-variant.sync="false"
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
      <v-toolbar-side-icon class="grey--text" @click.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-icon left dark>{{ section.icon }}</v-icon>
      <v-toolbar-title class="subheading">{{ section.longTitle }}</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-menu bottom right offset-y>
        <v-btn class="grey darken-1 white--text" slot="activator">
          <v-icon dark class="mr-2">portrait</v-icon>
          {{ fullname }}
        </v-btn>
        <v-list>
          <v-list-tile @click="onUserAction(action)" v-for="action in userActions" :key="action.id">
            <v-icon left light class="mr-2">{{action.icon}}</v-icon>
            <v-list-tile-title v-text="action.title"></v-list-tile-title>
          </v-list-tile>
        </v-list>
      </v-menu>
    </v-toolbar>
    <v-content>
      <router-view></router-view>
    </v-content>
    <!--
    <v-footer fixed dark class="pa-1">Copyright 2017 <strong>SiteWhere LLC</strong></v-footer>
    -->
  </v-app>
</template>

<script>
import Navigation from './common/Navigation'
import ErrorBanner from './common/ErrorBanner'
import {_getJwt} from '../http/sitewhere-api-wrapper'

export default {
  data: () => ({
    drawer: true,
    tenantId: null,
    sections: [{
      id: 'tenants',
      title: 'Tenant Management',
      icon: 'layers',
      route: 'system/tenants',
      longTitle: 'Manage System Tenants',
      requireAll: ['ADMINISTER_TENANTS']
    },
    {
      id: 'users',
      title: 'User Management',
      icon: 'people',
      route: 'system/users',
      longTitle: 'Manage System Users',
      requireAll: ['ADMINISTER_USERS']
    },
    {
      id: 'global',
      title: 'Global Microservices',
      icon: 'language',
      route: 'system/microservices',
      longTitle: 'Manage Global microservices',
      requireAll: ['ADMINISTER_TENANTS']
    }],
    userActions: [{
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
    this.onSectionClicked(this.$data.sections[0])
  },

  methods: {
    // Called when a section is clicked.
    onSectionClicked: function (section) {
      this.$store.commit('currentSection', section)
      this.$router.push('/' + section.route)
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
