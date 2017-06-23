<template>
  <v-card hover class="site white pa-2">
    <v-card-row>
      <v-card-text>
        {{ assignment.token }}
      </v-card-text>
    </v-card-row>
  </v-card>
</template>

<script>

export default {

  data: () => ({
  }),

  props: ['assignment'],

  methods: {
  }
}
</script>

<style scoped>
</style>
