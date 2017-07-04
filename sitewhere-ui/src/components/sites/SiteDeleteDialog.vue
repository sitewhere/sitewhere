<template>
  <div>
    <delete-dialog title="Delete Site" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-row>
        <v-card-text>
          Are you sure you want to delete this site?
        </v-card-text>
      </v-card-row>
    </delete-dialog>
    <v-btn icon v-tooltip:top="{ html: 'Delete Site' }"
      @click.native.stop="showDeleteDialog">
      <v-icon class="grey--text">delete</v-icon>
    </v-btn>
  </div>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {restAuthDelete} from '../../http/http-common'

export default {

  data: () => ({
    error: null
  }),

  props: ['token'],

  components: {
    DeleteDialog
  },

  methods: {
    // Show delete dialog.
    showDeleteDialog: function () {
      this.$children[0].openDialog()
    },

    // Perform delete.
    onDeleteConfirmed: function () {
      restAuthDelete(this.$store, '/sites/' + this.token + '?force=true',
        this.onDeleted, this.onFailed)
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('siteDeleted')
    },

    // Handle failed delete.
    onFailed: function (error) {
      this.$children[0].showError(error)
    }
  }
}
</script>

<style scoped>
</style>
