<template>
  <v-dialog v-model="visible" persistent :width="width">
    <v-card >
      <v-card-row class="delete-dialog">
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
        <v-btn class="blue--text darken-1" flat="flat" @click.native="onDeleteClicked">Delete</v-btn>
      </v-card-row>
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
.delete-dialog span {
  padding: 8px 12px;
  font-size: 22px;
  width: 100%;
}
</style>
