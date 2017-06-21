<template>
  <v-app>
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
        <v-list-group v-for="section in sections" :value="section.active" :key="section.id">
          <v-list-tile @click.native="onSectionClicked(section)" slot="item">
            <v-list-tile-action>
              <v-icon light>{{ section.icon }}</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{ section.title }}</v-list-tile-title>
            </v-list-tile-content>
            <v-list-tile-action v-if="section.subsections">
              <v-icon light>keyboard_arrow_down</v-icon>
            </v-list-tile-action>
          </v-list-tile>
          <v-list-item v-for="subsection in section.subsections" :key="subsection">
            <v-list-tile @click.native="onSectionClicked(subsection)">
              <v-list-tile-content>
                <v-list-tile-title>{{ subsection.title }}</v-list-tile-title>
              </v-list-tile-content>
              <v-list-tile-action>
                <v-icon light>{{ subsection.icon }}</v-icon>
              </v-list-tile-action>
            </v-list-tile>
          </v-list-item>
        </v-list-group>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar fixed class="grey darken-3" light>
      <v-toolbar-side-icon class="grey--text" @click.native.stop="drawer = !drawer"></v-toolbar-side-icon>
      <v-icon left light>{{section.icon}}</v-icon>
      <v-toolbar-title>{{section.longTitle}}</v-toolbar-title>
      <v-menu bottom right offset-y origin="bottom right" transition="v-slide-y-transition">
        <v-btn class="grey darken-1 white--text" slot="activator">
          <v-icon light class="mr-2">portrait</v-icon>
          {{fullname}}
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
      <v-progress-linear v-if="loading" class="login-progress" v-bind:indeterminate="true"></v-progress-linear>
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </main>
  </v-app>
</template>

<script>
export default {
  data: () => ({
    drawer: true,
    sections: [{
      id: 'server',
      title: 'Server',
      icon: 'home',
      route: '/admin/server',
      longTitle: 'Server Administration'
    },
    {
      id: 'sites',
      title: 'Sites',
      icon: 'map',
      route: '/admin/sites',
      longTitle: 'Manage Sites'
    },
    {
      id: 'deviceGroup',
      title: 'Devices',
      icon: 'developer_board',
      route: '/admin/devices',
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
        route: '/admin/devices',
        longTitle: 'Manage Devices'
      }, {
        id: 'devicegroups',
        title: 'Device Groups',
        icon: 'view_module',
        route: '/admin/devicegroups',
        longTitle: 'Manage Device Groups'
      }]
    },
    {
      id: 'assets',
      title: 'Assets',
      icon: 'local_offer',
      route: '/admin/assets',
      longTitle: 'Manage Assets'
    },
    {
      id: 'batch',
      title: 'Batch Operations',
      icon: 'group_work',
      route: '/admin/batch',
      longTitle: 'Manage Batch Operations'
    },
    {
      id: 'schedules',
      title: 'Schedules',
      icon: 'event',
      route: '/admin/schedules',
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

  computed: {
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
    loading: function () {
      return this.$store.getters.loading
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

    // Select first section from list.
    this.onSectionClicked(this.$data.sections[0])
  },

  methods: {
    // Called when a section is clicked.
    onSectionClicked: function (section) {
      this.$store.commit('currentSection', section)
      this.$router.push(section.route)
    },
    onUserAction: function (action) {
      if (action.id === 'logout') {
        this.onLogOut()
      }
    },
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
