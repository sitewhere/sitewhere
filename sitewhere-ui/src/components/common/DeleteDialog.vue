<template>
  <v-dialog v-model="visible" persistent :width="width">
    <v-card >
      <div class="delete-dialog blue darken-2 white--text title">
        {{title}}
      </div>
      <v-alert class="ma-0" error v-bind:value="true" style="width: 100%" slot="error" v-if="error">
        {{error}}
      </v-alert>
      <v-card-text class="pa-0">
        <slot>
            <div>Your content goes here!</div>
        </slot>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn class="grey--text darken-1" flat="flat" @click.native="onCancelClicked">Cancel</v-btn>
        <v-btn class="blue--text darken-1" flat="flat" @click.native="onDeleteClicked">Delete</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {

  data: () => ({
    visible: false
  }),

  props: ['title', 'width', 'error'],

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

    // Called after create button is clicked.
    onDeleteClicked: function (e) {
      this.$emit('delete')
    },

    // Called after cancel button is clicked.
    onCancelClicked: function (e) {
      this.$data.visible = false
    }
  }
}
</script>

<style scoped>
.delete-dialog {
  padding: 10px;
  width: 100%;
}
</style>
