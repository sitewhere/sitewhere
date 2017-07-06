<template>
  <div>
    <site-dialog title="Edit Site" width="600" resetOnOpen="true"
      createLabel="Update" cancelLabel="Cancel" @payload="onCommit">
    </site-dialog>
    <v-btn icon v-tooltip:top="{ html: 'Edit Site' }"
      @click.native.stop="onOpenDialog">
      <v-icon class="grey--text">edit</v-icon>
    </v-btn>
  </div>
</template>

<script>
import SiteDialog from './SiteDialog'
import {_getSite, _updateSite} from '../../http/sitewhere-api-wrapper'

export default {

  data: () => ({
  }),

  components: {
    SiteDialog
  },

  props: ['token'],

  methods: {
    // Send event to open dialog.
    onOpenDialog: function () {
      var component = this
      _getSite(this.$store, this.token)
        .then(function (response) {
          component.onSiteLoaded(response)
        }).catch(function (e) {
          component.onFailed(e)
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
      _updateSite(this.$store, this.token, payload)
        .then(function (response) {
          component.onCommitted(response)
        }).catch(function (e) {
          component.onFailed(e)
        })
    },

    // Handle successful commit.
    onCommitted: function (result) {
      this.$children[0].closeDialog()
      this.$emit('siteUpdated')
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
