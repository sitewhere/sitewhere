<template>
<v-app>
  <main>
    <v-container>
      <v-card raised class="grey lighten-4 white--text mt-3"
        style="width: 600px; margin-left: auto; margin-right: auto;">
        <v-card-media class="white" contain src="/static/sitewhere.png" height="200px">
        </v-card-media>
        <v-progress-linear v-if="loading" class="login-progress" :indeterminate="true"></v-progress-linear>
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
        <v-card-text>
          <v-layout row wrap pl-3 pr-0 mb-2 style="width: 100%">
            <v-flex xs12 pt-4>
              <v-text-field hide-details label="Username" v-model="username"></v-text-field>
            </v-flex>
            <v-flex xs12>
              <v-text-field hide-details label="Password" v-model="password" type="password"></v-text-field>
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
    password: ''
  }),

  components: {
    ErrorBanner
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
