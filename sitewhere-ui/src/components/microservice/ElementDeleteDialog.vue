<template>
  <span>
    <delete-dialog ref="dialog" title="Delete Configuration Element" width="400"
      :error="error" @delete="onDeleteConfirmed">
      <v-card-text>
        {{ query }}
      </v-card-text>
    </delete-dialog>
    <v-btn class="red darken-2 white--text"
      @click.native.stop="showDeleteDialog">
      <v-icon fa class="white--text mr-1">times</v-icon>
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
      if (this.element.required) {
        return 'Are you sure you want to delete \'' + this.element.name +
          '\'? It is a required by the parent component.'
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
