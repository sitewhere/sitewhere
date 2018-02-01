<template>
<v-app>
  <main>
    <v-container>
      <v-card raised class="grey lighten-4 white--text mt-4"
        style="width: 600px; margin-left: auto; margin-right: auto;">
        <v-card-text class="white">
          <img src="../assets/sitewhere.png" style="height: 185px;"/>
        </v-card-text>
        <v-divider></v-divider>
        <v-progress-linear v-if="loading" class="login-progress">
        </v-progress-linear>
        <error-banner :error="error"></error-banner>
        <v-card-text>
          <v-layout row wrap style="width: 100%">
            <v-flex xs12>
              <div style="width: 100%; text-align: center; color: #333; font-size: 28px;">
                SiteWhere Server Administration
              </div>
            </v-flex>
          </v-layout>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-text>
          <v-layout row wrap class="pa-3">
            <v-flex xs12 class="mb-4">
              <v-text-field hide-details label="Username" v-model="username">
              </v-text-field>
            </v-flex>
            <v-flex xs12 class="mb-4">
              <v-text-field hide-details label="Password" v-model="password"
                type="password">
              </v-text-field>
            </v-flex>
            <v-flex xs3 class="pr-3">
              <v-select required :items="protocols" v-model="protocol"
                label="Protocol"></v-select>
            </v-flex>
            <v-flex xs5 class="pr-3">
              <v-text-field hide-details label="Server" v-model="server">
              </v-text-field>
            </v-flex>
            <v-flex xs4>
              <v-text-field hide-details label="Port" type="number"
                v-model="port">
              </v-text-field>
            </v-flex>
          </v-layout>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn type="submit" primary dark @click.stop="onLogin">
            Login
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-container>
  </main>
</v-app>
</template>

<script>
import {_getJwt, _getUser} from '../http/sitewhere-api-wrapper'
import ErrorBanner from './common/ErrorBanner'

export default {

  data: () => ({
    username: '',
    password: '',
    protocol: null,
    server: null,
    port: null,
    protocols: [
      {
        'text': 'http',
        'value': 'http'
      }, {
        'text': 'https',
        'value': 'https'
      }]
  }),

  components: {
    ErrorBanner
  },

  created: function () {
    this.$data.protocol = this.$store.getters.protocol
    this.$data.server = this.$store.getters.server
    this.$data.port = this.$store.getters.port
  },

  watch: {
    // Push protocol value to store.
    protocol: function (value) {
      this.$store.commit('protocol', value)
    },

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
      this.$store.commit('selectedTenant', null)

      _getJwt(this.$store)
        .then(function (response) {
          var jwt = response.headers['x-sitewhere-jwt']
          component.$store.commit('jwt', jwt)
          component.onJwtAcquired()
        }).catch(function (e) {
          console.log(e)
        })
    },

    onJwtAcquired: function (jwt) {
      var component = this
      _getUser(this.$store, this.username)
        .then(function (response) {
          component.$store.commit('user', response.data)
          component.$router.push('/system')
        }).catch(function (e) {
          console.log(e)
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
