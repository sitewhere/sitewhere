<template>
  <div>
    <component-dialog ref="dialog" width="600"
      createLabel="Create" cancelLabel="Cancel"
      :title="title" @payload="onCommit" :context="context"
      :tenantId="tenantId">
    </component-dialog>
  </div>
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
        return 'Create ' + this.model.name
      }
      return 'Create Component'
    }
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
      this.getDialogComponent().closeDialog()
      this.$emit('elementAdded', payload)
    }
  }
}
</script>

<style scoped>
</style>
