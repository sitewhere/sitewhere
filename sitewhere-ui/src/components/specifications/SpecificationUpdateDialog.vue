<template>
  <div>
    <specification-dialog title="Edit Device Specification" width="600" resetOnOpen="true"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </specification-dialog>
    <v-btn icon v-tooltip:top="{ html: 'Edit Specification' }"
      @click.native.stop="onOpenDialog">
      <v-icon class="grey--text">edit</v-icon>
    </v-btn>
  </div>
</template>

<script>
import SpecificationDialog from './SpecificationDialog'
import {_getDeviceSpecification, _updateDeviceSpecification} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    SpecificationDialog
  },

  props: ['token'],

  methods: {
    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getDeviceSpecification(this.$store, this.token)
        .then(function (response) {
          component.onLoaded(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Called after site data is loaded.
    onLoaded: function (response) {
      this.$children[0].load(response.data)
      this.$children[0].openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateDeviceSpecification(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('specificationUpdated')
    },

    // Handle failed commit.
    onFailed: function (error) {
      this.$children[0].showError(error)
    }
  }
}
</script>

<style scoped>
</style>
