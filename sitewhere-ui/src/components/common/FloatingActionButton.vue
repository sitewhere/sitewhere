<template>
  <v-btn fab fixed bottom right dark class="add-button red darken-1 elevation-5"
    v-tooltip:top="{ html: label }" @click.native.stop="onButtonClicked">
    <v-icon>{{ icon }}</v-icon>
  </v-btn>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['label', 'icon'],

  methods: {
    // Called when button is clicked.
    onButtonClicked: function () {
      this.$emit('action')
    }
  }
}
</script>

<style scoped>
</style>
