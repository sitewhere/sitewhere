<template>
  <div>
    <tenant-dialog ref="dialog" title="Create Tenant"
      width="600" resetOnOpen="true" createLabel="Create" cancelLabel="Cancel"
      @payload="onCommit">
    </tenant-dialog>
    <floating-action-button label="Add Tenant" icon="fa-plus"
      @action="onOpenDialog">
    </floating-action-button>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import TenantDialog from './TenantDialog'
import {_createTenant} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    TenantDialog
  },

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      this.getDialogComponent().reset()
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      console.log(payload)
      var component = this
      _createTenant(this.$store, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.getDialogComponent().closeDialog()
      this.$emit('tenantAdded')
    }
  }
}
</script>

<style scoped>
</style>
