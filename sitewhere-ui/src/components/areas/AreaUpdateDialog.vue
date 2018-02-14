<template>
  <div>
    <area-dialog title="Edit Area" width="600" resetOnOpen="true"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </area-dialog>
    <v-tooltip top>
      <v-btn dark icon small
        @click.stop="onOpenDialog" slot="activator">
        <v-icon class="grey--text">fa-edit</v-icon>
      </v-btn>
      <span>Edit Area</span>
    </v-tooltip>
  </div>
</template>

<script>
import FloatingActionButton from '../common/FloatingActionButton'
import AreaDialog from './AreaDialog'
import {_getArea, _updateArea} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    FloatingActionButton,
    AreaDialog
  },

  props: ['token'],

  methods: {
    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getArea(this.$store, this.token)
        .then(function (response) {
          component.onSiteLoaded(response)
        }).catch(function (e) {
        })
    },

    // Called after site data is loaded.
    onSiteLoaded: function (response) {
      this.$children[0].load(response.data)
      this.$children[0].openDialog()
    },

    // Handle payload commit.
    onCommit: function (payload) {
      var component = this
      _updateArea(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('siteUpdated')
    }
  }
}
</script>

<style scoped>
</style>
