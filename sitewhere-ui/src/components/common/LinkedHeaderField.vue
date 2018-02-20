<template>
  <header-field :label="label">
    <a @click="onLinkClicked">{{ text}}</a>
  </header-field>
</template>

<script>
import Utils from '../common/Utils'
import HeaderField from './HeaderField'

export default {

  data: () => ({
  }),

  props: ['label', 'url', 'text'],

  components: {
    HeaderField
  },

  methods: {
    // Handle link clicked.
    onLinkClicked: function () {
      Utils.routeTo(this, this.url)
    }
  }
}
</script>

<style scoped>
</style>
