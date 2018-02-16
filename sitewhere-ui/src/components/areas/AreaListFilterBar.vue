<template>
  <v-card dark color="primary" class="elevation-1">
    <v-toolbar v-if="parentArea" flat dark dense card class="primary">
      <v-icon>{{ parentArea.areaType.icon }}</v-icon>
      <v-toolbar-title class="white--text">
        {{ parentArea.name }}
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-tooltip left>
        <v-btn icon slot="activator" @click="onUpOneLevel">
          <v-icon>fa-arrow-circle-up</v-icon>
        </v-btn>
        <span>Up One Level</span>
      </v-tooltip>
    </v-toolbar>
    <v-toolbar v-else flat dark dense card class="primary">
      <v-icon>fa-map</v-icon>
      <v-toolbar-title class="white--text">
        Root of Area Hierarchy
      </v-toolbar-title>
    </v-toolbar>
  </v-card>
</template>

<script>

export default {

  data: () => ({
    areaStack: []
  }),

  components: {
  },

  computed: {
    parentArea: function () {
      if (this.areaStack.length === 0) {
        return null
      }
      return this.areaStack[this.areaStack.length - 1]
    }
  },

  methods: {
    // Push an area onto the stack.
    pushArea: function (area) {
      this.areaStack.push(area)
    },
    // Move up one level in the hierarchy.
    onUpOneLevel: function () {
      this.areaStack.pop()
      this.$emit('parentAreaUpdated', this.parentArea)
    }
  }
}
</script>

<style scoped>
</style>
