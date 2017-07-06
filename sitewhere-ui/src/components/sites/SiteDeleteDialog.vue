<template>
  <div>
    <delete-dialog title="Delete Site" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this site?
      </v-card-text>
    </delete-dialog>
    <v-btn icon v-tooltip:top="{ html: 'Delete Site' }"
      @click.native.stop="showDeleteDialog">
      <v-icon class="grey--text">delete</v-icon>
    </v-btn>
  </div>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteSite} from '../../http/sitewhere-api-wrapper'

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
      var component = this
      _deleteSite(this.$store, this.token, true)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
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
