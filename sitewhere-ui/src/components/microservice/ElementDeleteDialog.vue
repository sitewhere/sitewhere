<template>
  <span>
    <delete-dialog ref="dialog" title="Delete Configuration Element" width="400"
      :error="error" @delete="onDeleteConfirmed">
      <v-card-text v-if="!element.optional" class="pb-0 mb-0">
        <v-icon fa class="red--text text--darken-3 mr-1 mb-1">warning</v-icon>
        <span class="title text--darken-5">Deleting Required Element!</span>
      </v-card-text>
      <v-card-text>
        {{ query }}
      </v-card-text>
    </delete-dialog>
    <v-btn class="red darken-2 white--text"
      @click.stop="showDeleteDialog">
      <v-icon class="white--text mr-1">fa-times</v-icon>
      Delete
    </v-btn>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'

export default {

  data: () => ({
    error: null
  }),

  props: ['element'],

  computed: {
    query: function () {
      if (!this.element.optional) {
        return 'Are you sure you want to delete \'' + this.element.name +
          '\'? It is required by the parent component.'
      } else {
        return 'Are you sure you want to delete \'' + this.element.name + '\'?'
      }
    }
  },

  components: {
    DeleteDialog
  },

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Show delete dialog.
    showDeleteDialog: function () {
      this.getDialogComponent().openDialog()
    },

    // Perform delete.
    onDeleteConfirmed: function () {
      this.getDialogComponent().closeDialog()
      this.$emit('elementDeleted')
    }
  }
}
</script>

<style scoped>
</style>
