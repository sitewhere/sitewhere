<template>
  <v-dialog v-model="visible" persistent :width="width">
    <v-card >
      <v-card-row class="confirm-dialog">
        <span class="blue darken-2 white--text">{{title}}</span>
      </v-card-row>
      <v-card-row slot="error" v-if="error">
        <v-alert class="ma-0" error v-bind:value="true" style="width: 100%">
          {{error}}
        </v-alert>
      </v-card-row>
      <v-card-row>
        <slot>
            <div>Your content goes here!</div>
        </slot>
      </v-card-row>
      <v-card-row actions>
        <v-btn class="grey--text darken-1" flat="flat" @click.native="onCancelClicked">Cancel</v-btn>
        <v-btn class="blue--text darken-1" flat="flat" @click.native="onActionClicked">{{ text }}</v-btn>
      </v-card-row>
    </v-card>
  </v-dialog>
</template>

<script>
export default {

  data: () => ({
    visible: false
  }),

  props: ['title', 'width', 'buttonText', 'error'],

  computed: {
    // Use fallback for button text.
    text: function () {
      return (this.buttonText) ? this.buttonText : 'Ok'
    }
  },

  methods: {
    // Called to open the dialog.
    openDialog: function () {
      this.$data.visible = true
    },

    // Called to open the dialog.
    closeDialog: function () {
      this.$data.visible = false
    },

    // Called to show an error message.
    showError: function (error) {
      this.$data.error = error
    },

    // Called after action button is clicked.
    onActionClicked: function (e) {
      this.$emit('action')
    },

    // Called after cancel button is clicked.
    onCancelClicked: function (e) {
      this.$data.visible = false
    }
  }
}
</script>

<style scoped>
.confirm-dialog span {
  padding: 8px 12px;
  font-size: 22px;
  width: 100%;
}
</style>
