<template>
  <base-dialog :title="title" width="800" :visible="dialogVisible"
    :createLabel="createLabel" :cancelLabel="cancelLabel" :error="error"
    @createClicked="onCreateClicked" @cancelClicked="onCancelClicked">
    <component-attributes :currentContext="context" :readOnly="false"
      :tenantToken="tenantToken" @valuesUpdated="onValuesUpdated">
    </component-attributes>
  </base-dialog>
</template>

<script>
import Utils from '../common/Utils'
import BaseDialog from '../common/BaseDialog'
import ComponentAttributes from './ComponentAttributes'

export default {

  data: () => ({
    active: null,
    dialogVisible: false,
    attrValues: null,
    error: null
  }),

  components: {
    BaseDialog,
    ComponentAttributes
  },

  props: ['context', 'title', 'createLabel', 'cancelLabel', 'tenantToken'],

  methods: {
    // Generate payload from UI.
    generatePayload: function () {
      let payload = {}
      payload.name = this.context.model.localName
      payload.attributes = Utils.metadataToArray(this.$data.attrValues)
      return payload
    },

    // Reset dialog contents.
    reset: function (e) {
      this.$data.active = 'configuration'
      this.$data.attrValues = null
    },

    // Load dialog from a given payload.
    load: function (payload) {
      this.reset()
    },

    // Called to open the dialog.
    openDialog: function () {
      this.$data.dialogVisible = true
    },

    // Called to open the dialog.
    closeDialog: function () {
      this.$data.dialogVisible = false
    },

    // Called to show an error message.
    showError: function (error) {
      this.$data.error = error
    },

    // Called when attribute values are updated.
    onValuesUpdated: function (values) {
      this.$data.attrValues = values
    },

    // Called after create button is clicked.
    onCreateClicked: function (e) {
      var payload = this.generatePayload()
      this.$emit('payload', payload)
    },

    // Called after cancel button is clicked.
    onCancelClicked: function (e) {
      this.$data.dialogVisible = false
    }
  }
}
</script>

<style scoped>
</style>
