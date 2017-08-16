<template>
<v-app>
  <main>
    <v-container>
      <v-card>
        <v-card-text class="yellow lighten-4" style="text-align: center;">
          <v-icon fa class="red--text mr-2">warning</v-icon>
          This is a preview release of the next-generation SiteWhere UI. It
          should be considered <strong>beta</strong> quality at this point and is not intended
          for production use.
        </v-card-text>
      </v-card>
      <v-card raised class="grey lighten-4 white--text mt-5"
        style="width: 600px; margin-left: auto; margin-right: auto;">
        <v-card-text class="white">
          <img src="../assets/sitewhere.png" style="height: 185px;"/>
        </v-card-text>
        <v-divider></v-divider>
        <v-progress-linear v-if="loading" class="login-progress">
        </v-progress-linear>
        <error-banner :error="error"></error-banner>
        <v-card-text>
          <v-layout row wrap pl-0 pr-0 mb-0 style="width: 100%">
            <v-flex xs12 pa-0>
              <div style="width: 100%; text-align: center; color: #333; font-size: 28px;">
                SiteWhere Server Administration
              </div>
            </v-flex>
          </v-layout>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-text>
          <v-layout row wrap pl-3 pr-0 mb-2 style="width: 100%">
            <v-flex xs12 pt-4>
              <v-text-field hide-details label="Username" v-model="username">
              </v-text-field>
            </v-flex>
            <v-flex xs12>
              <v-text-field hide-details label="Password" v-model="password"
                type="password">
              </v-text-field>
            </v-flex>
            <v-flex xs7>
              <v-text-field hide-details label="Server" v-model="server">
              </v-text-field>
            </v-flex>
            <v-flex xs5>
              <v-text-field hide-details label="Port" type="number"
                v-model="port">
              </v-text-field>
            </v-flex>
          </v-layout>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn primary dark @click.native="onLogin">Login</v-btn>
        </v-card-actions>
      </v-card>
    </v-container>
  </main>
</v-app>
</template>

<script>
import {_getUser} from '../http/sitewhere-api-wrapper'
import ErrorBanner from './common/ErrorBanner'

export default {

  data: () => ({
    username: '',
    password: '',
    server: null,
    port: null
  }),

  components: {
    ErrorBanner
  },

  created: function () {
    this.$data.server = this.$store.getters.server
    this.$data.port = this.$store.getters.port
  },

  watch: {
    // Push server value to store.
    server: function (value) {
      this.$store.commit('server', value)
    },

    // Push port value to store.
    port: function (value) {
      this.$store.commit('port', value)
    }
  },

  computed: {
    // Get global loading indicator.
    loading: function () {
      return this.$store.getters.loading
    },

    // Get global error indicator.
    error: function () {
      return this.$store.getters.error
    }
  },

  methods: {
    onLogin: function () {
      var component = this

      var token = btoa(this.username + ':' + this.password)
      this.$store.commit('authToken', token)

      _getUser(this.$store, this.username)
        .then(function (response) {
          component.$store.commit('user', response.data)
          component.$router.push('/system')
        }).catch(function (e) {
        })
    }
  }
}
</script>

<style scoped>
.login-progress {
  margin: 0px;
}
</style>
