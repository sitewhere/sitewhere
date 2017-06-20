import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: null,
    authToken: null,
    authTenants: null,
    currentSection: null
  },
  mutations: {
    // Set currently logged in user.
    setUser (state, user) {
      state.user = user
    },

    // Set authentication token for logged in user.
    setAuthToken (state, token) {
      state.authToken = token
    },

    // Set list of authorized tenants.
    setAuthTenants (state, tenants) {
      state.authTenants = tenants
    },

    // Set current app section.
    setCurrentSection (state, section) {
      state.currentSection = section
    },

    // Log out of the application.
    logOut (state) {
      state.user = null
      state.authToken = null
      state.authTenants = null
      state.currentSection = null
    }
  },

  getters: {
    user: state => {
      return state.user
    },

    authTenants: state => {
      return state.authTenants
    },

    currentSection: state => {
      return state.currentSection
    }
  }
})
