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
        <command-delete-dialog :token="command.token"
          @commandDeleted="onCommandDeleted">
        </command-delete-dialog>
      </div>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
import CommandHtml from './CommandHtml'
import CommandDeleteDialog from './CommandDeleteDialog'

export default {

  data: () => ({
  }),

  components: {
    CommandHtml,
    CommandDeleteDialog
  },

  props: ['command'],

  methods: {
    // Called after command has been deleted.
    onCommandDeleted: function () {
      this.$emit('commandDeleted')
    }
  }
}
</script>

<style scoped>
.command-comment {
  font-family: 'courier';
}
</style>
