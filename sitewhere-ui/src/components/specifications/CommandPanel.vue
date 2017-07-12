<template>
  <v-list-tile avatar>
    <v-list-tile-content>
      <command-html :command="command"></command-html>
    </v-list-tile-content>
    <v-list-tile-action>
      <div style="width: 80px;">
        <v-btn class="mr-1" icon v-tooltip:top="{ html: 'Edit Command' }"
          @click.native.stop="onEditCommand">
          <v-icon class="grey--text">edit</v-icon>
        </v-btn>
        <v-btn icon v-tooltip:top="{ html: 'Delete Command' }"
          @click.native.stop="onDeleteCommand">
          <v-icon class="grey--text">delete</v-icon>
        </v-btn>
      </div>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
import CommandHtml from './CommandHtml'

export default {

  data: () => ({
  }),

  components: {
    CommandHtml
  },

  props: ['command'],

  methods: {
    // Emit event indicating command should be deleted.
    onDeleteCommand: function () {

    }
  }
}
</script>

<style scoped>
.command-comment {
  font-family: 'courier';
}
</style>
