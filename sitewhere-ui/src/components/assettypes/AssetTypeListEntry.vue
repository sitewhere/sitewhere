<template>
  <v-card hover color="white">
    <v-container fluid grid-list-lg @click="onAssetTypeClicked">
      <v-layout row>
        <v-flex xs3>
          <v-card-media>
            <div :style="logoStyle"></div>
          </v-card-media>
        </v-flex>
        <v-flex xs9>
          <div>
            <div class="headline ellipsis">{{ assetType.name }}</div>
            <div class="atdesc">{{ assetType.description }}</div>
          </div>
        </v-flex>
      </v-layout>
    </v-container>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'

export default {

  data: () => ({
  }),

  components: {
    Utils
  },

  props: ['assetType'],

  computed: {
    // Compute style of logo.
    logoStyle: function () {
      return {
        'background-color': '#fff',
        'background-image': 'url(' + this.assetType.imageUrl + ')',
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
    // Callen when card is clicked.
    onAssetTypeClicked: function () {
      Utils.routeTo(this, '/assettypes/' + this.assetType.token)
    }
  }
}
</script>

<style scoped>
.atdesc {
  height: 80px;
  overflow-y: hidden;
}
</style>
