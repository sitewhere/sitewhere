<template>
  <v-list-tile avatar @click.native="onUpdateCommand">
    <v-list-tile-content>
      <command-html :command="command"></command-html>
    </v-list-tile-content>
    <v-list-tile-action>
      <div style="width: 80px;">
        <command-update-dialog ref="update" :token="command.token"
          @commandUpdated="onCommandUpdated">
        </command-update-dialog>
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
import CommandUpdateDialog from './CommandUpdateDialog'

export default {

  data: () => ({
  }),

  components: {
    CommandHtml,
    CommandDeleteDialog,
    CommandUpdateDialog
  },

  props: ['command'],

  methods: {
    // Opens update dialog on tile click.
    onUpdateCommand: function () {
      this.$refs['update'].onOpenDialog()
    },

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
.command-comment {
  font-family: 'courier';
}
</style>
