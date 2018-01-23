<template>
  <span>
    <component-dialog ref="dialog" :title="title"
      width="600" resetOnOpen="true" createLabel="Update" cancelLabel="Cancel"
      @payload="onCommit" :context="context" :tenantId="tenantId">
    </component-dialog>
  </span>
</template>

<script>
import ComponentDialog from './ComponentDialog'

export default {

  data: () => ({
  }),

  components: {
    ComponentDialog
  },

  props: ['context', 'tenantId'],

  computed: {
    title: function () {
      if (this.model) {
        return 'Update ' + this.model.name
      }
      return 'Update Component'
    }
  },

  methods: {
    // Get handle to nested dialog component.
    getDialogComponent: function () {
      return this.$refs['dialog']
    },

    // Send event to open dialog.
    onOpenDialog: function () {
      this.getDialogComponent().openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      this.getDialogComponent().closeDialog()
      this.$emit('elementUpdated', payload)
    }
  }
}
</script>

<style scoped>
</style>
