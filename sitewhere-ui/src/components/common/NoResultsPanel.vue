<template>
  <v-layout row wrap>
    <v-flex xs12>
      <v-card>
        <div class="no-results">
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

  props: ['text'],

  methods: {
  }
}
</script>

<style scoped>
.no-results {
  text-align: center;
  min-height: 100px;
  font-size: 20px;
  padding: 40px;
}
</style>
