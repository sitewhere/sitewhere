<template>
  <v-layout row wrap class="mb-1">
    <v-flex xs4 class="text-xs-right subheading pr-4">
      <strong>{{ label }}</strong>:
    </v-flex>
    <v-flex class="field" xs8>
      <slot></slot>
    </v-flex>
  </v-layout>
</template>

<script>

export default {

  data: () => ({
  }),

  props: ['label'],

  methods: {
  }
}
</script>

<style scoped>
.field {
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}
</style>
