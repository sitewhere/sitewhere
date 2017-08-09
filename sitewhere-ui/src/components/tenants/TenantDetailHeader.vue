<template>
  <v-card v-if="tenant" class="device white pa-2">
    <v-card-text>
      {{ tenant.name }}
    </v-card-text>
  </v-card>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from '../common/HeaderField'

export default {

  data: () => ({
    copyData: null,
    showIdCopied: false
  }),

  props: ['tenant'],

  components: {
    HeaderField
  },

  methods: {
    // Format date.
    formatDate: function (date) {
      return Utils.formatDate(date)
    }
  }
}
</script>

<style scoped>
</style>
