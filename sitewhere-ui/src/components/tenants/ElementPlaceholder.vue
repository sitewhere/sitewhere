<template>
  <v-toolbar flat dark class="grey lighten-5">
    <v-icon light fa class="fa-lg">plus</v-icon>
    <v-toolbar-title class="black--text">
      {{ contextElement.name }}
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-menu offset-y left>
      <v-btn dark class="grey" slot="activator">Add Component</v-btn>
      <v-list dense>
        <v-list-tile v-for="option in contextElement.options"
          :key="option.role"
          @click.native="onAddComponent(option)">
          <v-list-tile-title class="subheading">
            <v-icon fa class="mr-1">{{ option.icon }}</v-icon>
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
