<template>
  <v-snackbar class="error-banner" v-if="error" :timeout="4000" error v-model="errorDisplayed">
    {{ error.message }}
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

  methods: {
  }
}
</script>

<style scoped>
.error-banner {
  z-index: 2000;
}
</style>
