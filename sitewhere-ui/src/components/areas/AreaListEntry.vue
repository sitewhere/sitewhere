<template>
  <v-card hover color="white">
    <v-container fluid grid-list-lg @click="onOpenArea">
      <v-layout row>
        <v-flex xs3>
          <v-card-media>
            <div :style="logoStyle"></div>
          </v-card-media>
        </v-flex>
        <v-flex xs9>
          <div>
            <div class="headline">{{ area.name }}</div>
            <div style="height: 80px; overflow: hidden;">{{ area.description }}</div>
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

  props: ['area'],

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-color': '#fff',
        'background-image': 'url(' + this.area.imageUrl + ')',
        'background-size': 'cover',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee',
        'height': '120px',
        'width': '100px'
      }
    }
  },

  methods: {
    // Called when an area is clicked.
    onOpenArea: function () {
      this.$emit('areaOpened', this.area.token)
    }
  }
}
</script>

<style scoped>
</style>
