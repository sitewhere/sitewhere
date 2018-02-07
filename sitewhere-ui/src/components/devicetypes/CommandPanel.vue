<template>
  <v-list-tile avatar @click.native="onUpdateCommand">
    <v-list-tile-content>
      <command-html :command="command"></command-html>
    </v-list-tile-content>
    <v-list-tile-action>
      <actions-block @edited="onCommandUpdated" @deleted="onCommandDeleted">
        <command-update-dialog slot="edit" ref="update" :token="command.token">
        </command-update-dialog>
        <command-delete-dialog slot="delete" :token="command.token">
        </command-delete-dialog>
      </actions-block>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
import ActionsBlock from '../common/ActionsBlock'
import CommandHtml from './CommandHtml'
import CommandDeleteDialog from './CommandDeleteDialog'
import CommandUpdateDialog from './CommandUpdateDialog'

export default {

  data: () => ({
  }),

  components: {
    ActionsBlock,
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
