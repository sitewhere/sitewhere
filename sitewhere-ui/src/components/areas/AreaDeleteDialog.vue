<template>
  <span>
    <delete-dialog title="Delete Site" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-text>
        Are you sure you want to delete this site?
      </v-card-text>
    </delete-dialog>
    <v-tooltip top>
      <v-btn dark icon small
        @click.stop="showDeleteDialog" slot="activator">
        <v-icon class="grey--text">fa-times</v-icon>
      </v-btn>
      <span>Delete Site</span>
    </v-tooltip>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteArea} from '../../http/sitewhere-api-wrapper'

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
      _deleteArea(this.$store, this.token, true)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('siteDeleted')
    }
  }
}
</script>

<style scoped>
</style>
