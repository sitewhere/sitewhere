<template>
  <v-card hover color="white">
    <v-container fluid grid-list-lg @click="onAreaClicked">
      <v-layout row>
        <v-flex xs3>
          <v-card-media>
            <div :style="logoStyle"></div>
          </v-card-media>
        </v-flex>
        <v-flex xs9>
          <div>
            <div class="headline">{{ area.name }}</div>
            <div class="areadesc">{{ area.description }}</div>
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
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee',
        'height': '120px',
        'width': '100px'
      }
    }
  },

  methods: {
    // Handle area clicked.
    onAreaClicked: function () {
      this.$emit('openArea', this.area)
    }
  }
}
</script>

<style scoped>
.areadesc {
  height: 40px;
  overflow-y: hidden;
}
</style>
