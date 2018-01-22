<template>
  <div>
    <v-toolbar flat dense color="grey lighten-3">
      <v-toolbar-title>{{ title }}</v-toolbar-title>
      <slot></slot>
    </v-toolbar>
    <v-divider></v-divider>
  </div>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['title'],

  methods: {
  }
}
</script>

<style scoped>
</style>
