<template>
  <div class="date-time-picker">
    <v-text-field label="Choose date / time" v-model="formattedValue" readonly
      hide-details>
    </v-text-field>
    <v-menu class="calendar-icon" lazy
      v-model="calendarMenu" offset-y full-width max-width="290px">
      <v-icon slot="activator" fa>calendar</v-icon>
      <v-date-picker v-model="selectedDate" no-title scrollable actions>
      </v-date-picker>
    </v-menu>
    <v-menu class="time-icon" lazy :close-on-content-click="false"
      v-model="timeMenu" offset-y full-width max-width="290px">
      <v-icon slot="activator" fa>clock-o</v-icon>
      <v-time-picker v-model="selectedTime"></v-time-picker>
    </v-menu>
    <v-btn icon small flat class="delete-icon" @click.native="onClear">
      <v-icon fa>remove</v-icon>
    </v-btn>
  </div>
</template>

<script>
import moment from 'moment'

export default {

  data: () => ({
    selectedDate: null,
    selectedTime: null,
    calendarMenu: null,
    timeMenu: null
  }),

  props: ['value'],

  computed: {
    formattedValue: function () {
      if (this.selectedDate) {
        return this.selectedDate + ' ' + (this.selectedTime || '')
      }
      return null
    }
  },

  created: function () {
    this.updateFromValue(this.value)
  },

  watch: {
    // Handle value being set externally.
    value: function (value) {
      this.updateFromValue(value)
    },
    selectedTime: function (value) {
      if (value && !this.selectedDate) {
        this.selectedDate = moment().startOf('day').format('YYYY-MM-DD')
      }
    },
    formattedValue: function (value) {
      let updated = new Date(moment(value, 'YYYY-MM-DD hh:mma'))
      this.$emit('input', updated)
    }
  },

  methods: {
    updateFromValue: function () {
      console.log('update from ' + this.value)
      if (this.value) {
        this.selectedTime = moment(this.value).format('h:mma')
        this.selectedDate = moment(this.value).startOf('day')
      } else {
        this.selectedTime = null
        this.selectedDate = null
      }
    },

    // Clear the current data.
    onClear: function () {
      this.selectedDate = null
      this.selectedTime = null
    }
  }
}
</script>

<style scoped>
.date-time-picker {
  position: relative;
}
.calendar-icon {
  position: absolute;
  right: 52px;
  bottom: 22px;
}
.time-icon {
  position: absolute;
  right: 32px;
  bottom: 22px;
}
.delete-icon {
  position: absolute;
  margin: 0px;
  right: 0px;
  bottom: 17px;
}
</style>
