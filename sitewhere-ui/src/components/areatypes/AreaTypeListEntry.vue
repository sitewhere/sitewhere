<template>
  <v-card hover color="white">
    <v-container fluid grid-list-lg>
      <v-layout row>
        <v-flex xs2>
          <v-card-media>
            <v-icon style="font-size: 35pt;">{{ areaType.icon }}</v-icon>
          </v-card-media>
        </v-flex>
        <v-flex xs10>
          <div>
            <div class="headline">{{ areaType.name }}</div>
            <div>{{ areaType.description }}</div>
          </div>
        </v-flex>
      </v-layout>
    </v-container>
  </v-card>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['areaType'],

  methods: {
    // Called when an area is clicked.
    onOpenAreaType: function () {
      this.$emit('areaTypeOpened', this.areaType.token)
    }
  }
}
</script>

<style scoped>
</style>
