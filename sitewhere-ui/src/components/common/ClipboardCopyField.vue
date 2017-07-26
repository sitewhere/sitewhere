<template>
  <span>
    {{ field }}
    <v-btn style="position: absolute; right: -45px; bottom: -15px;"
      v-tooltip:left="{ html: 'Copy to Clipboard' }" class="mt-0"
      light icon v-clipboard="field" :key="field"
      @success="onFieldCopied" @error="onFieldCopyFailed">
      <v-icon fa>clipboard</v-icon>
    </v-btn>
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
