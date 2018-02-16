<template>
  <v-card hover color="white">
    <v-list v-if="areaTypes">
      <template v-for="(areaType, index) in areaTypes">
        <v-list-tile @click="" :key="areaType.id">
          <v-list-tile-action>
            <v-checkbox v-model="selected" :value="areaType.id">
            </v-checkbox>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>
              <v-icon class="mr-2">{{ areaType.icon }}</v-icon>
              {{ areaType.name }}
            </v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-divider></v-divider>
      </template>
    </v-list>
    <v-card-text v-else>
      No area types available.
    </v-card-text>
  </v-card>
</template>

<script>
export default {

  data: () => ({
    selected: []
  }),

  props: ['selectedAreaTypeIds', 'areaTypes'],

  computed: {
    // Indexes area types by id.
    areaTypesById: function () {
      let atById = {}
      if (this.areaTypes) {
        for (let i = 0; i < this.areaTypes.length; i++) {
          let at = this.areaTypes[i]
          atById[at.id] = at
        }
      }
      return atById
    }
  },

  watch: {
    // Update list selection.
    selectedAreaTypeIds: function (value) {
      this.$data.selected = value
    },
    // Reflect selection updates to listeners.
    selected: function (value) {
      let atById = this.areaTypesById
      let selectedAts = []
      for (let i = 0; i < value.length; i++) {
        let at = atById[value[i]]
        if (at) {
          selectedAts.push(at)
        }
      }
      this.$emit('selectedAreaTypesUpdated', selectedAts)
    }
  },

  methods: {
  }
}
</script>

<style scoped>
</style>
