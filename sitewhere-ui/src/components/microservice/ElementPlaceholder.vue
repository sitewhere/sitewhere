<template>
  <v-toolbar v-if="contextElement.optional" flat class="grey lighten-5 pa-2">
    <v-icon light>fa-plus</v-icon>
    <v-toolbar-title class="black--text">
      {{ contextElement.name }}
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-menu offset-y left>
      <v-btn dark class="grey mr-0" slot="activator">Add Component</v-btn>
      <v-list dense>
        <v-list-tile v-for="option in contextElement.options"
          :key="option.name" @click="onAddComponent(option)">
          <v-list-tile-title class="subheading">
            <v-icon class="mr-1">fa-{{ option.icon }}</v-icon>
            {{ option.name }}
          </v-list-tile-title>
        </v-list-tile>
      </v-list>
    </v-menu>
  </v-toolbar>
  <v-toolbar v-else flat class="grey lighten-5">
    <v-icon light class="fa-lg red--text text--darken-3">fa-warning</v-icon>
    <v-toolbar-title class="black--text">
      {{ contextElement.name }}
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-menu offset-y left>
      <v-btn dark class="red darken-3 mr-0" slot="activator">Add Required Component</v-btn>
      <v-list dense>
        <v-list-tile v-for="option in contextElement.options"
          :key="option.name"
          @click.native="onAddComponent(option)">
          <v-list-tile-title class="subheading">
            <v-icon class="mr-1">fa-{{ option.icon }}</v-icon>
            {{ option.name }}
          </v-list-tile-title>
        </v-list-tile>
      </v-list>
    </v-menu>
  </v-toolbar>
</template>

<script>
export default {

  data: () => ({
  }),

  props: ['contextElement'],

  methods: {
    // Add a component.
    onAddComponent: function (option) {
      this.$emit('addComponent', option)
    }
  }
}
</script>

<style scoped>
</style>
