<template>
  <span>
    <configuration-element-dialog ref="dialog" :title="title"
      width="600" resetOnOpen="true" createLabel="Update" cancelLabel="Cancel"
      @payload="onCommit" :model="model" :config="config">
    </configuration-element-dialog>
  </span>
</template>

<script>
import ConfigurationElementDialog from './ConfigurationElementDialog'

export default {

  data: () => ({
  }),

  components: {
    ConfigurationElementDialog
  },

  props: ['model', 'config'],

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
