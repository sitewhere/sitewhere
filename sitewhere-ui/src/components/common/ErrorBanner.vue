<template>
  <v-snackbar class="error-banner" v-if="error" :timeout="5000" error v-model="errorDisplayed">
    {{ errorMessage }}
    <v-btn dark flat @click.native="errorDisplayed = false">Close</v-btn>
  </v-snackbar>
</template>

<script>

export default {

  data: () => ({
    errorDisplayed: false
  }),

  props: ['error'],

  watch: {
    error: function (value) {
      if (value) {
        this.$data.errorDisplayed = true
      }
    }
  },

  computed: {
    errorMessage: function () {
      if (!this.error) {
        return ''
      } else if (this.error.response && this.error.response.headers) {
        if (this.error.response.headers['x-sitewhere-error']) {
          return this.error.response.headers['x-sitewhere-error']
        } else if (this.error.response.status === 403) {
          return 'Server Authentication Failed'
        }
      }
      return this.error.message
    }
  },

  methods: {
  }
}
</script>

<style scoped>
.error-banner {
  z-index: 2000;
}
</style>
