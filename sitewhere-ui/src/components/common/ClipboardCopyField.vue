<template>
  <span>
    {{ field }}
    <v-tooltip right class="pl-1">
      <v-icon style="font-size: 10pt;" class="grey--text" slot="activator"
        v-clipboard="field" :key="field" @success="onFieldCopied"
        @error="onFieldCopyFailed">fa-clipboard</v-icon>
      <span>Copy to Clipboard</span>
    </v-tooltip>
    <v-snackbar :timeout="2000" success v-model="showFieldCopied">{{ message }}
      <v-btn dark flat @click.native="showFieldCopied = false">Close</v-btn>
    </v-snackbar>
  </span>
</template>

<script>
export default {

  data: () => ({
    showFieldCopied: false
  }),

  props: ['field', 'message'],

  methods: {
    // Called after id is copied.
    onFieldCopied: function (e) {
      this.$data.showFieldCopied = true
    },

    // Called if unable to copy id.
    onFieldCopyFailed: function (e) {
    }
  }
}
</script>

<style scoped>
</style>
