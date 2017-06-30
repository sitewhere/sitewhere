<template>
<v-app>
  <main>
    <v-container>
      <v-layout row wrap>
        <v-flex xs6 offset-xs3>
          <v-card raised class="grey lighten-4 white--text mt-3">
            <v-card-row class="white" style="background: url(/static/sitewhere.png); background-size: contain; background-repeat: no-repeat;" height="200px">
            </v-card-row>
            <v-progress-linear v-if="attempting" class="login-progress" v-bind:indeterminate="true"></v-progress-linear>
            <v-card-row v-if="error">
              <v-alert error v-bind:value="true" style="width: 100%">
                {{error}}
              </v-alert>
            </v-card-row>
            <v-card-row>
              <v-layout row wrap pl-3 pr-0 mb-2 style="width: 100%">
                <v-flex xs12 pt-3>
                  <div style="width: 100%; text-align: center; color: #333; font-size: 28px;">
                    SiteWhere Server Administration
                  </div>
                </v-flex>
              </v-layout>
            </v-card-row>
            <v-card-row>
              <v-layout row wrap pl-3 pr-0 mb-2 style="width: 100%">
                <v-flex xs12 pt-4>
                  <v-text-field hide-details label="Username" v-model="username"></v-text-field>
                </v-flex>
                <v-flex xs12>
                  <v-text-field hide-details label="Password" v-model="password" type="password"></v-text-field>
                </v-flex>
              </v-layout>
            </v-card-row>
            <v-card-row actions>
              <v-btn primary light @click.native="onLogin">Login</v-btn>
            </v-card-row>
          </v-card>
        </v-flex>
      </v-layout>
    </v-container>
  </main>
</v-app>
</template>

<script>
import axios from 'axios'

export default {

  data: () => ({
    username: '',
    password: '',
    error: '',
    attempting: false
  }),

  methods: {
    onLogin: function () {
      this.error = ''
      var token = btoa(this.username + ':' + this.password)

      // Request user information.
      var HTTP = axios.create({
        baseURL: `http://localhost:9090/sitewhere/api/`,
        headers: {
          Authorization: 'Basic ' + token
        }
      })
      this.attempting = true
      HTTP.get(`users/` + this.username)
      .then(response => {
        this.$data.attempting = false
        this.$store.commit('authToken', token)
        this.$store.commit('user', response.data)
        this.$router.push('/tenants')
      })
      .catch(e => {
        this.$data.attempting = false
        this.error = 'Login failed. Verify that username and password are correct.'
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
