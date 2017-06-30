<template>
  <v-menu offset-y top :close-on-content-click="false" v-model="menu">
    <v-btn :style="{ 'background-color' : currentColor, 'color': '#fff' }" slot="activator">{{ text }}</v-btn>
    <chrome :value="colors" @input="onColorChosen"></chrome>
  </v-menu>
</template>

<script>
import {Chrome} from 'vue-color'

export default {

  data: () => ({
    menu: null,
    colors: {
      hex: '#194d33'
    }
  }),

  components: {
    Chrome
  },

  computed: {
    currentColor: function () {
      return (this.color) ? this.color : this.colors.hex
    }
  },

  props: ['color', 'text'],

  methods: {
    // Called when a color is chosen.
    onColorChosen: function (val) {
      this.colors.hex = val.hex
      this.$emit('colorChanged', val.hex)
    }
  }
}
</script>

<style scoped>
</style>
