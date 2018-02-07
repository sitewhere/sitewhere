<template>
  <v-menu offset-y>
    <v-btn icon slot="activator">
      <v-icon color="grey">fa-bars</v-icon>
    </v-btn>
    <v-card>
      <slot name="options">
          <div>Buttons go here!</div>
      </slot>
    </v-card>
  </v-menu>
</template>

<script>
export default {

  data: () => ({
  }),

  methods: {
  }
}
</script>

<style scoped>
</style>
