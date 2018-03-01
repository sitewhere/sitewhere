<template>
  <v-card hover color="white">
    <v-container fluid grid-list-lg @click="onAreaTypeClicked">
      <v-layout row>
        <v-flex xs2>
          <v-card-media>
            <v-icon class="atlogo">{{ areaType.icon }}</v-icon>
          </v-card-media>
        </v-flex>
        <v-flex xs10>
          <div>
            <div class="headline ellipsis">{{ areaType.name }}</div>
            <div class="atdesc">{{ areaType.description }}</div>
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

  props: ['areaType', 'areaTypes'],

  methods: {
    // Callen when card is clicked.
    onAreaTypeClicked: function () {
      Utils.routeTo(this, '/areatypes/' + this.areaType.token)
    }
  }
}
</script>

<style scoped>
.atlogo {
  font-size: 30pt;
  padding: 10px;
}
.atdesc {
  height: 40px;
  overflow-y: hidden;
}
</style>
