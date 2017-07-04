<template>
  <v-menu offset-y top :close-on-content-click="false" v-model="menu">
    <v-btn :style="{ 'background-color' : currentColor, 'color': '#fff' }" slot="activator">{{ text }}</v-btn>
    <chrome :value="chromeColor" @input="onColorChosen"></chrome>
  </v-menu>
</template>

<script>
import {Chrome} from 'vue-color'

export default {

  data: () => ({
    menu: null,
    updatedColor: null
  }),

  components: {
    Chrome
  },

  computed: {
    chromeColor: function () {
      return {
        hex: this.currentColor
      }
    },
    currentColor: function () {
      return this.updatedColor || this.color
    }
  },

  props: ['color', 'text'],

  methods: {
    // Called when a color is chosen.
    onColorChosen: function (val) {
      this.updatedColor = val.hex
      this.$emit('colorChanged', val.hex)
      this.$emit('opacityChanged', val.a)
    }
  }
}
</script>

<style scoped>
</style>
