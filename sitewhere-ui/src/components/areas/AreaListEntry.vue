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
            <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn flat color="orange" @click.stop="onViewData">
                View Data
              </v-btn>
              <v-btn v-if="hasSubareas" flat color="orange"
                @click.stop="onViewSubAreas">
                View Subareas
              </v-btn>
            </v-card-actions>
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

  props: ['area', 'parentArea'],

  computed: {
    // Determines whether area may have sub-areas.
    hasSubareas: function () {
      return this.area.areaType.containedAreaTypeIds.length > 0
    },
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
    // Determines action taken when area is clicked.
    onAreaClicked: function () {
      if (this.hasSubareas) {
        this.onViewSubAreas()
      } else {
        this.onViewData()
      }
    },
    // Called to view data for area.
    onViewData: function () {
      this.$emit('viewAreaData', this.area)
    },
    // Called to view sub-areas.
    onViewSubAreas: function () {
      this.$emit('viewSubAreas', this.area)
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
