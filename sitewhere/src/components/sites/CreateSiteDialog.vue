<template>
  <div>
    <site-dialog title="Create Site" width="600" :open="open" :close="close"
      resetOnOpen="true" createLabel="Create" cancelLabel="Cancel"
      @payload="onCommit">
    </site-dialog>
    <v-btn slot="activator" floating class="add-button red darken-1"
      v-tooltip:top="{ html: 'Add Site' }" @click.native.stop="onOpenDialog">
      <v-icon light>add</v-icon>
    </v-btn>
  </div>
</template>

<script>
import Vue from 'vue'
import SiteDialog from './SiteDialog'

export default {

  data: () => ({
    'open': false,
    'close': false
  }),

  components: {
    SiteDialog
  },

  methods: {
    // HACK to send one-time open signal.
    onOpenDialog: function () {
      var component = this
      component.$data.open = true
      Vue.nextTick(function () {
        component.$data.open = false
      })
    },
    // HACK to send one-time open signal.
    onCloseDialog: function () {
      var component = this
      component.$data.close = true
      Vue.nextTick(function () {
        component.$data.close = false
      })
    }
  }
}
</script>

<style scoped>
.add-button {
  position: absolute;
  bottom: 16px;
  right: 16px;
}
</style>
