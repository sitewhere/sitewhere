<template>
  <span class="ma-0">
    <delete-dialog title="Delete Zone" width="400" :error="error"
      @delete="onDeleteConfirmed">
      <v-card-row>
        <v-card-text>
          Are you sure you want to delete this zone?
        </v-card-text>
      </v-card-row>
    </delete-dialog>
    <v-btn class="ma-0" icon v-tooltip:top="{ html: 'Delete Zone' }"
      @click.native.stop="showDeleteDialog">
      <v-icon class="grey--text">delete</v-icon>
    </v-btn>
  </span>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {deleteZone} from '../../http/sitewhere-api'

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
      deleteZone(this.$store, this.token, this.onDeleted, this.onFailed)
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('zoneDeleted')
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
