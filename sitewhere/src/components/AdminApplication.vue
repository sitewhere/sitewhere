<template>
  <v-app>
    <v-navigation-drawer persistent light :mini-variant.sync="mini" v-model="drawer">
      <v-list class="pa-0">
        <v-list-item>
          <v-list-tile avatar tag="div">
            <img src="https://s3.amazonaws.com/sitewhere-demo/sitewhere-small.png"
              style="height: 50px; margin-left: 45px;" />
          </v-list-tile>
        </v-list-item>
      </v-list>
      <v-list class="pt-0">
        <v-divider></v-divider>
        <v-list-item v-for="section in sections" :key="section">
          <v-list-tile @click.native="onSectionClicked(section)">
            <v-list-tile-action>
              <v-icon>{{ section.icon }}</v-icon>
            </v-list-tile-action>
            <v-list-tile-content>
              <v-list-tile-title>{{ section.title }}</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>
        </v-list-item>
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
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </main>
    <v-footer class="pa-3 grey darken-3">
      <v-spacer></v-spacer>
      <div>© {{ new Date().getFullYear() }} SiteWhere LLC</div>
    </v-footer>
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
      var first = this.$store.getters.user.firstName
      var last = this.$store.getters.user.lastName
      if (last.length > 1) {
        return first + ' ' + last
      } else {
        return first
      }
    }
  },

  created: function () {
    this.onSectionClicked(this.$data.sections[0])
  },

  methods: {
    // Called when a section is clicked.
    onSectionClicked: function (section) {
      this.$store.commit('setCurrentSection', section)
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
</style>
