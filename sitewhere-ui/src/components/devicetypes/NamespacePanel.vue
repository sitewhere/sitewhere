<template>
  <v-card class="ma-3">
    <v-card-text class="pa-0">
      <v-toolbar dense class="grey lighten-3 black--text elevation-0" dark>
        <v-toolbar-title class="namespace-title subheading"><strong>Namespace:</strong> {{ namespace.value }}</v-toolbar-title>
      </v-toolbar>
      <v-list two-line dense>
        <div v-for="(command, index) in namespace.commands" :key="command.token">
          <v-divider v-if="index > 0"></v-divider>
          <command-panel :command="command" @commandDeleted="onCommandDeleted"
            @commandUpdated="onCommandUpdated">
          </command-panel>
        </div>
      </v-list>
    </v-card-text>
  </v-card>
</template>

<script>
import CommandPanel from './CommandPanel'

export default {

  data: () => ({
  }),

  components: {
    CommandPanel
  },

  props: ['namespace'],

  methods: {
    // Called after command has been deleted.
    onCommandDeleted: function () {
      this.$emit('commandDeleted')
    },

    // Called after command has been updated.
    onCommandUpdated: function () {
      this.$emit('commandUpdated')
    }
  }
}
</script>

<style scoped>
.namespace-title {
  font-family: 'courier'
}
</style>
