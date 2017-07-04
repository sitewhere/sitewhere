<template>
  <v-layout row wrap v-if="error">
    <v-flex xs12>
      <v-card class="white--text">
        <v-card-row>
          <v-alert error v-bind:value="true" class="top-alert">
            {{error.message}}
          </v-alert>
        </v-card-row>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>

export default {

  data: () => ({
  }),

  props: ['error'],

  methods: {
  }
}
</script>

<style scoped>
.top-alert {
  margin: 0px;
  width: 100%
}
</style>
