<template>
  <delete-dialog title="Delete Tenant" width="400" :error="error"
    @delete="onDeleteConfirmed">
    <v-card-text>
      Are you sure you want to delete this tenant?
    </v-card-text>
  </delete-dialog>
</template>

<script>
import DeleteDialog from '../common/DeleteDialog'
import {_deleteTenant} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
    error: null
  }),

  props: ['tenantToken'],

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
      _deleteTenant(this.$store, this.tenantToken, true)
        .then(function (response) {
          component.onDeleted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful delete.
    onDeleted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('tenantDeleted')
    }
  }
}
</script>

<style scoped>
</style>
