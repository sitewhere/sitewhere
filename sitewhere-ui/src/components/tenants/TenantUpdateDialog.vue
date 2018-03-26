<template>
  <div>
    <tenant-dialog ref="dialog" title="Edit Tenant" width="700"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </tenant-dialog>
  </div>
</template>

<script>
import TenantDialog from './TenantDialog'
import {_getTenant, _updateTenant} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    TenantDialog
  },

  props: ['tenantToken'],

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getTenant(this.$store, this.tenantToken)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
        })
    },

    // Called after data is loaded.
    onLoaded: function (response) {
      this.getDialogComponent().load(response.data)
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateTenant(this.$store, this.tenantToken, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('tenantUpdated')
    }
  }
}
</script>

<style scoped>
</style>
