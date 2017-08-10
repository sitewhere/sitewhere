<template>
  <div>
    <configuration-element-dialog ref="dialog" width="600"
      :title="title" @payload="onCommit" :model="model">
    </configuration-element-dialog>
  </div>
</template>

<script>
import ConfigurationElementDialog from './ConfigurationElementDialog'

export default {

  data: () => ({
  }),

  components: {
    ConfigurationElementDialog
  },

  props: ['model'],

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
      this.$emit('componentAdded', payload)
    }
  }
}
</script>

<style scoped>
</style>
