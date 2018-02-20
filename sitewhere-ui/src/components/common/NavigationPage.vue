<template>
  <div>
    <v-toolbar flat dark dense card class="primary">
      <v-icon>{{ icon }}</v-icon>
      <v-toolbar-title class="white--text">
        {{ title }}
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <slot name="actions"></slot>
    </v-toolbar>
    <slot name="content"></slot>
  </div>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['icon', 'title'],

  components: {
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
