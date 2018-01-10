<template>
  <v-dialog v-model="visible" persistent :width="width">
    <v-card >
      <v-toolbar dense flat card dark color="primary">
        <v-toolbar-title>{{title}}</v-toolbar-title>
      </v-toolbar>
      <v-alert class="ma-0" error v-bind:value="true" style="width: 100%" v-if="error">
        {{ error.message }}
      </v-alert>
      <v-card-text>
        <slot>
            <div>Your content goes here!</div>
        </slot>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn class="grey--text darken-1" flat="flat" @click.native="onCancelClicked">Cancel</v-btn>
        <v-btn class="blue--text darken-1" flat="flat" @click.native="onActionClicked">{{ text }}</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {

  data: () => ({
    visible: false,
    error: null
  }),

  props: ['title', 'width', 'buttonText'],

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
.confirm-dialog {
  padding: 10px;
  font-size: 26px;
  width: 100%;
}
</style>
