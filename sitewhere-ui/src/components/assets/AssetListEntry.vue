<template>
  <v-card hover color="white">
    <v-container fluid grid-list-lg @click="onAssetClicked">
      <v-layout row>
        <v-flex xs2>
          <v-card-media>
            <div :style="logoStyle"></div>
          </v-card-media>
        </v-flex>
        <v-flex xs10>
          <div>
            <div class="headline">{{ asset.name }}</div>
            <div>{{ asset.token }}</div>
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

  props: ['asset'],

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-color': '#fff',
        'background-image': 'url(' + this.asset.imageUrl + ')',
        'background-size': 'contain',
        'background-repeat': 'no-repeat',
        'background-position': '50% 50%',
        'border': '1px solid #eee',
        'height': '60px',
        'width': '60px'
      }
    }
  },

  methods: {
    // Handle asset clicked.
    onAssetClicked: function () {
      this.$emit('assetOpened', this.asset)
    }
  }
}
</script>

<style scoped>
</style>
