<template>
  <v-layout row wrap>
    <v-flex xs12>
      <v-card flat>
        <div class="no-results" :style="noResultsStyle">
          {{text}}
        </div>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script>

export default {

  data: () => ({
  }),

  props: ['text', 'minHeight', 'fontSize', 'padding'],

  computed: {
    noResultsStyle: function () {
      return {
        'min-height': this.defaultedMinHeight,
        'font-size': this.defaultedFontSize,
        'padding': this.defaultedPadding
      }
    },

    // Minimum height with default fallback.
    defaultedMinHeight: function () {
      if (this.minHeight) {
        return this.minHeight
      }
      return '100px'
    },

    // Font size with default fallback.
    defaultedFontSize: function () {
      if (this.fontSize) {
        return this.fontSize
      }
      return '20px'
    },

    // Padding with default fallback.
    defaultedPadding: function () {
      if (this.padding) {
        return this.padding
      }
      return '40px'
    }
  },

  methods: {
  }
}
</script>

<style scoped>
.no-results {
  text-align: center;
}
</style>
