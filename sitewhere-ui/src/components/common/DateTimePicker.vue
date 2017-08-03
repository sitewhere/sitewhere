<template>
  <div class="date-time-picker mt-3 mb-4">
    <v-icon fa class="mr-3 ml-1">calendar</v-icon>
    <span class="date-input">
      <flat-pickr v-model="date" :placeholder="label"
        :config="dateTimeConfig" :required="true"
        name="date">
      </flat-pickr>
    </span>
    <v-btn icon small flat class="delete-icon ma-0" @click.native="onClear">
      <v-icon fa>remove</v-icon>
    </v-btn>
  </div>
</template>

<script>
import FlatPickr from 'vue-flatpickr-component'

export default {

  data: () => ({
    date: null,
    formattedValue: null,
    menu: null,
    dateTimeConfig: {
      wrap: false,
      enableTime: true,
      altFormat: 'Y-m-d H:iK',
      altInput: true,
      dateFormat: 'Y-m-d H:i:S'
    }
  }),

  components: {
    FlatPickr
  },

  props: ['value', 'label'],

  watch: {
    value: function (value) {
      this.$data.date = value
    },
    date: function (value) {
      this.$emit('input', value)
    }
  },

  methods: {
    onClear: function () {
      this.$data.date = null
    }
  }
}
</script>

<style scoped>
.date-input {
  color: #333;
  font-size: 16px;
  border-bottom: 1px solid #999;
}
</style>
